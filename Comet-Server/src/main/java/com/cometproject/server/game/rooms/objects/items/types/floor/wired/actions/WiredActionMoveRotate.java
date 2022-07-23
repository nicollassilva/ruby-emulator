package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.DiceFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;

import java.util.Random;


public class WiredActionMoveRotate extends WiredActionItem {
    private static final int PARAM_MOVEMENT = 0;
    private static final int PARAM_ROTATION = 1;
    private static final Random random = new Random();

    public WiredActionMoveRotate(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 4;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (this.getWiredData().getParams().size() != 2) {
            return;
        }

        final int movement = this.getWiredData().getParams().get(PARAM_MOVEMENT);
        final int rotation = this.getWiredData().getParams().get(PARAM_ROTATION);

        synchronized (this.getWiredData().getSelectedIds()) {
            for (final long itemId : this.getWiredData().getSelectedIds()) {
                final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

                if (floorItem == null || floorItem instanceof DiceFloorItem) continue;

                final Position currentPosition = floorItem.getPosition().copy();
                final Position newPosition = this.getRandomPosition(movement, currentPosition, this.getRoom());
                final int newRotation = this.handleRotation(floorItem.getRotation(), rotation);
                final boolean rotationChanged = newRotation != floorItem.getRotation();

                if (this.getRoom().getItems().moveFloorItemWired(floorItem, newPosition, newRotation, true, true, true)) {
                    if (!rotationChanged) {
                        this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
                    } else {
                        this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
                    }
                }

                floorItem.save();
            }
        }
    }

    private static Position handleMovement(Position point, int movementType) {
        final boolean dir = Math.random() < 0.5;

        switch (movementType) {
            case 0:
                return point;

            case 1:
                // Random
                final int movement = random.nextInt((4 - 1) + 1 + 1);

                if (movement == 1) {
                    point = handleMovement(point, 4);
                } else if (movement == 2) {
                    point = handleMovement(point, 5);
                } else if (movement == 3) {
                    point = handleMovement(point, 6);
                } else {
                    point = handleMovement(point, 7);
                }
                break;

            case 2:
                if (dir) {
                    point = handleMovement(point, 7);
                } else {
                    point = handleMovement(point, 5);
                }
                break;

            case 3:
                if (dir) {
                    point = handleMovement(point, 4);
                } else {
                    point = handleMovement(point, 6);
                }
                break;

            case 4:
                // Up
                point.setY(point.getY() - 1);
                break;

            case 5:
                // Right
                point.setX(point.getX() + 1);
                break;

            case 6:
                // Down
                point.setY(point.getY() + 1);
                break;

            case 7:
                // Left
                point.setX(point.getX() - 1);
                break;
        }

        return point;
    }

    private int handleRotation(int rotation, int rotationType) {
        switch (rotationType) {
            case 0:
                return rotation;

            case 1:
                // Clockwise
                rotation = rotation + 2;

                if (rotation > 6)
                    rotation = 0;
                break;

            case 2:
                // Counterclockwise
                rotation = rotation - 2;

                if (rotation > 6)
                    rotation = 6;
                break;

            case 3:
                // Random
                int i = random.nextInt((2 - 1) + 1 + 1);

                if (i == 1) {
                    rotation = handleRotation(rotation, 1);
                } else {
                    rotation = handleRotation(rotation, 2);
                }
                break;

        }

        return rotation;
    }

    public static Position getRandomPosition(int movement, Position currentPosition, Room room) {
        int limit = 5;
        Position newPosition;

        while (limit <= 12) {
            newPosition = handleMovement(currentPosition.copy(), movement);

            final RoomTile randomRoomTile = room.getMapping().getTile(newPosition);
            if (randomRoomTile != null) {
                if (randomRoomTile.canPlaceItemHere() && randomRoomTile.canStack()) {
                    newPosition.setZ(randomRoomTile.getStackHeight());

                    return newPosition;
                }
            }

            limit++;
        }

        return currentPosition;
    }
}
