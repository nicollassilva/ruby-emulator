package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.events.types.RollerFloorItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.AdvancedFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.groups.GroupGateFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;

import java.util.List;
import java.util.Set;


public class RollerFloorItem extends AdvancedFloorItem<RollerFloorItemEvent> {
    private final boolean hasRollScheduled = false;
    private final long lastTick = 0;
    private final Set<Integer> entitiesOnRoller = new ConcurrentHashSet<>();
    private final RollerFloorItemEvent event;

    public RollerFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        this.event = new RollerFloorItemEvent(0);
        this.queueEvent(event);
    }

    @Override
    public void onLoad() {
        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    @Override
    public void onPlaced() {
        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (entity.isWalking()) return;

        if (this.entitiesOnRoller.contains(entity.getId())) {
            return;
        }

        this.entitiesOnRoller.add(entity.getId());
        event.setTotalTicks(this.getTickCount());
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!this.entitiesOnRoller.contains(entity.getId())) {
            return;
        }

        this.entitiesOnRoller.remove(entity.getId());
    }

    @Override
    public void onItemAddedToStack(RoomItemFloor floorItem) {
        if (event.getCurrentTicks() < 1) {
            event.setTotalTicks(this.getTickCount());
        }
    }

    @Override
    public void onEventComplete(RollerFloorItemEvent event) {
        this.handleItems();
        this.handleEntities();

        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    private void handleEntities() {
        final Position sqInfront = this.getPosition().squareInFront(this.getRotation());

        if (!this.getRoom().getMapping().isValidPosition(sqInfront)) {
            return;
        }

        boolean retry = false;

        final List<RoomEntity> entities = this.getRoom().getEntities().getEntitiesAt(this.getPosition());

        for (final RoomEntity entity : entities) {
            if (entity.getPosition().getX() != this.getPosition().getX() && entity.getPosition().getY() != this.getPosition().getY()) {
                continue;
            }

            if (!this.entitiesOnRoller.contains(entity.getId())) {
                continue;
            }

            if (entity.getPositionToSet() != null) {
                continue;
            }

            if (!this.getRoom().getMapping().isValidStep(entity.getId(), entity.getPosition(), sqInfront, true, false, false, false, false) || this.getRoom().getEntities().positionHasEntity(sqInfront)) {
                retry = true;
                break;
            }

            if (entity.isWalking()) {
                continue;
            }

            if (sqInfront.getX() == this.getRoom().getModel().getDoorX() && sqInfront.getY() == this.getRoom().getModel().getDoorY()) {
                entity.leaveRoom(false, false, true);
                continue;
            }

            WiredTriggerWalksOffFurni.executeTriggers(entity, this);

            final double toHeight = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY()).getWalkHeight();
            final RoomTile oldTile = this.getRoom().getMapping().getTile(entity.getPosition().getX(), entity.getPosition().getY());
            final RoomTile newTile = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY());
            final Position realPosition = entity.getPosition();

            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(
                    entity.getPosition(),
                    new Position(sqInfront.getX(), sqInfront.getY(), toHeight),
                    this.getVirtualId(), entity.getId(), 0
            ));

            if (oldTile != null) {
                oldTile.getEntities().remove(entity);
            }

            this.onEntityStepOff(entity);

            if(newTile != null && !(newTile.getTopItemInstance() instanceof RollerFloorItem) && entity.isWalking()) {
                if (entity.getWalkingGoal().getX() != realPosition.getX() || entity.getWalkingGoal().getY() != realPosition.getY()) {
                    continue;
                }
            }

            if (newTile != null) {
                newTile.getEntities().add(entity);
            }

            for (final RoomItemFloor nextItem : this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY())) {
                if (nextItem instanceof GroupGateFloorItem) break;

                if (entity instanceof PlayerEntity) {
                    WiredTriggerWalksOnFurni.executeTriggers(entity, nextItem);
                }

                nextItem.onEntityStepOn(entity);
            }

            entity.setPosition(new Position(sqInfront.getX(), sqInfront.getY(), toHeight));
        }

        if (retry) {
            this.setTicks(this.getTickCount());
        }
    }

    private void handleItems() {
        final List<RoomItemFloor> floorItems = this.getRoom().getItems().getItemsOnSquare(this.getPosition().getX(), this.getPosition().getY());

        if (floorItems.size() < 2) {
            return;
        }

        // quick check illegal use of rollers
        int rollerCount = 0;

        for (final RoomItemFloor f : floorItems) {
            if (f instanceof RollerFloorItem) {
                rollerCount++;
            }
        }

        if (rollerCount > 1) {
            return;
        }

        final Position sqInfront = this.getPosition().squareInFront(this.getRotation());
        boolean noItemsOnNext = false;

        for (final RoomItemFloor floor : floorItems) {
            if (floor.getPosition().getX() != this.getPosition().getX() && floor.getPosition().getY() != this.getPosition().getY()) {
                continue;
            }

            if (floor instanceof RollerFloorItem || floor.getPosition().getZ() <= this.getPosition().getZ()) {
                continue;
            }

            if (!floor.getDefinition().canStack() && !(floor instanceof RollableFloorItem)) {
                if (floor.getTile().getTopItem() != floor.getId())
                    continue;
            }

            double height = this.getRoom().getMapping().getTile(sqInfront).getStackHeight();

            final List<RoomItemFloor> itemsSq = this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY());

            boolean hasRoller = false;

            for (final RoomItemFloor iq : itemsSq) {
                if (iq instanceof RollerFloorItem) {
                    hasRoller = true;

                    if (iq.getPosition().getZ() != this.getPosition().getZ()) {
                        height -= this.getPosition().getZ();
                        height += iq.getPosition().getZ();
                    }
                }
            }

            if (!hasRoller || noItemsOnNext) {
                //height -= 0.5;
                noItemsOnNext = true;
            }

//            double heightDiff = 0;
//
//            if (itemsSq.size() > 1) {
//                RoomItemFloor item1 = itemsSq.get(0);
//                RoomItemFloor item2 = itemsSq.get(1);
//
//                heightDiff = item1.getPosition().getZ() - item2.getPosition().getZ();
//            }
//
//            if (heightDiff > -2) {
//                if (!this.getRoom().getMapping().isValidStep(new Position(floor.getPosition().getX(), floor.getPosition().getY(), floor.getPosition().getZ()), sqInfront, true) || !this.getRoom().getEntities().positionHasEntity(sqInfront.getX(), sqInfront.getY())) {
//                    this.setTicks(3);
//                    break;
//                }
//            }

            if (!this.getRoom().getMapping().isValidStep(new Position(floor.getPosition().getX(), floor.getPosition().getY(), height), sqInfront, true) || this.getRoom().getEntities().positionHasEntity(sqInfront)) {
                this.setTicks(this.getTickCount());
                return;
            }

            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(new Position(floor.getPosition().getX(), floor.getPosition().getY(), floor.getPosition().getZ()), new Position(sqInfront.getX(), sqInfront.getY(), height), this.getVirtualId(), 0, floor.getVirtualId()));

            floor.getPosition().setX(sqInfront.getX());
            floor.getPosition().setY(sqInfront.getY());
            floor.getPosition().setZ(height);
            floor.save();
        }

        this.getRoom().getMapping().updateTile(this.getPosition().getX(), this.getPosition().getY());
        this.getRoom().getMapping().updateTile(sqInfront.getX(), sqInfront.getY());

        for (final RoomItemFloor nextItem : this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY())) {
            for (final RoomItemFloor floor : floorItems) {
                nextItem.onItemAddedToStack(floor);
            }
        }
    }

    private int getTickCount() {
        return RoomItemFactory.getProcessTime((this.getRoom().getData().getRollerSpeed() ? this.getRoom().getData().getRollerSpeedLevel() : 4.0) / 2);
    }
}