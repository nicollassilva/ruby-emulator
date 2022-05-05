package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;


public class SeatFloorItem extends RoomItemFloor {

    public SeatFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity) entity;

            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId())
                    && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        this.toggleInteract(true);
        this.sendUpdate();

        if (this instanceof AdjustableHeightSeatFloorItem) {
            for (RoomEntity sitter : this.getEntitiesOnItem()) {
                this.onEntityStepOn(sitter, true);
            }
        }

        this.saveData();
        return true;
    }

    public void onEntityStepOn(RoomEntity entity, boolean instantUpdate) {
        for (final Position position : this.getPositions()) {
            if(entity.getPosition().getX() == position.getX() && entity.getPosition().getY() == position.getY()) {
                entity.sit(getSitHeight(), getRotation());
            }
        }

        if (instantUpdate || entity.isRolling()) {
            entity.setRolling(false);
            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entity));
        } else {
            entity.markNeedsUpdate();
        }
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.onEntityStepOn(entity, false);
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (entity.hasStatus(RoomEntityStatus.SIT)) {
            entity.removeStatus(RoomEntityStatus.SIT);
        }

        entity.markNeedsUpdate();
    }

    public double getSitHeight() {
        return this.getDefinition().getHeight();
    }
}
