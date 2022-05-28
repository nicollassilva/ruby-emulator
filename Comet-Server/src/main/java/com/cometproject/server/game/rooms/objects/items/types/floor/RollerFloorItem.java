package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.events.types.RollerFloorItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.AdvancedFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.rooms.types.mapping.RoomTileStatusType;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


public class RollerFloorItem extends AdvancedFloorItem<RollerFloorItemEvent> {
    private final RollerFloorItemEvent event;
    private boolean cycleCancelled;
    private final Set<RoomEntity> movedEntities;
    private final ArrayList<RoomEntity> stepOnRoller;

    public RollerFloorItem(final RoomItemData itemData, final Room room) {
        super(itemData, room);
        this.cycleCancelled = false;
        this.movedEntities = new ConcurrentHashSet<>();
        this.stepOnRoller = new ArrayList<>();

        this.queueEvent(this.event = new RollerFloorItemEvent(this.getTickCount()));
    }

    @Override
    public void onLoad() {
        this.event.setTotalTicks(this.getTickCount());
        this.queueEvent(this.event);
    }

    @Override
    public void onPlaced() {
        this.event.setTotalTicks(this.getTickCount());
        this.queueEvent(this.event);
    }

    @Override
    public void onEntityStepOn(final RoomEntity entity) {
        event.setTotalTicks(this.getTickCount());
        this.stepOnRoller.add(entity);
    }

    @Override
    public void onEntityStepOff(final RoomEntity entity) {
    }

    @Override
    public void onItemAddedToStack(final RoomItemFloor floorItem) {
        event.setTotalTicks(this.getTickCount());
    }

    @Override
    public void onEventComplete(final RollerFloorItemEvent event) {
        if (this.cycleCancelled) {
            this.cycleCancelled = false;
        }

        synchronized (this) {
            this.handleItems();
            this.handleEntities();
        }

        this.movedEntities.clear();
        this.stepOnRoller.clear();

        event.setTotalTicks(this.getTickCount());
        this.queueEvent(event);
    }

    private void handleEntities() {
        final Position sqInfront = this.getPosition().squareInFront(this.getRotation());

        if (!this.getRoom().getMapping().isValidPosition(sqInfront)) {
            return;
        }

        final RoomTile tile = this.getRoom().getMapping().getTile(sqInfront);
        boolean retry = false;
        final List<RoomEntity> entities = this.stepOnRoller.size() > 0
                ? this.stepOnRoller
                : this.getRoom().getEntities().getEntitiesAt(this.getPosition());

        final Iterator<RoomEntity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            final RoomEntity entity = entityIterator.next();
            final boolean isNotOnRoller = entity.getPosition().getX() != this.getPosition().getX() || entity.getPosition().getY() != this.getPosition().getY();
            final boolean hasEntityHoldingRoller = entityIterator.hasNext();
            if (isNotOnRoller && !hasEntityHoldingRoller) {
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
            } else {
                WiredTriggerWalksOffFurni.executeTriggers(entity, this);
                final double toHeight = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY()).getWalkHeight();
                final Position entityPos = this.getPosition().copy();
                entityPos.setZ(entity.getPosition().getZ());

                this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(entityPos, new Position(sqInfront.getX(), sqInfront.getY(), toHeight), this.getVirtualId(), entity.getId(), 0));

                if (tile.getStatus() == RoomTileStatusType.SIT) {
                    entity.setRolling(true);
                }

                this.onEntityStepOff(entity);
                this.movedEntities.add(entity);

                entity.updateAndSetPosition(new Position(sqInfront.getX(), sqInfront.getY(), toHeight));
                entity.markNeedsUpdate(false);
            }

            if(hasEntityHoldingRoller){
                break;
            }
        }

        if (retry) {
            this.cycleCancelled = true;
        }
    }

    private void handleItems() {
        final List<RoomItemFloor> floorItems = this.getRoom().getItems().getItemsOnSquare(this.getPosition().getX(), this.getPosition().getY());

        if (floorItems.size() < 2) {
            return;
        }

        int rollerCount = 0;

        for (final RoomItemFloor f : floorItems) {
            if (f instanceof RollerFloorItem) {
                ++rollerCount;
            }
        }

        if (rollerCount > 1) {
            return;
        }

        final Position sqInfront = this.getPosition().squareInFront(this.getRotation());
        final List<RoomItemFloor> itemsSq = this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY());
        final RoomTile nextTile = this.getRoom().getMapping().getTile(sqInfront);

        boolean noItemsOnNext = false;
        Position position = null;

        final Map<Integer, Double> slidingItems = Maps.newHashMap();

        for (final RoomItemFloor floor : floorItems) {
            if (floor.getPosition().getX() != this.getPosition().getX() && floor.getPosition().getY() != this.getPosition().getY()) {
                continue;
            }

            if (floor instanceof RollerFloorItem) {
                continue;
            }

            if (floor.getPosition().getZ() <= this.getPosition().getZ()) {
                continue;
            }

            if (!floor.getDefinition().canStack() && !(floor instanceof RollableFloorItem) && floor.getTile().getTopItem() != floor.getId()) {
                continue;
            }

            if (position == null) {
                position = floor.getPosition().copy();
            }

            double height = nextTile.getStackHeight();
            boolean hasRoller = false;

            for (final RoomItemFloor iq : itemsSq) {
                if (iq instanceof RollerFloorItem) {
                    hasRoller = true;

                    if (iq.getPosition().getZ() == this.getPosition().getZ()) {
                        continue;
                    }

                    height -= this.getPosition().getZ();
                    height += iq.getPosition().getZ();
                }
            }

            if (!hasRoller || noItemsOnNext) {
                //height -= 0.5;
                noItemsOnNext = true;
            }

            if (!this.getRoom().getMapping().isValidStep(null, new Position(floor.getPosition().getX(), floor.getPosition().getY(), floor.getPosition().getZ()), sqInfront, true, false, false, false, true) ||
                    this.getRoom().getEntities().positionHasEntity(sqInfront, this.movedEntities) ||
                    (nextTile.getTopItemInstance() != null && !nextTile.getTopItemInstance().getDefinition().canStack())) {
                return;
            }

            slidingItems.put(floor.getVirtualId(), height);
            floor.getPosition().setX(sqInfront.getX());
            floor.getPosition().setY(sqInfront.getY());
            floor.getPosition().setZ(height);

            for (final RoomEntity roomEntity : this.movedEntities) {
                floor.onEntityStepOn(roomEntity);
            }

            this.getRoom().getItemProcess().saveItem(floor);
        }

        if (slidingItems.size() != 0) {
            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(position, sqInfront.copy(), this.getVirtualId(), 0, slidingItems));
        }

        this.getRoom().getMapping().updateTile(this.getPosition().getX(), this.getPosition().getY());
        this.getRoom().getMapping().updateTile(sqInfront.getX(), sqInfront.getY());

        for (final RoomItemFloor nextItem : this.getRoom().getItems().getItemsOnSquare(sqInfront.getX(), sqInfront.getY())) {
            for (final RoomItemFloor floor2 : floorItems) {
                nextItem.onItemAddedToStack(floor2);
            }
        }
    }

    private int getTickCount() {
        return RoomItemFactory.getProcessTime(((double) this.getRoom().getData().getRollerSpeedLevel()) / 2);
    }
}
