package com.cometproject.server.game.rooms.objects.entities.pathfinding.types;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.RoomObject;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Pathfinder;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.RollableFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.football.FootballFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.football.OriginalFootballFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.groups.GroupGateFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionChase;
import com.cometproject.server.game.rooms.types.mapping.RoomEntityMovementNode;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.api.game.rooms.models.RoomTileState;

public class ItemPathfinder extends Pathfinder {
    private static ItemPathfinder pathfinderInstance;

    public static ItemPathfinder getInstance() {
        if (pathfinderInstance == null) {
            pathfinderInstance = new ItemPathfinder();
        }

        return pathfinderInstance;
    }

    @Override
    public boolean isValidStep(RoomObject object, Position from, Position to, boolean lastStep, boolean isRetry, boolean generating) {
        if (from.getX() == to.getX() && from.getY() == to.getY()) {
            return true;
        }

        if (!(to.getX() < object.getRoom().getModel().getSquareState().length)) {
            return false;
        }

        if ((!object.getRoom().getMapping().isValidPosition(to) || (object.getRoom().getModel().getSquareState()[to.getX()][to.getY()] == RoomTileState.INVALID))) {
            return false;
        }

        final int rotation = Position.calculateRotation(from, to);

        if (rotation == 1 || rotation == 3 || rotation == 5 || rotation == 7) {
            RoomTile left = null;
            RoomTile right = null;

            switch (rotation) {
                case 1:
                    left = object.getRoom().getMapping().getTile(from.squareInFront(rotation + 1));
                    right = object.getRoom().getMapping().getTile(to.squareBehind(rotation + 1));
                    break;

                case 3:
                    left = object.getRoom().getMapping().getTile(to.squareBehind(rotation + 1));
                    right = object.getRoom().getMapping().getTile(to.squareBehind(rotation - 1));
                    break;

                case 5:
                    left = object.getRoom().getMapping().getTile(from.squareInFront(rotation - 1));
                    right = object.getRoom().getMapping().getTile(to.squareBehind(rotation - 1));
                    break;

                case 7:
                    left = object.getRoom().getMapping().getTile(to.squareBehind(0));
                    right = object.getRoom().getMapping().getTile(from.squareInFront(rotation - 1));
                    break;
            }

            if (left != null && right != null) {
                if (left.getMovementNode() != RoomEntityMovementNode.OPEN && right.getMovementNode() != RoomEntityMovementNode.OPEN)
                    return false;
            }
        }

        RoomTile tile = object.getRoom().getMapping().getTile(to.getX(), to.getY());

        if (tile == null) {
            return false;
        }

        if (object instanceof FootballFloorItem|| object instanceof OriginalFootballFloorItem) {
            for (RoomItemFloor floor : tile.getItems()) {
                if (floor instanceof GroupGateFloorItem) {
                    return false;
                }
            }

            if (tile.getItems().size() == 1) {
                return tile.getStackHeight() <= 0.5 && tile.canPlaceItemHere();
            }
        }

        if (object instanceof WiredActionChase) {
            int target = ((WiredActionChase) object).getTargetId();

            if (target != -1) {
                for (RoomEntity entity : tile.getEntities()) {
                    if (entity.getId() != target) {
                        return false;
                    }
                }
            }
        }

        if (object instanceof RollableFloorItem) {
            for (RoomEntity entity : tile.getEntities()) {
                return false;
            }
        }

        if (tile.getMovementNode() == RoomEntityMovementNode.CLOSED || (tile.getMovementNode() == RoomEntityMovementNode.END_OF_ROUTE && !lastStep)) {
            return false;
        }

        final double fromHeight = object.getRoom().getMapping().getStepHeight(from);
        final double toHeight = object.getRoom().getMapping().getStepHeight(to);

        if (fromHeight < toHeight && (toHeight - fromHeight) > 1.0) return false;

        return true;
    }
}
