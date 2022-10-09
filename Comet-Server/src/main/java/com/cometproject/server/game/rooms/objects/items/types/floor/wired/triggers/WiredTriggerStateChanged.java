package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;


public class WiredTriggerStateChanged extends WiredTriggerItem {

    public WiredTriggerStateChanged(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static boolean executeTriggers(RoomEntity entity, RoomItemFloor floorItem) {
        System.out.println("[--------] execute trigger");

        boolean wasExecuted = false;

        for (final WiredTriggerStateChanged wiredItem : getTriggers(entity.getRoom(), WiredTriggerStateChanged.class)) {
            if(wiredItem == null) continue;

            if (wiredItem.getWiredData().getSelectedIds().contains(floorItem.getId())) {
                wasExecuted = wiredItem.evaluate(entity, floorItem);
            }
        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 1;
    }
}