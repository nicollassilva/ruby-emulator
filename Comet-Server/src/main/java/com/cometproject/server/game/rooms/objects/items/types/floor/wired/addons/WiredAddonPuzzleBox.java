package com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.types.ItemPathfinder;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;

import java.util.List;

public class WiredAddonPuzzleBox extends RoomItemFloor
{
    public WiredAddonPuzzleBox(final RoomItemData itemData, final Room room) {
        super(itemData, room);
    }

    private Position getNextPosition(final Position nextPosition) {
        return nextPosition.squareInFront(this.getRotation());
    }

    private boolean isValidRoll(final Position nextPosition) {
        final List<Square> path = ItemPathfinder.getInstance().makePath(this, nextPosition);
        return path != null && !path.isEmpty();
    }

    private static void roll(final RoomItemFloor item, final Position from, final Position to, final Room room) {
        room.getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from.copy(), to.copy(), item.getVirtualId(), 0, item.getVirtualId()));
    }

    private static Position calculatePosition(final int x, final int y, final int playerRotation) {
        return Position.calculatePosition(x, y, playerRotation, false, 1);
    }

    private void moveTo(final Position pos, final int rotation, final RoomEntity entity, final Position old) {
        final RoomTile newTile = this.getRoom().getMapping().getTile(pos);
        if (newTile != null) {
            pos.setZ(newTile.getStackHeight());
            roll(this, this.getPosition(), pos, this.getRoom());
            final RoomTile tile = this.getRoom().getMapping().getTile(this.getPosition());
            this.setRotation(rotation);
            this.getPosition().setX(pos.getX());
            this.getPosition().setY(pos.getY());
            if (tile != null) {
                tile.reload();
            }
            entity.moveTo(old);
            newTile.reload();
            for (final RoomItemFloor floorItem : this.getRoom().getItems().getItemsOnSquare(pos.getX(), pos.getY())) {
                floorItem.onItemAddedToStack(this);
            }
            this.getPosition().setZ(pos.getZ());
        }
    }

    @Override
    public boolean onInteract(final RoomEntity entity, final int requestData, final boolean isWiredTrigger) {
        if (isWiredTrigger || entity == null) {
            return false;
        }
        if (!this.getPosition().touching(entity.getPosition())) {
            return true;
        }
        final int rotation = Position.calculateRotation(entity.getPosition().getX(), entity.getPosition().getY(), this.getPosition().getX(), this.getPosition().getY(), false);
        if (!entity.hasStatus(RoomEntityStatus.SIT) && !entity.hasStatus(RoomEntityStatus.LAY)) {
            entity.setBodyRotation(rotation);
            entity.setHeadRotation(rotation);
            entity.markNeedsUpdate();
        }
        final Position currentPosition = new Position(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
        if (this.isValidRoll(this.getNextPosition(currentPosition))) {
            final Position newPosition = calculatePosition(this.getPosition().getX(), this.getPosition().getY(), rotation);
            this.moveTo(newPosition, rotation, entity, currentPosition);
            return true;
        }
        return false;
    }
}
