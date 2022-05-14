package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

import java.util.Optional;


public class WiredTriggerCollision extends WiredTriggerItem {

    public WiredTriggerCollision(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static boolean executeTriggers(RoomEntity entity, RoomItemFloor collidingItem) {
        boolean wasExecuted = false;

        for (final WiredTriggerCollision floorItem : getTriggers(entity.getRoom(), WiredTriggerCollision.class)) {
            if(floorItem == null) continue;

            wasExecuted = floorItem.evaluate(entity, collidingItem);
        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 9;
    }

}
