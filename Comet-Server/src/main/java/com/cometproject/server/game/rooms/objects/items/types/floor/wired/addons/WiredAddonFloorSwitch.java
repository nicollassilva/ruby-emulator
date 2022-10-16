package com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;


public class WiredAddonFloorSwitch extends RoomItemFloor {
    public WiredAddonFloorSwitch(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        final PlayerEntity playerEntity = ((PlayerEntity) entity);

        if(entity == null) {
            return false;
        }

        if (!this.getPosition().touching(entity.getPosition())) {
            if(!playerEntity.isFreeze() || !playerEntity.usingTeleportItem()) {
                entity.moveTo(this.getPosition().squareBehind(this.getRotation()).getX(), this.getPosition().squareBehind(this.getRotation()).getY());
                return false;
            }
        }

        if (this.getPosition().touching(entity.getPosition())) {
            this.toggleInteract(true);

            this.sendUpdate();
            this.saveData();
        }

        return true;
    }
}
