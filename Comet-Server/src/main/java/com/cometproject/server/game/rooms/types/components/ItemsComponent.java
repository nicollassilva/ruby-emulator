package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.ItemType;
import com.cometproject.api.game.furniture.types.LimitedEditionItem;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.floor.DiceFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.GiftFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.MagicStackFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.SoundMachineFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.freeze.FreezeTileFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonNewPuzzleBox;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.cometproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveWallItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.SendFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.SendWallItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.cache.objects.items.FloorItemDataObject;
import com.cometproject.server.storage.cache.objects.items.WallItemDataObject;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import com.cometproject.storage.api.StorageContext;
import com.cometproject.storage.api.data.Data;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ItemsComponent {

    private static final int MAX_FOOTBALLS = 15;

    private final Logger log;
    private final Map<Long, RoomItemFloor> floorItems = new ConcurrentHashMap<>();
    private final Map<Long, RoomItemWall> wallItems = new ConcurrentHashMap<>();
    private final Map<Integer, String> itemOwners = new ConcurrentHashMap<>();
    private final Room room;
    private final Map<Class<? extends RoomItemFloor>, Set<Long>> itemClassIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> itemInteractionIndex = new ConcurrentHashMap<>();
    private long soundMachineId = 0;
    private long moodlightId;

    private long itemsCount = 0;
    private int blackHolesCount = 0;
    private int iceSkateCount = 0;
    private int snowbSlopeCount = 0;
    private int rollerSkateCount = 0;
    private int postItCount = 0;

    public ItemsComponent(Room room) {
        this.room = room;
        this.log = LogManager.getLogger("Room Items Component [" + room.getData().getName() + "]");
        this.itemClassIndex.put(HighscoreFloorItem.class, Sets.newConcurrentHashSet());
        this.loadItems();
    }

    public int getBlackHolesCount() {
        return this.blackHolesCount;
    }

    public int getRollerSkateCount() {
        return this.rollerSkateCount;
    }

    public int getPostItCount() {
        return this.postItCount;
    }

    public int getSnowBoardSlopeCount() {
        return this.snowbSlopeCount;
    }

    public int getIceSkateCount() {
        return this.iceSkateCount;
    }

    public long getItemsCount() {
        return this.itemsCount;
    }

    public void setItemsCount(long value) {
        this.itemsCount = value;
    }

    public void increaseItemsCount() {
        this.itemsCount++;
    }

    public void decreaseItemsCount() {
        this.itemsCount--;
    }

    private void loadItems() {
        if (room.getCachedData() != null) {
            for (final FloorItemDataObject floorItemDataObject : room.getCachedData().getFloorItems()) {
                final RoomItemData data = new RoomItemData(floorItemDataObject.getId(),
                        floorItemDataObject.getItemDefinitionId(),
                        floorItemDataObject.getOwner(),
                        floorItemDataObject.getOwnerName(),
                        floorItemDataObject.getPosition(),
                        floorItemDataObject.getRotation(),
                        floorItemDataObject.getData(), "", floorItemDataObject.getLimitedEditionItemData());

                this.floorItems.put(floorItemDataObject.getId(), RoomItemFactory.createFloor(
                        data, room, ItemManager.getInstance().getDefinition(floorItemDataObject.getItemDefinitionId())));
            }

            for (final WallItemDataObject wallItemDataObject : room.getCachedData().getWallItems()) {
                final RoomItemData data = new RoomItemData(wallItemDataObject.getId(),
                        wallItemDataObject.getItemDefinitionId(),
                        wallItemDataObject.getOwner(),
                        wallItemDataObject.getOwnerName(),
                        new Position(),
                        0,
                        wallItemDataObject.getData(), wallItemDataObject.getWallPosition(),
                        wallItemDataObject.getLimitedEditionItemData());

                this.wallItems.put(wallItemDataObject.getId(), RoomItemFactory.createWall(data, room,
                        ItemManager.getInstance().getDefinition(wallItemDataObject.getItemDefinitionId())));
            }
        } else {
            final Data<List<RoomItemData>> items = Data.createEmpty();

            StorageContext.getCurrentContext().getRoomItemRepository().getItemsByRoomId(this.room.getId(), items::set);

            if (items.has()) {
                for (final RoomItemData roomItem : items.get()) {
                    final FurnitureDefinition itemDefinition = ItemManager.getInstance().getDefinition(roomItem.getItemId());

                    if (itemDefinition == null) continue;

                    if (itemDefinition.getItemType() == ItemType.FLOOR)
                        this.floorItems.put(roomItem.getId(), RoomItemFactory.createFloor(roomItem, room, itemDefinition));
                    else if (itemDefinition.getItemType() == ItemType.WALL)
                        this.wallItems.put(roomItem.getId(), RoomItemFactory.createWall(roomItem, room, itemDefinition));
                }
            }
        }

        this.indexFloorItems();
        this.indexWallItems();
    }

    private void indexFloorItems() {
        for (final RoomItemFloor floorItem : this.floorItems.values()) {
            if (floorItem instanceof SoundMachineFloorItem) {
                soundMachineId = floorItem.getId();
            }

            if (floorItem.getDefinition().getInteraction().equals("blackhole")) {
                this.blackHolesCount++;
            }

            if (floorItem.getDefinition().getInteraction().equals("iceskate")) {
                this.iceSkateCount++;
            }

            if (floorItem.getDefinition().getInteraction().equals("snowb_slope")) {
                this.snowbSlopeCount++;
            }

            if (floorItem.getDefinition().getInteraction().equals("rollerskate")) {
                this.rollerSkateCount++;
            }

            if (floorItem instanceof WiredFloorItem) {
                final List<Long> itemsToRemove = Lists.newArrayList();

                for (final long selectedItemId : ((WiredFloorItem) floorItem).getWiredData().getSelectedIds()) {
                    final RoomItemFloor floor = this.getFloorItem(selectedItemId);

                    if (floor != null) {
                        floor.getWiredItems().add(floorItem.getId());
                    } else {
                        itemsToRemove.add(selectedItemId);
                    }
                }

                for (final long itemId : itemsToRemove) {
                    ((WiredFloorItem) floorItem).getWiredData().getSelectedIds().remove(itemId);
                }

                itemsToRemove.clear();
            }

            this.indexFloorItem(floorItem);
        }
    }

    private void indexWallItems() {
        for (final RoomItemWall floorItem : this.wallItems.values()) {
            this.indexWallItem(floorItem);
        }
    }

    public void onLoaded() {
        for (final RoomItemFloor floorItem : floorItems.values()) {
            floorItem.onLoad();
        }

        for (final RoomItemWall wallItem : wallItems.values()) {

            if (wallItem.getDefinition().getInteraction().equals("postit")) {
                this.postItCount++;
            }

            wallItem.onLoad();
        }
    }

    public void dispose() {
        for (final RoomItemFloor floorItem : floorItems.values()) {
            ItemManager.getInstance().disposeItemVirtualId(floorItem.getId());
            floorItem.onUnload();
        }

        for (final RoomItemWall wallItem : wallItems.values()) {
            ItemManager.getInstance().disposeItemVirtualId(wallItem.getId());
            wallItem.onUnload();
        }

        this.floorItems.clear();
        this.wallItems.clear();

        for (final Set<Long> itemIds : this.itemClassIndex.values()) {
            itemIds.clear();
        }

        for (final Set<Long> itemInteractions : itemInteractionIndex.values()) {
            itemInteractions.clear();
        }

        this.itemOwners.clear();
        this.itemInteractionIndex.clear();
        this.itemClassIndex.clear();
    }

    public boolean setMoodlight(long moodlight) {
        if (this.moodlightId != 0)
            return false;

        this.moodlightId = moodlight;
        return true;
    }

    public void removeMoodlight() {
        if (this.moodlightId == 0) {
            return;
        }

        this.moodlightId = 0;
    }

    private boolean moveFloorItemAfter(RoomItemFloor item, Position newPosition, double height, int rotation, boolean save) {
        final List<RoomItemFloor> floorItemsAt = this.getItemsOnSquare(newPosition.getX(), newPosition.getY());

        for (final RoomItemFloor stackItem : floorItemsAt) {
            if (item.getId() != stackItem.getId()) {

                stackItem.onItemAddedToStack(item);
            }
        }

        item.onPositionChanged(newPosition);

        final List<RoomEntity> affectEntities0 = room.getEntities().getEntitiesAt(item.getPosition());

        for (RoomEntity entity0 : affectEntities0) {
            item.onEntityStepOff(entity0);
        }

        final List<Position> tilesToUpdate = new ArrayList<>();

        tilesToUpdate.add(new Position(item.getPosition().getX(), item.getPosition().getY()));
        tilesToUpdate.add(new Position(newPosition.getX(), newPosition.getY()));

        // Catch this so the item still updates!
        try {
            for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation())) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                final List<RoomEntity> affectEntities1 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (RoomEntity entity1 : affectEntities1) {
                    item.onEntityStepOff(entity1);
                }
            }

            for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), newPosition.getX(), newPosition.getY(), rotation)) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                final List<RoomEntity> affectEntities2 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (final RoomEntity entity2 : affectEntities2) {
                    item.onEntityStepOn(entity2);
                }
            }
        } catch (Exception e) {
            log.error("Failed to update entity positions for changing item position", e);
        }

        item.getPosition().setX(newPosition.getX());
        item.getPosition().setY(newPosition.getY());

        item.getPosition().setZ(height);
        item.setRotation(rotation);
        //item.resetLastMovement();

        final List<RoomEntity> affectEntities3 = room.getEntities().getEntitiesAt(newPosition);

        for (final RoomEntity entity3 : affectEntities3) {
            item.onEntityStepOn(entity3);
        }

        if (save)
            item.save();

        for (final Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessageModeBuild(tileInstance);
            }
        }

        tilesToUpdate.clear();
        //WiredTriggerFurniOutPosition.executeTriggers(item);

        return true;
    }

    public boolean moveFloorItemWired(RoomItemFloor item, Position newPosition, int rotation, boolean save, boolean autoheight, boolean limit) {
        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if (autoheight && !this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), rotation)) {
            return false;
        }

        if (item instanceof FreezeTileFloorItem && tile.hasItems() && tile.getItems().stream().anyMatch(tileItem -> tileItem instanceof FreezeTileFloorItem)) {
            return false;
        }

        double height;

        if (autoheight)
            height = tile.getStackHeight(item);
        else
            height = newPosition.getZ();

        if (item instanceof WiredAddonNewPuzzleBox) {
            if (!tile.canPlaceItemHere()) {
                return false;
            }
        }

        if (limit && (tile.getStackHeight() - tile.getTileHeight()) > 0.2)
            return false;


        if (autoheight && this.getRoom().getEntities().getEntitiesAt(newPosition).size() > 0)
            return false;

        return this.moveFloorItemAfter(item, newPosition, height, rotation, save);
    }

    public void commit() {
        if (!CometSettings.storageItemQueueEnabled) {
            return;
        }

        /*List<RoomItem> floorItems = new ArrayList<>();

        for (RoomItemFloor floorItem : this.floorItems.values()) {
            if (floorItem.hasQueuedSave()) {
                floorItems.add(floorItem);

                ItemStorageQueue.getInstance().unqueue(floorItem);
            }
        }

        if (floorItems.size() != 0) {
            RoomItemData.saveFloorItems(floorItems);
        }

        floorItems.clear();*/
    }

    public boolean isMoodlightMatches(RoomItem item) {
        if (this.moodlightId == 0) {
            return false;
        }

        return (this.moodlightId == item.getId());
    }

    public MoodlightWallItem getMoodlight() {
        return (MoodlightWallItem) this.getWallItem(this.moodlightId);
    }

    public RoomItemFloor addFloorItem(long id, int baseId, Room room, int ownerId, String ownerName, int x, int y, int rot, double height, String data, LimitedEditionItem limitedEditionItem) {
        final RoomItemData itemData = new RoomItemData(id, baseId, ownerId, ownerName, new Position(x, y, height), rot, data, "", limitedEditionItem);

        final FurnitureDefinition furnitureDefinition = ItemManager.getInstance().getDefinition(baseId);
        final RoomItemFloor floor = RoomItemFactory.createFloor(itemData, room, ItemManager.getInstance().getDefinition(baseId));

        if (floor == null) return null;

        final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(ownerId);

        if (session != null && session.getPlayer() != null && room.getData().getOwnerId() == ownerId && session.getPlayer().isOnline()) {
            session.getPlayer().getAchievements().progressAchievement(AchievementType.BUILDER_FURNI_COUNT, 1);

            if (furnitureDefinition.getInteraction().equals("blackhole")) {
                session.getPlayer().getAchievements().progressAchievement(AchievementType.BUILDER_BLACK_HOLES_COUNT, 1);
                this.blackHolesCount++;
            }

            if (furnitureDefinition.getInteraction().equals("snowb_slope")) {
                session.getPlayer().getAchievements().progressAchievement(AchievementType.BUILDER_SNOWBOARD_COUNT, 1);
                this.snowbSlopeCount++;
            }

            if (furnitureDefinition.getInteraction().equals("iceskate")) {
                session.getPlayer().getAchievements().progressAchievement(AchievementType.BUILDER_ICESKATES_COUNT, 1);
                this.iceSkateCount++;
            }

            if (furnitureDefinition.getInteraction().equals("rollerskate")) {
                session.getPlayer().getAchievements().progressAchievement(AchievementType.BUILDER_ROLLERSKATES_COUNT, 1);
                this.rollerSkateCount++;
            }
        }

        this.floorItems.put(id, floor);
        this.indexFloorItem(floor);

        this.increaseItemsCount();
        return floor;
    }

    public RoomItemWall addWallItem(long id, int baseId, Room room, int ownerId, String ownerName, String position, String data) {
        final RoomItemData itemData = new RoomItemData(id, baseId, ownerId, ownerName, new Position(), 0, data, position, null);

        final RoomItemWall wall = RoomItemFactory.createWall(itemData, room, ItemManager.getInstance().getDefinition(baseId));

        this.getWallItems().put(id, wall);

        final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(ownerId);

        if (session != null && session.getPlayer() != null && room.getData().getOwnerId() == ownerId && session.getPlayer().isOnline()) {
            session.getPlayer().getAchievements().progressAchievement(AchievementType.BUILDER_FURNI_COUNT, 1);
        }

        this.increaseItemsCount();
        this.indexWallItem(wall);
        return wall;
    }

    public List<RoomItemFloor> getItemsOnSquare(int x, int y) {
        final RoomTile tile = this.getRoom().getMapping().getTile(x, y);

        if (tile == null) {
            return Lists.newArrayList();
        }

        return new ArrayList<>(tile.getItems());
    }

    public RoomItemFloor getFloorItem(int id) {
        final Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);

        if (itemId == null) {
            return null;
        }

        return this.floorItems.get(itemId);
    }

    public RoomItemWall getWallItem(int id) {
        final Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);

        if (itemId == null) {
            return null;
        }

        return this.wallItems.get(itemId);
    }

    public RoomItemFloor getFloorItem(long id) {
        return this.floorItems.get(id);
    }

    public RoomItemWall getWallItem(long id) {
        return this.wallItems.get(id);
    }

    public List<RoomItemFloor> getByInteraction(String interaction) {
        final List<RoomItemFloor> items = new ArrayList<>();

        for (final RoomItemFloor floorItem : this.floorItems.values()) {
            if (floorItem == null || floorItem.getDefinition() == null) continue;

            if (floorItem.getDefinition().getInteraction().equals(interaction)) {
                items.add(floorItem);
            } else if (interaction.contains("%")) {
                if (interaction.startsWith("%") && floorItem.getDefinition().getInteraction().endsWith(interaction.replace("%", ""))) {
                    items.add(floorItem);
                } else if (interaction.endsWith("%") && floorItem.getDefinition().getInteraction().startsWith(interaction.replace("%", ""))) {
                    items.add(floorItem);
                }
            }
        }

        return items;
    }

    public <T extends RoomItemFloor> List<T> getByClass(Class<T> clazz) {
        final List<T> items = new ArrayList<>();

        if (this.itemClassIndex.containsKey(clazz)) {
            for (long itemId : this.itemClassIndex.get(clazz)) {
                RoomItemFloor floorItem = this.getFloorItem(itemId);

                if (floorItem == null || floorItem.getDefinition() == null) continue;

//                if (!floorItem.getClass().equals(clazz)) {
//                    continue;
//                }

                T item = ((T) floorItem);
                items.add(item);
            }
        }

        return items;
    }

    public void removeItem(RoomItemWall item, int ownerId, Session client) {
        StorageContext.getCurrentContext().getRoomItemRepository().removeItemFromRoom(item.getId(), ownerId, item.getItemData().getData());

        room.getEntities().broadcastMessage(new RemoveWallItemMessageComposer(ItemManager.getInstance().getItemVirtualId(item.getId()), ownerId));
        this.getWallItems().remove(item.getId());
        this.decreaseItemsCount();

        if (item.getDefinition().getInteraction().equals("postit")) {
            this.postItCount--;
        }

        if (client != null && client.getPlayer() != null) {
            client.getPlayer().getInventory().add(item.getId(), item.getItemData().getItemId(), item.getItemData().getData(), item.getLimitedEditionItemData());
            client.send(new UpdateInventoryMessageComposer());
        }
    }

    public void removeItem(RoomItemFloor item, Session client) {
        removeItem(item, client, true);
    }

    public void removeItem(RoomItemFloor item, Session client, boolean toInventory) {
        if (item instanceof SoundMachineFloorItem) {
            this.soundMachineId = 0;
        }

        if (item.getWiredItems().size() != 0) {
            for (final long wiredItem : item.getWiredItems()) {
                final WiredFloorItem floorItem = (WiredFloorItem) this.getFloorItem(wiredItem);

                if (floorItem != null) {
                    floorItem.getWiredData().getSelectedIds().remove(item.getId());
                }
            }
        }

        removeItem(item, client, toInventory, false);
    }

    public void removeItem(RoomItemFloor item, Session session, boolean toInventory, boolean delete) {
        final RoomTile roomTile = room.getMapping().getTile(item.getPosition());

        final List<RoomEntity> affectEntities = room.getEntities().getEntitiesAt(item.getPosition());
        final List<Position> tilesToUpdate = new ArrayList<>();

        tilesToUpdate.add(new Position(item.getPosition().getX(), item.getPosition().getY(), 0d));

        for (final RoomEntity entity : affectEntities) {
            item.onEntityStepOff(entity);
        }

        if (item instanceof SoundMachineFloorItem) {
            if (this.soundMachineId == item.getId()) {
                this.soundMachineId = 0;
            }
        }

        if (item.getDefinition().getInteraction().equals("blackhole")) {
            this.blackHolesCount--;
        }

        if (item.getDefinition().getInteraction().equals("iceskate")) {
            this.iceSkateCount--;
        }

        if (item.getDefinition().getInteraction().equals("snowb_slope")) {
            this.snowbSlopeCount--;
        }

        if (item.getDefinition().getInteraction().equals("rollerskate")) {
            this.rollerSkateCount--;
        }

        for (final AffectedTile tile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation())) {
            final List<RoomEntity> entitiesOnItem = room.getEntities().getEntitiesAt(new Position(tile.x, tile.y));
            tilesToUpdate.add(new Position(tile.x, tile.y, 0d));

            for (final RoomEntity entity : entitiesOnItem) {
                item.onEntityStepOff(entity);

                if (roomTile != null && roomTile.getWalkHeight() != entity.getPosition().getZ()) {
                    entity.getPosition().setZ(roomTile.getWalkHeight());
                    entity.markNeedsUpdate();
                }
            }
        }

        Session client = session;
        final int owner = item.getItemData().getOwnerId();

        if (session != null) {
            if (owner != session.getPlayer().getId()) {
                client = NetworkManager.getInstance().getSessions().getByPlayerId(owner);
            }
        }

        this.getRoom().getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(item.getVirtualId(), (session != null) ? owner : 0));
        this.getFloorItems().remove(item.getId());
        this.decreaseItemsCount();

        StorageContext.getCurrentContext().getRoomItemRepository().removeItemFromRoom(item.getId(), owner, item.getDataObject());

        if (toInventory && client != null) {
            final PlayerItem playerItem = client.getPlayer().getInventory().add(item.getId(), item.getItemData().getItemId(), item.getItemData().getData(), item instanceof GiftFloorItem ? ((GiftFloorItem) item).getGiftData() : null, item.getLimitedEditionItemData());
            client.sendQueue(new UpdateInventoryMessageComposer());
            client.sendQueue(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
            client.flush();
        } else {
            if (delete)
                StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(item.getId());
        }

        for (final Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tileInstance));
            }
        }
    }

    public void removeItem(RoomItemWall item, Session client, boolean toInventory) {
        this.getRoom().getEntities().broadcastMessage(new RemoveWallItemMessageComposer(item.getVirtualId(), item.getItemData().getOwnerId()));
        this.getWallItems().remove(item.getId());
        this.decreaseItemsCount();

        if (item.getDefinition().getInteraction().equals("postit")) {
            this.postItCount--;
        }

        if (toInventory) {
            StorageContext.getCurrentContext().getRoomItemRepository().removeItemFromRoom(item.getId(), item.getItemData().getOwnerId(), item.getItemData().getData());

            Session session = client;

            if (item.getItemData().getOwnerId() != client.getPlayer().getId()) {
                session = NetworkManager.getInstance().getSessions().getByPlayerId(item.getItemData().getOwnerId());
            }

            if (session != null) {
                session.getPlayer().getInventory().add(item.getId(), item.getItemData().getItemId(), item.getItemData().getData(), item.getLimitedEditionItemData());
                session.send(new UpdateInventoryMessageComposer());
                session.send(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                    put(1, Lists.newArrayList(item.getVirtualId()));
                }}));
            }
        } else {
            StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(item.getId());
        }
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int rotation, boolean save, Session client) {
        final RoomItemFloor item = this.getFloorItem(itemId);

        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());
        final double height = tile.getStackHeight(item);

        if (!this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), rotation)) {
            return false;
        }

        return this.moveFloorItemAfter(item, newPosition, height, rotation, save);
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int rotation, boolean save) {
        return moveFloorItem(itemId, newPosition, rotation, save, true, null, false);
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int rotation, boolean save, boolean obeyStack, Player mover, boolean canPlaceOnEntity) {
        final RoomItemFloor item = this.getFloorItem(itemId);
        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if (!this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), rotation)) {
            return false;
        }

        double height = tile.getStackHeight(item);

        final List<RoomItemFloor> floorItemsAt = this.getItemsOnSquare(newPosition.getX(), newPosition.getY());

        for (final RoomItemFloor stackItem : floorItemsAt) {
            if (item.getId() != stackItem.getId()) {
                stackItem.onItemAddedToStack(item);
            }
        }

        item.onPositionChanged(newPosition);

        final List<Position> tilesToUpdate = new ArrayList<>();

        tilesToUpdate.add(new Position(item.getPosition().getX(), item.getPosition().getY()));
        tilesToUpdate.add(new Position(newPosition.getX(), newPosition.getY()));

        final Position oldPosition = item.getPosition().copy();
        final int oldRotation = item.getRotation();

        item.getPosition().setX(newPosition.getX());
        item.getPosition().setY(newPosition.getY());

        item.getPosition().setZ(height);
        item.getItemData().setRotation(rotation);

        // Catch this so the item still updates!
        try {
            for (final AffectedTile affectedTile : AffectedTile.getAffectedBothTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), oldPosition.getX(), oldPosition.getY(), oldRotation)) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                final List<RoomEntity> affectEntities1 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (final RoomEntity entity1 : affectEntities1) {
                    item.onEntityStepOff(entity1);
                    WiredTriggerWalksOffFurni.executeTriggers(entity1, item);
                    // update their height! maybe we shouldnt ? seems to lag the room
                    if (tile.getWalkHeight() != entity1.getPosition().getZ()) {
                        entity1.getPosition().setZ(tile.getWalkHeight());
                        entity1.markNeedsUpdate();
                    }
                }
            }

            for (final AffectedTile affectedTile : AffectedTile.getAffectedBothTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), newPosition.getX(), newPosition.getY(), rotation)) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                final List<RoomEntity> affectEntities2 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (final RoomEntity entity2 : affectEntities2) {
                    item.onEntityStepOn(entity2);
                    if (!item.getPosition().equals(newPosition))
                        WiredTriggerWalksOnFurni.executeTriggers(entity2, item); // possible stack overflow

                    // update their height! maybe we shouldnt ? seems to lag the room
                    if (tile.getWalkHeight() != entity2.getPosition().getZ()) {
                        entity2.getPosition().setZ(tile.getWalkHeight());
                        entity2.markNeedsUpdate();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to update entity positions for changing item position", e);
        }

        if (save)
            item.save();

        for (final Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tileInstance));
            }
        }

        tilesToUpdate.clear();
        return true;
    }

    private boolean verifyItemPosition(FurnitureDefinition item, RoomItemFloor floor, RoomTile tile, Position currentPosition, int rotation) {
        if (tile != null) {
            if (currentPosition != null && currentPosition.getX() == tile.getPosition().getX() && currentPosition.getY() == tile.getPosition().getY())
                return true;

            if (!tile.canPlaceItemHere()) {
                return false;
            }

            if (!tile.canStack() && tile.getTopItem() != 0 && tile.getTopItem() != item.getId()) {
                if (!item.getItemName().startsWith(RoomItemFactory.STACK_TOOL))
                    return false;
            }

            if (!item.getInteraction().equals(RoomItemFactory.TELEPORT_PAD) && tile.getPosition().getX() == this.getRoom().getModel().getDoorX() && tile.getPosition().getY() == this.getRoom().getModel().getDoorY()) {
                return false;
            }

            if (item.getInteraction().equals("dice")) {
                boolean hasOtherDice = false;
                boolean hasStackTool = false;

                for (final RoomItemFloor floorItem : tile.getItems()) {
                    if (floorItem instanceof DiceFloorItem) {
                        hasOtherDice = true;
                    }

                    if (floorItem instanceof MagicStackFloorItem) {
                        hasStackTool = true;
                    }
                }

                if (hasOtherDice && hasStackTool)
                    return false;
            }

            if (!CometSettings.roomCanPlaceItemOnEntity) {
                return tile.getEntities().size() == 0;
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean verifyItemTilePosition(FurnitureDefinition item, RoomItemFloor floorItem, RoomTile tile, int rotation) {
        if (!tile.canPlaceItemHere()) {
            return false;
        }

        if (!tile.canStack() && tile.getTopItem() != 0 && (floorItem == null || tile.getTopItem() != floorItem.getId())) {
            if (!item.getItemName().startsWith(RoomItemFactory.STACK_TOOL))
                return false;
        }

        if (!item.getInteraction().equals(RoomItemFactory.TELEPORT_PAD) && tile.getPosition().getX() == this.getRoom().getModel().getDoorX() && tile.getPosition().getY() == this.getRoom().getModel().getDoorY()) {
            return false;
        }

        if (item.getInteraction().equals("dice")) {
            boolean hasOtherDice = false;
            boolean hasStackTool = false;

            for (final RoomItemFloor itemFloor : tile.getItems()) {
                if (itemFloor instanceof DiceFloorItem) {
                    hasOtherDice = true;
                }

                if (itemFloor instanceof MagicStackFloorItem) {
                    hasStackTool = true;
                }
            }

            if (hasOtherDice && hasStackTool)
                return false;
        }

        if (!CometSettings.roomCanPlaceItemOnEntity) {
            return tile.getEntities().size() == 0;
        }

        return true;
    }

    private boolean verifyItemTilePositionSetz(FurnitureDefinition item, RoomItemFloor floorItem, RoomTile tile, int rotation) {
        return tile.canPlaceItemHere();
    }

    public void placeWallItem(PlayerItem item, String position, Player player) {
        int roomId = this.room.getId();

        StorageContext.getCurrentContext().getRoomItemRepository().placeWallItem(roomId, position, item.getExtraData().trim().isEmpty() ? "0" :
                item.getExtraData(), item.getId());
        player.getInventory().removeItem(item.getId());

        final RoomItemWall wallItem = this.addWallItem(item.getId(), item.getBaseId(), this.room, player.getId(),
                player.getData().getUsername(), position, (item.getExtraData().isEmpty() ||
                        item.getExtraData().equals(" ")) ? "0" : item.getExtraData());

        if (wallItem.getDefinition().getInteraction().equals("postit")) {
            this.postItCount++;
        }

        this.room.getEntities().broadcastMessage(
                new SendWallItemMessageComposer(wallItem)
        );

        wallItem.onPlaced();
    }

    public Room getRoom() {
        return this.room;
    }

    public Map<Long, RoomItemFloor> getFloorItems() {
        return this.floorItems;
    }

    public Map<Long, RoomItemWall> getWallItems() {
        return this.wallItems;
    }

    public void placeFloorItem(PlayerItem item, int x, int y, int rot, Player player) {
        final RoomTile tile = room.getMapping().getTile(x, y);

        if (tile == null)
            return;

        double height = tile.getStackHeight(null);

        if (item.getDefinition().getInteraction().equals("soundmachine")) {
            if (this.soundMachineId > 0) {
                final Map<String, String> notificationParams = Maps.newHashMap();

                notificationParams.put("message", Locale.get("game.room.jukeboxExists"));

                player.getSession().send(new NotificationMessageComposer("furni_placement_error", notificationParams));
                return;
            } else {
                this.soundMachineId = item.getId();
            }
        }

        final List<RoomItemFloor> floorItems = room.getItems().getItemsOnSquare(x, y);

        if (item.getDefinition() != null && item.getDefinition().getInteraction() != null) {
            if (item.getDefinition().getInteraction().equals("mannequin")) {
                rot = 2;
            }
        }

        final String ExtraData = (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : item.getExtraData();
        RoomItemDao.placeFloorItem(room.getId(), x, y, height, rot, ExtraData, item.getId());
        player.getInventory().removeItem(item.getId());

        if(item.getDefinition().getItemName().startsWith(RoomItemFactory.STACK_TOOL)) {
            height = tile.getWalkHeight();
        }

        final RoomItemFloor floorItem = room.getItems().addFloorItem(item.getId(), item.getBaseId(), room, player.getId(), player.getData().getUsername(), x, y, rot, height, ExtraData, item.getLimitedEditionItem());

        final List<Position> tilesToUpdate = new ArrayList<>();

        for (final RoomItemFloor stackItem : floorItems) {
            if (item.getId() != stackItem.getId()) {
                stackItem.onItemAddedToStack(floorItem);
            }
        }

        tilesToUpdate.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY(), 0d));

        for (final AffectedTile affTile : AffectedTile.getAffectedBothTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), floorItem.getPosition().getX(), floorItem.getPosition().getY(), floorItem.getRotation())) {
            tilesToUpdate.add(new Position(affTile.x, affTile.y, 0d));

            final List<RoomEntity> affectEntities0 = room.getEntities().getEntitiesAt(new Position(affTile.x, affTile.y));

            for (final RoomEntity entity0 : affectEntities0) {
                floorItem.onEntityStepOn(entity0);
            }
        }

        for (final Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tileInstance));
            }
        }

        room.getEntities().broadcastMessage(new SendFloorItemMessageComposer(floorItem));

        floorItem.onPlaced();
        floorItem.saveData();
    }

    private void indexFloorItem(RoomItemFloor floorItem) {
        this.itemOwners.put(floorItem.getItemData().getOwnerId(), floorItem.getItemData().getOwnerName());

        if (!this.itemClassIndex.containsKey(floorItem.getClass())) {
            itemClassIndex.put(floorItem.getClass(), new HashSet<>());
        }

        if (floorItem instanceof HighscoreFloorItem) {
            itemClassIndex.get(HighscoreFloorItem.class).add(floorItem.getId());
        }

        if (!this.itemInteractionIndex.containsKey(floorItem.getDefinition().getInteraction())) {
            this.itemInteractionIndex.put(floorItem.getDefinition().getInteraction(), new HashSet<>());
        }

        this.itemClassIndex.get(floorItem.getClass()).add(floorItem.getId());
        this.itemInteractionIndex.get(floorItem.getDefinition().getInteraction()).add(floorItem.getId());
    }

    private void indexWallItem(RoomItemWall wallItem) {
        this.itemOwners.put(wallItem.getItemData().getOwnerId(), wallItem.getItemData().getOwnerName());
    }

    public SoundMachineFloorItem getSoundMachine() {
        if (this.soundMachineId != 0) {
            return ((SoundMachineFloorItem) this.getFloorItem(this.soundMachineId));
        }

        return null;
    }

    public Map<Integer, String> getItemOwners() {
        return this.itemOwners;
    }
}
