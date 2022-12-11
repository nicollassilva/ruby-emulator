package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.ItemType;
import com.cometproject.api.game.furniture.types.LimitedEditionItem;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.floor.*;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiTeleporterFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.traxmachine.TraxMachineFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonNewPuzzleBox;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonPuzzleBox;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.cometproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.types.RoomMessageType;
import com.cometproject.server.game.rooms.types.mapping.RoomEntityMovementNode;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.*;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageComposer;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ItemsComponent {
    private final Logger log;
    private final Map<Long, RoomItemFloor> floorItems = new ConcurrentHashMap<>();
    private final Map<Long, RoomItemWall> wallItems = new ConcurrentHashMap<>();
    private final Map<Integer, String> itemOwners = new ConcurrentHashMap<>();
    private final Room room;
    private final Map<Class<? extends RoomItemFloor>, Set<Long>> itemClassIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> itemInteractionIndex = new ConcurrentHashMap<>();
    private RoomItemFloor soundMachineFloorItem;
    private RoomItemFloor traxMachineFloorItem;
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
        this.soundMachineFloorItem = null;
        this.traxMachineFloorItem = null;
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
                final RoomItemData data = new RoomItemData(floorItemDataObject.getId(), floorItemDataObject.getItemDefinitionId(), floorItemDataObject.getOwner(), floorItemDataObject.getOwnerName(), floorItemDataObject.getPosition(), floorItemDataObject.getRotation(), floorItemDataObject.getData(), "", floorItemDataObject.getLimitedEditionItemData());

                this.floorItems.put(floorItemDataObject.getId(), RoomItemFactory.createFloor(data, room, ItemManager.getInstance().getDefinition(floorItemDataObject.getItemDefinitionId())));
            }

            for (final WallItemDataObject wallItemDataObject : room.getCachedData().getWallItems()) {
                final RoomItemData data = new RoomItemData(wallItemDataObject.getId(), wallItemDataObject.getItemDefinitionId(), wallItemDataObject.getOwner(), wallItemDataObject.getOwnerName(), new Position(), 0, wallItemDataObject.getData(), wallItemDataObject.getWallPosition(), wallItemDataObject.getLimitedEditionItemData());

                this.wallItems.put(wallItemDataObject.getId(), RoomItemFactory.createWall(data, room, ItemManager.getInstance().getDefinition(wallItemDataObject.getItemDefinitionId())));
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
                soundMachineFloorItem = floorItem;
            }

            if (floorItem instanceof TraxMachineFloorItem) {
                traxMachineFloorItem = floorItem;
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
        if (this.moodlightId != 0) return false;

        this.moodlightId = moodlight;
        return true;
    }

    public void removeMoodlight() {
        if (this.moodlightId == 0) {
            return;
        }

        this.moodlightId = 0;
    }

    public void moveItemsOnSquare(MagicMoveFloorItem moveFloorItem, Position newPosition) {
        final List<AffectedTile> oldPositionAffectedTiles = AffectedTile.getAffectedBothTilesAt(moveFloorItem.getDefinition().getLength(), moveFloorItem.getDefinition().getWidth(), moveFloorItem.getPosition().getX(), moveFloorItem.getPosition().getY(), moveFloorItem.getRotation());
        final Position oldPosition = moveFloorItem.getPosition().copy();
        if (!moveFloorItemWired(moveFloorItem, newPosition, moveFloorItem.getRotation(), false, false)) {
            return;
        }

        final List<RoomItemFloor> itemsToMove = new ArrayList<>(oldPositionAffectedTiles.size());
        for (final AffectedTile affectedTile : oldPositionAffectedTiles) {
            final RoomTile tile = this.room.getMapping().getTile(affectedTile.x, affectedTile.y);
            if (tile == null) continue;

            itemsToMove.addAll(tile.getItems());
        }

        final List<MessageComposer> toRoomMessages = new ArrayList<>(itemsToMove.size());
        final int diffX = newPosition.getX() - oldPosition.getX();
        final int diffY = newPosition.getY() - oldPosition.getY();
        for (final RoomItemFloor item : itemsToMove) {
            if (item instanceof MagicMoveFloorItem) continue;
            final Position oldItemsPosition = item.getPosition().copy();
            final Position newItemsPosition = oldItemsPosition.copy();
            newItemsPosition.setX(newItemsPosition.getX() + diffX);
            newItemsPosition.setY(newItemsPosition.getY() + diffY);

            final RoomTile tile = this.room.getMapping().getTile(newItemsPosition);
            if (!this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), null))
                continue;

            for (final RoomItemFloor stackItem : this.getItemsOnSquare(newItemsPosition.getX(), newItemsPosition.getY())) {
                if (item.getId() != stackItem.getId()) {
                    stackItem.onItemAddedToStack(item);
                }
            }
            item.getPosition().setX(newItemsPosition.getX());
            item.getPosition().setY(newItemsPosition.getY());
            item.getPosition().setZ(item.getPosition().getZ());

            item.save();
            toRoomMessages.add(new SlideObjectBundleMessageComposer(oldItemsPosition, newItemsPosition, 0, 0, item.getVirtualId()));
        }
        room.getEntities().broadcastMessagesQueue(toRoomMessages, false, RoomMessageType.GENERIC_COMPOSER);

        updateEntitiesOnAffectedTiles(oldPositionAffectedTiles);
        final List<AffectedTile> newAffectedTiles = AffectedTile.getAffectedBothTilesAt(moveFloorItem.getDefinition().getLength(), moveFloorItem.getDefinition().getWidth(), moveFloorItem.getPosition().getX(), moveFloorItem.getPosition().getY(), moveFloorItem.getRotation());
        updateEntitiesOnAffectedTiles(newAffectedTiles);
    }

    /**
     * Internal use only.
     * Used for skip verifications while moving items.
     *
     * @param item
     * @param newPosition
     * @param newHeight
     * @param newRotation
     * @param state
     */
    public void __unsafeMoveItemUpdateTilesAndSave(Session client, RoomItemFloor item, Position newPosition, double newHeight, int newRotation, int state) {
        final Position oldPosition = item.getPosition().copy();
        int oldRotation = item.getRotation();
        for (final RoomItemFloor stackItem : this.getItemsOnSquare(newPosition.getX(), newPosition.getY())) {
            if (item.getId() != stackItem.getId()) {
                stackItem.onItemAddedToStack(item);
            }
        }

        newPosition.setZ(newHeight);
        item.onPositionChanged(newPosition);
        item.getPosition().setX(newPosition.getX());
        item.getPosition().setY(newPosition.getY());
        item.getPosition().setZ(newPosition.getZ());
        item.setRotation(newRotation);

        if (client != null && client.getPlayer().getEntity().hasAttribute("state.height"))
            item.getItemData().setData(state);

        item.save();
        __updateEntitiesWhenItemWasMoved(item, newPosition, newRotation, oldPosition, oldRotation);
    }

    private void __updateEntitiesWhenItemWasMoved(RoomItemFloor item, Position newPosition, int newRotation, Position oldPosition, int oldRotation) {
        updateEntitiesOnFurniMove(item, oldPosition, oldPosition, newPosition, oldRotation, false);
        updateEntitiesOnFurniMove(item, newPosition, oldPosition, newPosition, newRotation, true);
    }


    public boolean moveFloorItemMatch(RoomItemFloor item, Position newPosition, int rotation, boolean save, boolean autoheight, boolean limit) {
        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if (autoheight && !this.verifyItemTilePosition(item.getDefinition(), item, tile, item.getPosition(), rotation)) {
            return false;
        }

        double height;

        if (autoheight)
            height = tile.getStackHeight(item);
        else
            height = newPosition.getZ();

        if (item instanceof WiredAddonPuzzleBox) {
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
            for (AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation())) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                final List<RoomEntity> affectEntities1 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (RoomEntity entity1 : affectEntities1) {
                    item.onEntityStepOff(entity1);
                }
            }

            for (AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), newPosition.getX(), newPosition.getY(), rotation)) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                final List<RoomEntity> affectEntities2 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (RoomEntity entity2 : affectEntities2) {
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
        item.setMoveDirection(-1);

        final List<RoomEntity> affectEntities3 = room.getEntities().getEntitiesAt(newPosition);

        for (RoomEntity entity3 : affectEntities3) {
            item.onEntityStepOn(entity3);
        }

        if (save)
            item.save();

        for (Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                // room.getEntities().broadcastMessageModeBuild(tileInstance);
            }
        }

        tilesToUpdate.clear();

        return true;
    }

    public boolean moveFloorItemWired(RoomItemFloor item, Position newPosition, int newRotation, boolean autoHeight, boolean limit) {
        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if (autoHeight && !this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), null)) {
            return false;
        }

        double height = newPosition.getZ();

        if (autoHeight) {
            height = tile.getStackHeight(item);
        }

        if (item instanceof WiredAddonNewPuzzleBox && !tile.canPlaceItemHere())
            return false;

        if (limit && (tile.getStackHeight() - tile.getTileHeight()) > 1.2)
            return false;


        if (autoHeight && this.getRoom().getEntities().getEntitiesAt(newPosition).size() > 0)
            return false;

        this.__unsafeMoveItemUpdateTilesAndSave(null, item, newPosition, height, newRotation, 0);
        return true;
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
        removeItem(item, client, toInventory, true);
    }

    public void removeItem(RoomItemFloor item, Session client, boolean toInventory, boolean sendPacket) {
        if (item instanceof SoundMachineFloorItem) {
            this.soundMachineFloorItem = null;
        }

        if (item instanceof TraxMachineFloorItem) {
            this.traxMachineFloorItem = null;
        }

        if (item.getWiredItems().size() != 0) {
            for (final long wiredItem : item.getWiredItems()) {
                final WiredFloorItem floorItem = (WiredFloorItem) this.getFloorItem(wiredItem);

                if (floorItem != null) {
                    floorItem.getWiredData().getSelectedIds().remove(item.getId());
                }
            }
        }

        removeItem(item, client, toInventory, false, sendPacket);
    }

    private void updatePickedUpObjectEntities(RoomItemFloor item, Position position) {
        updateEntitiesOnFurniMove(item, position, position, new Position(-1, -1), item.getRotation(), false);
    }

    public void updateEntitiesOnFurniMove(RoomItemFloor item, Position itemPosition, Position oldPosition, Position newPosition, int rot, boolean isWalkOn) {
        final List<AffectedTile> affectedTiles = AffectedTile.getAffectedBothTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), itemPosition.getX(), itemPosition.getY(), rot);
        updateEntitiesOnAffectedTiles(affectedTiles, isWalkOn, item, oldPosition, newPosition);
    }

    private void updateEntitiesOnAffectedTiles(List<AffectedTile> affectedTiles) {
        updateEntitiesOnAffectedTiles(affectedTiles, false, null, null, null);
    }

    private void updateEntitiesOnAffectedTiles(List<AffectedTile> affectedTiles, boolean isWalkOn, @Nullable RoomItemFloor triggerItem, @Nullable Position oldPosition, @Nullable Position newPosition) {
        final List<RoomTile> updatedTiles = new ArrayList<>(affectedTiles.size());
        for (final AffectedTile affectedTile : affectedTiles) {
            final RoomTile affectedTileInstance = this.getRoom().getMapping().getTile(affectedTile.x, affectedTile.y);
            updatedTiles.add(affectedTileInstance);
            affectedTileInstance.reload();

            final double newHeightOnAffectedTile = affectedTileInstance.getWalkHeight();
            for (final RoomEntity entity : affectedTileInstance.getEntities()) {
                if (triggerItem != null) {
                    if (isWalkOn) triggerItem.onEntityStepOn(entity);
                    else triggerItem.onEntityStepOff(entity);
                }

                if (triggerItem != null && oldPosition != null && newPosition != null && (oldPosition.getX() != newPosition.getX() || oldPosition.getY() != newPosition.getY())) { // call trigger only if furniture was move to another place
                    if (isWalkOn) WiredTriggerWalksOnFurni.executeTriggers(entity, triggerItem);
                    else WiredTriggerWalksOffFurni.executeTriggers(entity, triggerItem);
                }

                if (newHeightOnAffectedTile != entity.getPosition().getZ()) {
                    entity.getPosition().setZ(newHeightOnAffectedTile);
                    entity.setNeedsForcedUpdate(true);
                }
            }
        }

        if (!updatedTiles.isEmpty()) {
            room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(updatedTiles));
        }
    }

    public void removeItem(RoomItemFloor item, Session session, boolean toInventory, boolean delete, boolean sendPacket) {
        if (item instanceof SoundMachineFloorItem) {
            this.soundMachineFloorItem = null;
        }

        if (item instanceof TraxMachineFloorItem) {
            this.traxMachineFloorItem = null;
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

        Session client = session;
        final int owner = item.getItemData().getOwnerId();
        if (session != null) {
            if (owner != session.getPlayer().getId()) {
                client = NetworkManager.getInstance().getSessions().getByPlayerId(owner);
            }
        }

        if (sendPacket) {
            this.getRoom().getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(item.getVirtualId(), (session != null) ? owner : 0));
        }
        this.getFloorItems().remove(item.getId());
        this.decreaseItemsCount();

        StorageContext.getCurrentContext().getRoomItemRepository().removeItemFromRoom(item.getId(), owner, item.getDataObject());

        if (toInventory && client != null) {
            final PlayerItem playerItem = client.getPlayer().getInventory().add(item.getId(), item.getItemData().getItemId(), item.getItemData().getData(), item instanceof GiftFloorItem ? ((GiftFloorItem) item).getGiftData() : null, item.getLimitedEditionItemData());
            if (sendPacket) {
                client.sendQueue(new UpdateInventoryMessageComposer());
                client.sendQueue(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
                client.flush();
            }
        } else {
            if (delete) StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(item.getId());
        }

        updatePickedUpObjectEntities(item, item.getPosition());
    }

    public void removeItem(RoomItemWall item, Session client, boolean toInventory, boolean sendPacket) {
        if (sendPacket) {
            this.getRoom().getEntities().broadcastMessage(new RemoveWallItemMessageComposer(item.getVirtualId(), item.getItemData().getOwnerId()));
        }
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
                if (sendPacket) {
                    session.sendQueue(new UpdateInventoryMessageComposer());
                    session.sendQueue(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                        put(1, Lists.newArrayList(item.getVirtualId()));
                    }}));
                    session.flush();
                }
            }
        } else {
            StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(item.getId());
        }
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int newRotation, Session client) {
        final RoomItemFloor item = this.getFloorItem(itemId);

        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());
        double newHeight = tile.getStackHeight(item);

        if (client.getPlayer().getEntity().hasAttribute("setz.height")) {
            newHeight = (double) client.getPlayer().getEntity().getAttribute("setz.height") + this.room.getMapping().getTile(newPosition.getX(), newPosition.getY()).getTileHeight();
        }

        if (client.getPlayer().getEntity().hasAttribute("rotation.height")) {
            newRotation = (int) client.getPlayer().getEntity().getAttribute("rotation.height");
        }

        int newState = 0;

        if (client.getPlayer().getEntity().hasAttribute("state.height") && item.getDefinition().getInteractionCycleCount() > 0) {
            newState = (int) client.getPlayer().getEntity().getAttribute("state.height");

            if (newState > (item.getDefinition().getInteractionCycleCount() - 1))
                newState = item.getDefinition().getInteractionCycleCount() - 1;
        }

        if (!this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), client.getPlayer().getEntity())) {
            return false;
        }

        this.__unsafeMoveItemUpdateTilesAndSave(client, item, newPosition, newHeight, newRotation, newState);
        return true;
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int newRotation) {
        final RoomItemFloor item = this.getFloorItem(itemId);
        if (item == null) return false;

        final RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());
        if (!this.verifyItemPosition(item.getDefinition(), item, tile, item.getPosition(), null)) {
            return false;
        }

        double newHeight = tile.getStackHeight(item);

        newPosition.setZ(newHeight);
        this.__unsafeMoveItemUpdateTilesAndSave(null, item, newPosition, newHeight, newRotation, 0);
        return true;
    }


    private boolean verifyItemTilePosition(FurnitureDefinition item, RoomItemFloor floor, RoomTile tile, Position currentPosition, int rotation) {
        if (tile != null) {
            if (currentPosition != null && currentPosition.getX() == tile.getPosition().getX() && currentPosition.getY() == tile.getPosition().getY())
                return true;

            List<AffectedTile> affectedTiles = AffectedTile.getAffectedBothTilesAt(
                    item.getLength(), item.getWidth(), tile.getPosition().getX(), tile.getPosition().getY(), rotation);

            for (AffectedTile affectedTile : affectedTiles) {
                final RoomTile roomTile = this.getRoom().getMapping().getTile(affectedTile.x, affectedTile.y);

                if (roomTile != null) {
                    if (!this.verifyItemPosition(item, floor, roomTile, currentPosition, null)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean verifyItemPosition(FurnitureDefinition item, RoomItemFloor floor, RoomTile tile, Position currentPosition, PlayerEntity playerEntity) {
        if (tile == null) {
            return false;
        }

        final boolean hasStackToolCommand = playerEntity != null && playerEntity.hasAttribute("setz.height");

        if (currentPosition != null && currentPosition.getX() == tile.getPosition().getX() && currentPosition.getY() == tile.getPosition().getY())
            return true;

        if (!tile.canPlaceItemHere() && !hasStackToolCommand)
            return false;

        if (item.isLimitableItem()) {
            final long itemCount = tile.getItems().stream().filter(instance -> instance.getDefinition().getInteraction().equals(item.getInteraction())).count();

            if (itemCount >= item.getItemLimitation()) return false;
        }

        if (!tile.canStack() && tile.getTopItem() != 0 && tile.getTopItem() != item.getId() && !hasStackToolCommand) {
            if (!item.getItemName().startsWith(RoomItemFactory.STACK_TOOL)) {
                if (playerEntity != null)
                    return false;

                if (tile.getMovementNode() == RoomEntityMovementNode.CLOSED)
                    return false;
            }
        }


        if (!item.getInteraction().equals(RoomItemFactory.TELEPORT_PAD) && tile.getPosition().getX() == this.getRoom().getModel().getDoorX() && tile.getPosition().getY() == this.getRoom().getModel().getDoorY())
            return false;

        if (item.getInteraction().equals("dice") && !hasStackToolCommand) {
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

            if (hasOtherDice && hasStackTool) return false;
        }

        if (!CometSettings.roomCanPlaceItemOnEntity) {
            return tile.getEntities().size() == 0;
        }

        return true;
    }

    public void placeWallItem(PlayerItem item, String position, Player player) {
        int roomId = this.room.getId();

        StorageContext.getCurrentContext().getRoomItemRepository().placeWallItem(roomId, position, item.getExtraData().trim().isEmpty() ? "0" : item.getExtraData(), item.getId());
        player.getInventory().removeItem(item.getId());

        final RoomItemWall wallItem = this.addWallItem(item.getId(), item.getBaseId(), this.room, player.getId(), player.getData().getUsername(), position, (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : item.getExtraData());

        if (wallItem.getDefinition().getInteraction().equals("postit")) {
            this.postItCount++;
        }

        this.room.getEntities().broadcastMessage(new SendWallItemMessageComposer(wallItem));

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

        if (tile == null) {
            this.sendFurniturePlacementError(player.getSession());
            return;
        }

        double height = tile.getStackHeight(null);

        if (!this.verifyItemPosition(item.getDefinition(), null, tile, null, player.getEntity())) {
            this.sendFurniturePlacementError(player.getSession());
            return;
        }

        if (item.getDefinition().getInteraction().equals("soundmachine") && this.soundMachineFloorItem != null) {
            final Map<String, String> notificationParams = Maps.newHashMap();

            notificationParams.put("message", Locale.get("game.room.jukeboxExists"));

            player.getSession().send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            return;
        }

        if (item.getDefinition().getInteraction().equals("traxmachine") && this.traxMachineFloorItem != null) {
            final Map<String, String> notificationParams = Maps.newHashMap();

            notificationParams.put("message", Locale.get("game.room.traxExists"));

            player.getSession().send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            return;
        }

        if (item.getDefinition() != null && item.getDefinition().getInteraction() != null) {
            if (item.getDefinition().getInteraction().equals("mannequin")) {
                rot = 2;
            }
        }

        if (player.getEntity().hasAttribute("rotation.height")) {
            rot = (int) player.getEntity().getAttribute("rotation.height");
        }

        int newState = 0;

        if (player.getEntity().hasAttribute("state.height") && item.getDefinition().getInteractionCycleCount() > 0) {
            newState = (int) player.getEntity().getAttribute("state.height");

            if (newState > (item.getDefinition().getInteractionCycleCount() - 1))
                newState = item.getDefinition().getInteractionCycleCount() - 1;
        }

        final String ExtraData = (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : (!player.getEntity().hasAttribute("state.height") ? item.getExtraData() : String.valueOf(newState));

        RoomItemDao.placeFloorItem(room.getId(), x, y, height, rot, ExtraData, item.getId());
        player.getInventory().removeItem(item.getId());

        if (player.getEntity().hasAttribute("setz.height")) {
            height = (double) player.getEntity().getAttribute("setz.height") + this.room.getMapping().getTile(x, y).getTileHeight();
        }

        if (item.getDefinition().getItemName().startsWith(RoomItemFactory.STACK_TOOL)) {
            height = tile.getStackHeight();
        }

        final RoomItemFloor floorItem = room.getItems().addFloorItem(item.getId(), item.getBaseId(), room, player.getId(), player.getData().getUsername(), x, y, rot, height, ExtraData, item.getLimitedEditionItem());
        final List<RoomTile> tilesToUpdate = new ArrayList<>();

        for (final RoomItemFloor stackItem : room.getItems().getItemsOnSquare(x, y)) {
            if (item.getId() != stackItem.getId()) {
                stackItem.onItemAddedToStack(floorItem);
            }
        }

        for (final AffectedTile affTile : AffectedTile.getAffectedBothTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), floorItem.getPosition().getX(), floorItem.getPosition().getY(), floorItem.getRotation())) {
            final List<RoomEntity> affectEntities = room.getEntities().getEntitiesAt(new Position(affTile.x, affTile.y));

            for (final RoomEntity entity0 : affectEntities) {
                floorItem.onEntityStepOn(entity0);
            }

            final RoomTile tileInstance = this.room.getMapping().getTile(affTile.x, affTile.y);

            if (tileInstance != null) {
                tileInstance.reload();
                tilesToUpdate.add(tileInstance);
            }
        }

        room.getEntities().broadcastMessage(new SendFloorItemMessageComposer(floorItem));

        if (floorItem instanceof SoundMachineFloorItem) {
            this.soundMachineFloorItem = floorItem;
        }

        if (floorItem instanceof TraxMachineFloorItem) {
            this.traxMachineFloorItem = floorItem;
        }

        if (!tilesToUpdate.isEmpty()) {
            room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tilesToUpdate));
        }

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
        return (SoundMachineFloorItem) this.soundMachineFloorItem;
    }

    public TraxMachineFloorItem getTraxMachine() {
        return (TraxMachineFloorItem) this.traxMachineFloorItem;
    }

    public Map<Integer, String> getItemOwners() {
        return this.itemOwners;
    }

    protected void sendFurniturePlacementError(Session client) {
        final Map<String, String> notificationParams = Maps.newHashMap();
        notificationParams.put("message", "${room.error.cant_set_item}");

        client.send(new NotificationMessageComposer("furni_placement_error", notificationParams));
    }

    public List<RoomItemFloor> getBanzaiTeleportsExcept(long teleportId) {
        return this.getFloorItems().values().stream()
                .filter(item -> item instanceof BanzaiTeleporterFloorItem && item.getId() != teleportId)
                .collect(Collectors.toList());
    }
}
