package com.cometproject.server.game.catalog.types;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.items.ItemManager;

import java.util.ArrayList;
import java.util.List;


public class CatalogItem implements ICatalogItem {
    /**
     * The ID of the catalog item
     */
    private final int id;

    /**
     * The ID of the item definition
     */
    private final String itemId;

    /**
     * The name of item which will be displayed in the catalog
     */
    private final String displayName;

    /**
     * The coin cost of the item
     */
    private final int costCredits;

    /**
     * The duckets cost of the item
     */
    private final int costActivityPoints;

    /**
     * The diamonds cost of the item
     */
    private final int costDiamonds;

    /**
     * The seasonal currency cost of the item
     */
    private final int costSeasonal;

    /**
     * The amount of items you get if you purchase this
     */
    private final int amount;

    /**
     * Is this item only available to VIP members?
     */
    private final boolean vip;

    /**
     * The items (if this is a bundle)
     */
    private List<ICatalogBundledItem> items;

    /**
     * If this item is limited edition, how many items are available
     */
    private final int limitedTotal;

    /**
     * If this item is limited edition, how many items have been sold
     */
    private int limitedSells;

    /**
     * Allow this item to be sold
     */
    private final boolean allowOffer;

    /**
     * Badge ID that's bundled with this item (if any)
     */
    private final String badgeId;

    /**
     * Item extra-data presets (once purchased, this preset will be applied to the item)
     */
    private final String presetData;

    /**
     * The catalog page ID
     */
    private final int pageId;

    public CatalogItem(int id, String itemId, String displayName, int costCredits, int costActivityPoints, int costDiamonds, int costSeasonal, int amount, boolean vip, int limitedTotal, int limitedSells, boolean allowOffer, String badgeId, String presetData, int pageId) {
        this(id, itemId, null, displayName, costCredits, costActivityPoints, costDiamonds, costSeasonal, amount, vip, limitedTotal, limitedSells, allowOffer, badgeId, presetData, pageId);
    }

    public CatalogItem(int id, String itemId, List<ICatalogBundledItem> bundledItems, String displayName, int costCredits, int costActivityPoints, int costDiamonds, int costSeasonal, int amount, boolean vip, int limitedTotal, int limitedSells, boolean allowOffer, String badgeId, String presetData, int pageId) {
        this.id = id;
        this.itemId = itemId;
        this.displayName = displayName;
        this.costCredits = costCredits;
        this.costActivityPoints = costActivityPoints;
        this.costDiamonds = costDiamonds;
        this.costSeasonal = costSeasonal;
        this.amount = amount;
        this.vip = vip;
        this.limitedTotal = limitedTotal;
        this.limitedSells = limitedSells;
        this.allowOffer = allowOffer;
        this.badgeId = badgeId;
        this.presetData = presetData;
        this.pageId = pageId;

        this.items = bundledItems != null ? bundledItems : new ArrayList<>();

        if (items.size() == 0) {
            if (!this.itemId.equals("-1")) {
                if (bundledItems != null) {
                    items = bundledItems;
                } else {

                    if (itemId.contains(",")) {
                        final String[] split = itemId.replace("\n", "").split(",");

                        for (final String str : split) {
                            if (!str.equals("")) {
                                final String[] parts = str.split(":");
                                if (parts.length != 3) continue;

                                try {
                                    final int aItemId = Integer.parseInt(parts[0]);
                                    final int aAmount = Integer.parseInt(parts[1]);
                                    final String aPresetData = parts[2];

                                    this.items.add(new CatalogBundledItem(aPresetData, aAmount, aItemId));
                                } catch (Exception ignored) {
                                    Comet.getServer().getLogger().warn("Invalid item data for catalog item: " + this.id);
                                }
                            }
                        }
                    } else {
                        this.items.add(new CatalogBundledItem(this.presetData, this.amount, Integer.parseInt(this.itemId)));
                    }
                }
            }

            if (this.getItems().size() == 0) return;

            List<ICatalogBundledItem> itemsToRemove = new ArrayList<>();

            for (ICatalogBundledItem catalogBundledItem : this.items) {
                final FurnitureDefinition itemDefinition = ItemManager.getInstance().getDefinition(catalogBundledItem.getItemId());

                if (itemDefinition == null) {
                    itemsToRemove.add(catalogBundledItem);
                }
            }

            this.items.removeAll(itemsToRemove);
            itemsToRemove.clear();

            if (this.items.size() == 0) {
                return;
            }

            if (ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId()) == null) return;
            int offerId = ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId()).getOfferId();

            if (!CatalogManager.getInstance().getCatalogOffers().containsKey(offerId)) {
                CatalogManager.getInstance().getCatalogOffers().put(offerId, new CatalogOffer(offerId, this.getPageId(), this.getId()));
            }
        }
    }

    @Override
    public void compose(IComposer msg) {
        final FurnitureDefinition firstItem = this.itemId.equals("-1") ? null : ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId());

        msg.writeInt(this.getId());
        msg.writeString(this.getDisplayName());
        msg.writeBoolean(false);

        msg.writeInt(this.getCostCredits());

        if (this.getCostDiamonds() > 0) {
            msg.writeInt(this.getCostDiamonds());
            msg.writeInt(105);
        } else if (this.getCostActivityPoints() > 0) {
            msg.writeInt(this.getCostActivityPoints());
            msg.writeInt(0);
        } else if(this.getCostSeasonal() > 0) {
            msg.writeInt(this.getCostSeasonal());
            msg.writeInt(CometSettings.seasonalNumber);
        } else {
            msg.writeInt(0);
            msg.writeInt(0);
        }

        msg.writeBoolean(firstItem != null && firstItem.canGift());

        if (!this.hasBadge()) {
            msg.writeInt(this.getItems().size());
        } else {
            msg.writeInt(this.isBadgeOnly() ? 1 : this.getItems().size() + 1);
            msg.writeString("b");
            msg.writeString(this.hasBadge() ? this.getBadgeId().split(",")[0] : "");
        }

        if (!this.isBadgeOnly()) {
            for (ICatalogBundledItem bundledItem : this.getItems()) {
                FurnitureDefinition def = ItemManager.getInstance().getDefinition(bundledItem.getItemId());

                    msg.writeString(def.getType());
                    msg.writeInt(def.getSpriteId());

                    if (this.getDisplayName().contains("wallpaper_single") || this.getDisplayName().contains("floor_single") || this.getDisplayName().contains("landscape_single")) {
                        msg.writeString(this.getDisplayName().split("_")[2]);
                    } else {
                        msg.writeString(bundledItem.getPresetData());
                    }

                    msg.writeInt(bundledItem.getAmount());

                    msg.writeBoolean(this.getLimitedTotal() != 0);

                    if (this.getLimitedTotal() > 0) {
                        msg.writeInt(this.getLimitedTotal());
                        msg.writeInt(this.getLimitedTotal() - this.getLimitedSells());
                    }
            }
        }

        msg.writeInt(0); // club level
        msg.writeBoolean(!(this.getLimitedTotal() > 0) && this.allowOffer());
        msg.writeBoolean(false); // unknown
        msg.writeString("");
    }

    @Override
    public void composeClubPresents(IComposer msg) {
        final FurnitureDefinition firstItem = this.itemId.equals("-1") ? null : ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId());
        msg.writeInt(firstItem.getSpriteId());
        msg.writeString(firstItem.getItemName());
        msg.writeBoolean(false);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeBoolean(false);
        msg.writeInt(1);

        msg.writeString(firstItem.getType());
        msg.writeInt(firstItem.getSpriteId());
        msg.writeString("");
        msg.writeInt(1);
        msg.writeBoolean(false);

        msg.writeInt(0); // club level
        msg.writeBoolean(false);
        msg.writeBoolean(false);
        msg.writeString("");
    }

    @Override
    public void serializeAvailability(IComposer msg) {
        final FurnitureDefinition firstItem = this.itemId.equals("-1") ? null : ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId());
        msg.writeInt(firstItem.getSpriteId());
        msg.writeBoolean(true);
        msg.writeInt(0);
        msg.writeBoolean(true);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public List<ICatalogBundledItem> getItems() {
        return this.items;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getCostCredits() {
        return costCredits;
    }

    @Override
    public int getCostActivityPoints() {
        return costActivityPoints;
    }

    @Override
    public int getCostDiamonds() {
        return costDiamonds;
    }

    @Override
    public int getCostSeasonal() {
        return costSeasonal;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isVip() {
        return vip;
    }

    @Override
    public int getLimitedTotal() {
        return this.limitedTotal;
    }

    @Override
    public int getLimitedSells() {
        return this.limitedSells;
    }

    @Override
    public boolean allowOffer() {
        return this.allowOffer;
    }

    @Override
    public void increaseLimitedSells(int amount) {
        this.limitedSells += amount;
    }

    @Override
    public boolean hasBadge() {
        if(this.badgeId == null) {
            return false;
        }

        return !(this.badgeId.isEmpty());
    }

    @Override
    public boolean isBadgeOnly() {
        return this.items.size() == 0 && this.hasBadge();
    }

    @Override
    public String getBadgeId() {
        return this.badgeId;
    }

    @Override
    public String getPresetData() {
        return presetData;
    }

    @Override
    public int getPageId() {
        return pageId;
    }

    @Override
    public boolean isZeroCredits() {
        return this.costCredits <= 0;
    }

    @Override
    public boolean isZeroActivityPoints() {
        return this.costActivityPoints <= 0;
    }

    @Override
    public boolean isZeroDiamonds() {
        return this.costDiamonds <= 0;
    }

    @Override
    public boolean isZeroSeasonal() {
        return this.costSeasonal <= 0;
    }
}
