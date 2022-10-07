package com.cometproject.server.game.players.components;

import com.cometproject.api.game.catalog.marketplace.IMarketPlaceOffer;
import com.cometproject.api.game.furniture.types.IGiftData;
import com.cometproject.api.game.furniture.types.LimitedEditionItem;
import com.cometproject.api.game.furniture.types.SongItem;
import com.cometproject.api.game.players.data.components.PlayerInventory;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.marketplace.MarketPlace;
import com.cometproject.server.game.catalog.marketplace.MarketPlaceState;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.items.music.SongItemData;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.game.players.components.types.inventory.InventoryItemSnapshot;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.players.types.PlayerComponent;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.wired.WiredRewardMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.BadgeInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.InventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.RemoveObjectFromInventoryMessageComposer;
import com.cometproject.server.storage.queries.achievements.PlayerAchievementDao;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.storage.queries.player.inventory.InventoryDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gnu.trove.set.hash.THashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class InventoryComponent extends PlayerComponent implements PlayerInventory {
    private Map<Long, PlayerItem> inventoryItems;

    private Map<String, Integer> badges;

    private boolean itemsLoaded = false;

    private final THashSet<IMarketPlaceOffer> items;

    private boolean isViewingInventory = false;
    private int viewingInventoryUser = 0;

    private int equippedEffect = -1;
    private Set<Integer> effects;

    private final Logger log = LogManager.getLogger(InventoryComponent.class.getName());

    public InventoryComponent(Player player) {
        super(player);

        this.inventoryItems = new ConcurrentHashMap<>();

        this.badges = new ConcurrentHashMap<>();

        this.loadEffects();
        this.loadBadges();

        // MarketPlace Own Offers
        this.items = MarketPlace.getOwnOffers(this.getPlayer());
    }

    public void addMarketplaceOffer(IMarketPlaceOffer marketPlaceOffer) {
        this.items.add(marketPlaceOffer);
    }

    public void removeMarketplaceOffer(IMarketPlaceOffer marketPlaceOffer) {
        this.items.remove(marketPlaceOffer);
    }

    public THashSet<IMarketPlaceOffer> getMarketplaceItems() {
        return this.items;
    }

    public int getSoldPriceTotal() {
        int i = 0;

        for (IMarketPlaceOffer offer : this.items) {
            if (offer.getState().equals(MarketPlaceState.SOLD)) {
                i += offer.getPrice();
            }
        }

        return i;
    }

    public IMarketPlaceOffer getOffer(int id) {
        synchronized (this.items) {
            for (IMarketPlaceOffer offer : this.items) {
                if (offer.getOfferId() == id)
                    return offer;
            }
        }

        return null;
    }

    public void loadEffects() {
        if (this.effects != null) {
            this.effects.clear();
            this.effects = null;
        }

        this.effects = PlayerDao.getEffects(this.getPlayer().getId());
    }

    @Override
    public void loadItems(int userId) {
        this.itemsLoaded = true;

        if (this.inventoryItems.size() >= 1) {
            this.inventoryItems.clear();
        }

        this.isViewingInventory = userId != 0;
        this.viewingInventoryUser = userId;

        try {
            final Map<Long, PlayerItem> inventoryItems = InventoryDao.getInventoryByPlayerId(userId == 0 ? this.getPlayer().getId() : userId);

            this.inventoryItems.putAll(inventoryItems);

            this.getPlayer().flush();
        } catch (Exception e) {
            log.error("Error while loading user inventory", e);
        }
    }

    @Override
    public void loadBadges() {
        try {
            // TODO: redo this so we can seperate achievement badges to other badges. Maybe a "badge type" or something.
            this.badges = InventoryDao.getBadgesByPlayerId(this.getPlayer().getId());
        } catch (Exception e) {
            log.error("Error while loading user badges");
        }
    }

    @Override
    public void addBadge(String code, boolean insert) {
        this.addBadge(code, insert, true);
    }

    public void addBadge(String code, boolean insert, boolean sendAlert) {
        this.addBadge(code, insert, sendAlert, false);
    }

    @Override
    public void addBadge(String code, boolean insert, boolean sendAlert, boolean isAchievement) {
        if (!badges.containsKey(code)) {
            if (insert) {
                InventoryDao.addBadge(code, this.getPlayer().getId());
            }

            this.badges.put(code, 0);

            this.getPlayer().getSession().
                    send(new BadgeInventoryMessageComposer(this.getBadges())).
                    send(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                        put(4, Lists.newArrayList(1));
                    }}));

            if (sendAlert) {
                    this.getPlayer().getSession().send(new WiredRewardMessageComposer(7));
            }

            this.getPlayer().flush();
        }
    }

    public void send() {
        if (this.inventoryItems.size() == 0) {
            this.getPlayer().getSession().send(new InventoryMessageComposer(1, 0, Maps.newHashMap()));
            return;
        }

        final int totalPages = (int) Math.ceil(this.inventoryItems.size() / InventoryMessageComposer.ITEMS_PER_PAGE);

        int totalSent = 0;
        int currentPage = 0;
        Map<Long, PlayerItem> inventoryItems = new HashMap<>();

        for (final Map.Entry<Long, PlayerItem> item : this.getInventoryItems().entrySet()) {
            totalSent++;
            inventoryItems.put(item.getKey(), item.getValue());

            if (inventoryItems.size() >= InventoryMessageComposer.ITEMS_PER_PAGE || totalSent == this.inventoryItems.size()) {
                this.getPlayer().getSession().send(new InventoryMessageComposer(totalPages + 1, currentPage, inventoryItems));

                inventoryItems = new HashMap<>();
                currentPage++;
            }
        }
    }

    @Override
    public boolean hasBadge(String code) {
        return this.badges.containsKey(code);
    }

    @Override
    public void removeBadge(String code, boolean delete) {
        this.removeBadge(code, delete, true, true);
    }

    @Override
    public void removeBadge(String code, boolean delete, boolean sendAlert, boolean sendUpdate) {
        if (badges.containsKey(code)) {
            if (delete) {
                InventoryDao.removeBadge(code, this.getPlayer().getId());
            }

            this.badges.remove(code);

            if (sendAlert) {
                this.getPlayer().getSession().send(new AlertMessageComposer(Locale.get("badge.deleted")));
            }

            this.getPlayer().getSession().send(new BadgeInventoryMessageComposer(this.getBadges()));

            this.getPlayer().flush();
        }
    }

    @Override
    public void achievementBadge(String achievement, int level) {
        final String oldBadge = achievement + (level - 1);
        final String newBadge = achievement + level;

        if(this.badges.containsKey(newBadge)) return;

        boolean isUpdated = false;

        if (this.badges.containsKey(oldBadge)) {
            this.removeBadge(oldBadge, false, false, false);

            PlayerAchievementDao.updateBadge(oldBadge, newBadge, this.getPlayer().getId());
            isUpdated = true;
        }

        this.addBadge(newBadge, !isUpdated, false, true);
    }

    @Override
    public void resetBadgeSlots() {
        for (Map.Entry<String, Integer> badge : this.badges.entrySet()) {
            if (badge.getValue() != 0) {
                this.badges.replace(badge.getKey(), 0);
            }
        }

        this.getPlayer().flush();
    }

    @Override
    public String[] equippedBadges() {
        final String[] badges = new String[11];

        // Map<String, Integer> badges = new ConcurrentHashMap<>();

        for (Map.Entry<String, Integer> badge : this.getBadges().entrySet()) {
            if (badge.getValue() > 0)
                badges[badge.getValue()] = badge.getKey();
            //badges.put(badge.getKey(), badge.getValue());
        }

        return badges;
        //return Collections.sortbadges;
    }

    @Override
    public PlayerItem add(long id, int itemId, String extraData, IGiftData giftData, LimitedEditionItem limitedEditionItem) {
        final PlayerItem item = new InventoryItem(id, itemId, extraData, giftData, limitedEditionItem);

        this.inventoryItems.put(id, item);

        this.getPlayer().flush();

        return item;
    }

    @Override
    public List<SongItem> getSongs() {
        final List<SongItem> songItems = Lists.newArrayList();

        for (final PlayerItem inventoryItem : this.inventoryItems.values()) {
            if (inventoryItem.getDefinition().isSong()) {
                songItems.add(new SongItemData((InventoryItemSnapshot) inventoryItem.createSnapshot(), inventoryItem.getDefinition().getSongId()));
            }
        }

        return songItems;
    }

    public boolean hasBaseItem(long itemId) {
        for (final PlayerItem inventoryItem : this.inventoryItems.values()) {
            if (inventoryItem.getDefinition().getId() == itemId) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Nullable
    public PlayerItem getFirstItemByBaseItemId(long id) {
        for (final PlayerItem inventoryItem : this.inventoryItems.values()) {
            if (inventoryItem.getDefinition().getId() == id) {
                return inventoryItem;
            }
        }

        return null;
    }

    @Override
    public void add(long id, int itemId, String extraData, LimitedEditionItem limitedEditionItem) {
        add(id, itemId, extraData, null, limitedEditionItem);
    }

    @Override
    public void addItem(PlayerItem item) {
        this.inventoryItems.put(item.getId(), item);

        this.getPlayer().flush();
    }

    @Override
    public void removeItem(PlayerItem item) {
        this.removeItem(item.getId());
    }

    @Override
    public void removeItem(long itemId) {
        this.inventoryItems.remove(itemId);
        this.getPlayer().getSession().send(new RemoveObjectFromInventoryMessageComposer(ItemManager.getInstance().getItemVirtualId(itemId)));

        this.getPlayer().flush();
    }

    @Override
    public void removeItems(long itemId) {
        this.inventoryItems.entrySet()
                .stream()
                .filter(map -> map.getValue().getDefinition().getId() == itemId)
                .forEach(item -> {
                    this.inventoryItems.remove(item.getKey());
                    this.getPlayer().getSession().send(new RemoveObjectFromInventoryMessageComposer(ItemManager.getInstance().getItemVirtualId(item.getValue().getId())));
                });

        this.getPlayer().flush();
    }

    @Override
    public boolean hasItem(long id) {
        return this.getInventoryItems().containsKey(id);
    }

    @Override
    public PlayerItem getItem(long id) {
        return this.inventoryItems.get(id);
    }

    @Override
    public void dispose() {
        for (final PlayerItem inventoryItem : this.inventoryItems.values()) {
            ItemManager.getInstance().disposeItemVirtualId(inventoryItem.getId());
        }

        this.inventoryItems.clear();
        this.inventoryItems = null;

        this.effects.clear();
        this.effects = null;

        this.badges.clear();
        this.badges = null;
    }

    @Override
    public int getTotalSize() {
        return this.inventoryItems.size();
    }

    @Override
    public Map<Long, PlayerItem> getInventoryItems() {
        return this.inventoryItems;
    }

    @Override
    public Map<String, Integer> getBadges() {
        return this.badges;
    }

    @Override
    public boolean hasEffect(int effectId) {
        return this.effects.contains(effectId);
    }

    @Override
    public Set<Integer> getEffects() {
        return this.effects;
    }

    @Override
    public boolean itemsLoaded() {
        return itemsLoaded;
    }

    public int getEquippedEffect() {
        return this.equippedEffect;
    }

    public void setEquippedEffect(int equippedEffect) {
        this.equippedEffect = equippedEffect;
    }

    public boolean isViewingInventory() {
        return this.isViewingInventory;
    }

    public int viewingInventoryUserId() {
        return this.viewingInventoryUser;
    }

    @Override
    public boolean hasNonDeleteableItems() {
        return this.inventoryItems.values().stream().anyMatch(item -> !item.getDefinition().canDelete());
    }

    @Override
    public void clearItems() {
        final Stream<PlayerItem> nonDeleteableItems = this.inventoryItems.values().stream().filter(item -> !item.getDefinition().canDelete());

        this.inventoryItems = new HashMap<>();

        nonDeleteableItems.forEach(item -> this.inventoryItems.put(item.getId(), item));
    }
}
