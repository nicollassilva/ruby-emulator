package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredTriggerCollisionPlayer extends WiredTriggerItem {

    public WiredTriggerCollisionPlayer(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 9;
    }

    public static boolean executeTriggers(RoomEntity entity) {
        boolean wasExecuted = false;

        for (final WiredTriggerCollisionPlayer floorItem : getTriggers(entity.getRoom(), WiredTriggerCollisionPlayer.class)) {
            if(floorItem == null) continue;

            wasExecuted = floorItem.evaluate(entity, null);

        }
        return wasExecuted;
    }
}
