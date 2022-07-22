package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerCollision;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.utilities.Direction;

import java.util.concurrent.atomic.AtomicInteger;

public class WiredActionMoveToDirection extends WiredActionItem {
    private static final int PARAM_START_DIR = 0;
    private static final int PARAM_ACTION_WHEN_BLOCKED = 1;

    private static final int ACTION_WAIT = 0;
    private static final int ACTION_TURN_RIGHT_45 = 1;
    private static final int ACTION_TURN_RIGHT_90 = 2;
    private static final int ACTION_TURN_LEFT_45 = 3;
    private static final int ACTION_TURN_LEFT_90 = 4;
    private static final int ACTION_TURN_BACK = 5;
    private static final int ACTION_TURN_RANDOM = 6;

    public WiredActionMoveToDirection(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 13;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (this.getWiredData().getParams().size() != 2) {
            return;
        }

        final int startDir = this.getWiredData().getParams().get(PARAM_START_DIR);

        synchronized (this.getWiredData().getSelectedIds()) {
            for (final long itemId : this.getWiredData().getSelectedIds()) {
                final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

                if (floorItem == null) continue;

                if (floorItem.getMoveDirection() == -1 || floorItem.getLastStartDir() == startDir) {
                    floorItem.setMoveDirection(startDir);
                    this.setLastStartDir(startDir);
                }

                this.moveItem(floorItem, new AtomicInteger(0));
            }
        }
    }

    private void moveItem(RoomItemFloor floorItem, AtomicInteger tries) {
        final Position currentPosition = floorItem.getPosition().copy();
        final Position nextPosition = floorItem.getPosition().squareInFront(floorItem.getMoveDirection());

        if (this.getRoom().getItems().moveFloorItemWired(floorItem, floorItem.getPosition().squareInFront(floorItem.getMoveDirection()), floorItem.getRotation(), true, true, true)) {
            nextPosition.setZ(floorItem.getPosition().getZ());
            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, nextPosition, this.getVirtualId(), 0, floorItem.getVirtualId()));
        } else {
            tries.incrementAndGet();

            if (tries.get() < 4)
                this.attemptBlockedAction(floorItem, tries);
        }
    }

    private void attemptBlockedAction(RoomItemFloor floorItem, AtomicInteger tries) {
        int movementDirection = floorItem.getMoveDirection();

        final int actionWhenBlocked = this.getWiredData().getParams().get(PARAM_ACTION_WHEN_BLOCKED);
        final Position position = floorItem.getPosition().squareInFront(floorItem.getMoveDirection());
        final RoomTile roomTile = this.getRoom().getMapping().getTile(position);

        if (roomTile != null) {
            if (roomTile.getEntity() != null) {
                WiredTriggerCollision.executeTriggers(roomTile.getEntity(), floorItem);
                return;
            }
        }

        switch (actionWhenBlocked) {
            case ACTION_TURN_BACK:
                movementDirection = Direction.get(movementDirection).invert().num;
                break;

            case ACTION_TURN_RANDOM:
                movementDirection = getRandomDirection(floorItem);
                break;

            case ACTION_TURN_RIGHT_45:
                movementDirection = this.getNextDirection(movementDirection);
                break;

            case ACTION_TURN_RIGHT_90:
                movementDirection = this.clockwise(movementDirection, 2);
                break;

            case ACTION_TURN_LEFT_45:
                movementDirection = this.getPreviousDirection(movementDirection);
                break;

            case ACTION_TURN_LEFT_90:
                movementDirection = this.antiClockwise(movementDirection, 2);
                break;
        }

        floorItem.setMoveDirection(movementDirection);
        this.moveItem(floorItem, tries);
    }

    public int getRandomDirection(RoomItemFloor floorItem) {
        int limit = 0;
        int movementDirection;

        while (limit <= 7) {
            movementDirection = Direction.random().num;

            final Position randomPosition = floorItem.getPosition().squareInFront(movementDirection);
            final RoomTile randomRoomTile = this.getRoom().getMapping().getTile(randomPosition);

            if (randomRoomTile != null && randomRoomTile.canPlaceItemHere()) {
                return movementDirection;
            }

            limit++;
        }

        return 0;
    }

    private int clockwise(int movementDirection, int times) {
        for (int i = 0; i < times; i++) {
            movementDirection = this.getNextDirection(movementDirection);
        }

        return movementDirection;
    }

    private int antiClockwise(int movementDirection, int times) {
        for (int i = 0; i < times; i++) {
            movementDirection = this.getPreviousDirection(movementDirection);
        }

        return movementDirection;
    }

    private int getNextDirection(int movementDirection) {
        if (movementDirection == 7) {
            return 0;
        }

        return movementDirection + 1;
    }

    private int getPreviousDirection(int movementDirection) {
        if (movementDirection == 0) {
            return 7;
        }

        return movementDirection - 1;
    }
}
