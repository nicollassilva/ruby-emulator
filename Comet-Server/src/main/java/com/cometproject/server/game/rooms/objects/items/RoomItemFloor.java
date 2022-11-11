package com.cometproject.server.game.rooms.objects.items;

import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.objects.IFloorItem;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.*;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiTileFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.battleball.BattleBallTileFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.traxmachine.TraxMachineFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonFloorSwitch;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.utilities.attributes.Collidable;
import com.cometproject.storage.api.StorageContext;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;


public abstract class RoomItemFloor extends RoomItem implements Collidable, IFloorItem {
    private FurnitureDefinition itemDefinition;
    private RoomEntity collidedEntity;
    private boolean hasQueuedSave;
    private String coreState;
    private boolean stateSwitched = false;
    private int lastStartDir = -1;

    private boolean isLocked;

    public boolean getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(boolean value) {
        this.isLocked = value;
    }

    public RoomItemFloor(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public RoomItemFloor(int id, int userId, PlayerItem item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public void serialize(IComposer msg, boolean isNew) {
        msg.writeInt(this.getVirtualId());
        msg.writeInt(this.getDefinition().getSpriteId());
        msg.writeInt(this.getPosition().getX());
        msg.writeInt(this.getPosition().getY());
        msg.writeInt(this.getRotation());

        msg.writeString(this instanceof MagicStackFloorItem ? this.getItemData().getData() : this.getPosition().getZ());
        msg.writeString(this instanceof AdjustableHeightFloorItem ? this.getOverrideHeight() : this.getDefinition().getHeight());

        if (this.getLimitedEditionItemData() != null) {
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(true);
            msg.writeBoolean(false);
            msg.writeString(this.getItemData().getData());

            msg.writeInt(this.getLimitedEditionItemData().getLimitedRare());
            msg.writeInt(this.getLimitedEditionItemData().getLimitedRareTotal());
        } else {
            this.composeItemData(msg);
        }

        msg.writeInt(-1);
        msg.writeInt(this.isUsableFurnitureForEverybody() ? 2 : this.isUsableFurnitureControllers() ? 1 : 0);
        msg.writeInt(this.getRoom().isPublicRoom() ? 0 : this.getItemData().getOwnerId());

        if (isNew)
            msg.writeString(this.getRoom().isPublicRoom() ? "" : this.getItemData().getOwnerName());
    }

    public boolean isUsableFurnitureForEverybody() {
        if (!this.notUsableFurnitures()) {
            return false;
        }

        return this instanceof VendingMachineFloorItem || this instanceof SidelessVendingMachineFloorItem || this instanceof WiredAddonFloorSwitch || this instanceof TeleporterFloorItem || this instanceof DiceFloorItem;
    }

    public boolean isUsableFurnitureControllers() {
        if (!this.notUsableFurnitures()) {
            return false;
        }

        return this.isUsable();
    }

    private boolean notUsableFurnitures() {
        return !(this instanceof SoundMachineFloorItem) && !(this instanceof TraxMachineFloorItem) && !(this instanceof BanzaiTileFloorItem) && !(this instanceof BattleBallTileFloorItem);
    }

    @Override
    public void serialize(IComposer msg) {
        this.serialize(msg, false);
    }

    public FurnitureDefinition getDefinition() {
        if (this.itemDefinition == null) {
            this.itemDefinition = ItemManager.getInstance().getDefinition(this.getItemData().getItemId());
        }

        return this.itemDefinition;
    }

    public void onItemAddedToStack(RoomItemFloor floorItem) {
        // override me
    }

    public void onEntityPreStepOn(RoomEntity entity) {
        // override me
    }

    public void onEntityStepOn(RoomEntity entity) {
        // override me
    }

    public void onEntityPostStepOn(RoomEntity entity) {
        // override me
    }

    public void onEntityStepOff(RoomEntity entity) {
        // override me
    }

    public RoomEntity getPusher(){
        return null;
    }

    public void onPositionChanged(Position newPosition) {
        // override me
    }

    public boolean isMovementCancelled(RoomEntity entity) {
        return false;
    }

    public boolean isMovementCancelled(RoomEntity entity, Position position) {
        return this.isMovementCancelled(entity);
    }

    @Override
    public void save() {
        this.getItemData().setData(this.getDataObject());
        this.getRoom().getItemProcess().saveItem(this);
//        StorageContext.getCurrentContext().getRoomItemRepository().saveItem(this.getItemData());
    }

    @Override
    public void saveData() {
        //kek
        this.save();
//        /*if (CometSettings.storageItemQueueEnabled) {
//            ItemStorageQueue.getInstance().queueSaveData(this);
//        } else {
//            RoomItemData.saveData(this.getId(), this.getDataObject());
//        }*/
//
//        this.getItemData().setData(this.getDataObject());
//        StorageContext.getCurrentContext().getRoomItemRepository().saveData(this.getId(), this.getDataObject());

//        MySQLStorageQueues.instance().getItemDataUpdateQueue().add(this.getId(), this.getDataObject());
    }

    @Override
    public void sendUpdate() {
        final Room r = this.getRoom();

        if (r != null) {
            r.getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(this));
        }
    }

    public void tempState(int state) {
        this.stateSwitched = true;
        this.coreState = this.getItemData().getData();

        this.getItemData().setData(state);
        this.sendUpdate();
    }

    public void restoreState() {
        this.stateSwitched = false;

        this.getItemData().setData(coreState);
        this.sendUpdate();
    }

    public String getDataObject() {
        return this.getItemData().getData();
    }

    public List<RoomItemFloor> getItemsOnStack() {
        final List<RoomItemFloor> floorItems = Lists.newArrayList();

        final List<AffectedTile> affectedTiles = AffectedTile.getAffectedTilesAt(
                this.getDefinition().getLength(), this.getDefinition().getWidth(),
                this.getPosition().getX(), this.getPosition().getY(), this.getRotation()
        );

        floorItems.addAll(this.getRoom().getItems().getItemsOnSquare(this.getPosition().getX(), this.getPosition().getY()));

        for (final AffectedTile tile : affectedTiles) {
            for (final RoomItemFloor floorItem : this.getRoom().getItems().getItemsOnSquare(tile.x, tile.y)) {
                if (!floorItems.contains(floorItem)) floorItems.add(floorItem);
            }
        }

        return floorItems;
    }

    public List<RoomEntity> getEntitiesOnItem() {
        final List<RoomEntity> entities = Lists.newArrayList();

        entities.addAll(this.getRoom().getEntities().getEntitiesAt(this.getPosition()));

        for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
            final List<RoomEntity> entitiesOnTile = this.getRoom().getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

            entities.addAll(entitiesOnTile);
        }

        return entities;
    }

    public List<RoomEntity> getEntitiesRedOnItem() {
        final List<RoomEntity> entities = Lists.newArrayList();

        if (nearestPlayerEntity().getGameTeam().equals(GameTeam.RED)) {
            entities.addAll(this.getRoom().getEntities().getEntitiesAt(this.getPosition()));

            for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
                final List<RoomEntity> entitiesOnTile = this.getRoom().getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                entities.addAll(entitiesOnTile);
            }
        }


        return entities;
    }

    public List<RoomEntity> getEntitiesGreenOnItem() {
        final List<RoomEntity> entities = Lists.newArrayList();

        if (nearestPlayerEntity().getGameTeam().equals(GameTeam.GREEN)) {
            entities.addAll(this.getRoom().getEntities().getEntitiesAt(this.getPosition()));

            for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
                final List<RoomEntity> entitiesOnTile = this.getRoom().getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                entities.addAll(entitiesOnTile);
            }
        }


        return entities;
    }

    public List<RoomEntity> getEntitiesBlueOnItem() {
        final List<RoomEntity> entities = Lists.newArrayList();

        if (nearestPlayerEntity().getGameTeam().equals(GameTeam.BLUE)) {
            entities.addAll(this.getRoom().getEntities().getEntitiesAt(this.getPosition()));

            for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
                final List<RoomEntity> entitiesOnTile = this.getRoom().getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                entities.addAll(entitiesOnTile);
            }
        }


        return entities;
    }

    public List<RoomEntity> getEntitiesYellowOnItem() {
        final List<RoomEntity> entities = Lists.newArrayList();

        if (nearestPlayerEntity().getGameTeam().equals(GameTeam.YELLOW)) {
            entities.addAll(this.getRoom().getEntities().getEntitiesAt(this.getPosition()));

            for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
                final List<RoomEntity> entitiesOnTile = this.getRoom().getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                entities.addAll(entitiesOnTile);
            }
        }


        return entities;
    }


    public ArrayList<Position> getPositions() {
        final ArrayList<Position> positions = new ArrayList<>();

        positions.add(new Position(this.getPosition().getX(), this.getPosition().getY()));

        for (final AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
            final Position position = new Position(affectedTile.x, affectedTile.y);

            if (this.getRoom().getMapping().isValidPosition(position)) {
                if (!positions.contains(position)) {
                    positions.add(position);
                }
            }
        }

        return positions;
    }

    public Position getPartnerTile() {
        if (this.getDefinition().getLength() != 2) return null;

        for (final AffectedTile affTile : AffectedTile.getAffectedBothTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
            if (affTile.x == this.getPosition().getX() && affTile.y == this.getPosition().getY()) continue;

            return new Position(affTile.x, affTile.y);
        }

        return null;
    }

    public RoomEntity getCollision() {
        return this.collidedEntity;
    }

    public void setCollision(RoomEntity entity) {
        this.collidedEntity = entity;
    }

    public void nullifyCollision() {
        this.collidedEntity = null;
    }

    public double getOverrideHeight() {
        return -1d;
    }

    public boolean hasQueuedSave() {
        return hasQueuedSave;
    }

    public void setHasQueuedSave(boolean hasQueuedSave) {
        this.hasQueuedSave = hasQueuedSave;
    }

    public boolean isStateSwitched() {
        return stateSwitched;
    }

    public void setStateSwitched(boolean stateSwitched) {
        this.stateSwitched = stateSwitched;
    }

    public int getRotation() {
        return this.getItemData().getRotation();
    }

    public void setRotation(int rotation) {
        this.getItemData().setRotation(rotation);
    }

    public int getLastStartDir() {
        return this.lastStartDir;
    }

    public void setLastStartDir(int startDir) {
        this.lastStartDir = startDir;
    }
}
