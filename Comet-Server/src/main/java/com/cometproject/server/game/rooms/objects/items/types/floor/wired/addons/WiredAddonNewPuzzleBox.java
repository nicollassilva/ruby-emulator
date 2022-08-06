package com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;

public class WiredAddonNewPuzzleBox extends RoomItemFloor {
    public WiredAddonNewPuzzleBox(final RoomItemData itemData, final Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(final RoomEntity entity, final int requestData, final boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!this.getPosition().touching(entity.getPosition())) {
                entity.moveTo(this.getPosition().squareInFront(this.rotation).getX(), this.getPosition().squareBehind(this.rotation).getY());
                return false;
            }
        }

        if (entity != null) {
            int NewX, NewY;

            if (entity.getPosition().getX() == this.getPosition().getX() && entity.getPosition().getY() - 1 == this.getPosition().getY()) {
                NewX = this.getPosition().getX();
                NewY = this.getPosition().getY() - 1;
            } else if (entity.getPosition().getX() + 1 == this.getPosition().getX() && entity.getPosition().getY() == this.getPosition().getY()) {
                NewX = this.getPosition().getX() + 1;
                NewY = this.getPosition().getY();
            } else if (entity.getPosition().getX() == this.getPosition().getX() && entity.getPosition().getY() + 1 == this.getPosition().getY()) {
                NewX = this.getPosition().getX();
                NewY = this.getPosition().getY() + 1;
            } else if (entity.getPosition().getX() - 1 == this.getPosition().getX() && entity.getPosition().getY() == this.getPosition().getY()) {
                NewX = this.getPosition().getX() - 1;
                NewY = this.getPosition().getY();
            } else {
                final Position finalBoxPosition = new Position(this.getPosition().squareInFront(this.rotation).getX(), this.getPosition().squareBehind(this.rotation).getY());

                if(this.getRoom().getMapping().isValidPosition(finalBoxPosition)) {
                    entity.moveTo(finalBoxPosition);
                    return false;
                }

                return true;
            }

            double z = this.getPosition().getZ();
            final RoomTile tile = this.getRoom().getMapping().getTile(NewX, NewY);

            if (tile != null) {
                z = tile.getStackHeight();
            }

            final Position currentPosition = this.getPosition().copy();
            final Position newPosition = new Position(NewX, NewY, z);

            if (this.getRoom().getMapping().isValidPosition(newPosition) && this.getRoom().getItems().moveFloorItemWired(this, newPosition, this.getRotation(), true, true)) {
                this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, this.getVirtualId()));
                this.save();
                entity.moveTo(currentPosition.getX(), currentPosition.getY());
            }

        }

        return true;
    }
}
