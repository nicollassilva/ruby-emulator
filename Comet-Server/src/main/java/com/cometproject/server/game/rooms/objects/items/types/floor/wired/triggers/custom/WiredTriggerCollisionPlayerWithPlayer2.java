package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredTriggerCollisionPlayerWithPlayer2 extends WiredTriggerItem {

    public WiredTriggerCollisionPlayerWithPlayer2 (RoomItemData roomItemData, Room room) {
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

        for (RoomItemFloor floorItem : getTriggers(entity.getRoom(), WiredTriggerCollisionPlayerWithPlayer2.class)) {
            WiredTriggerCollisionPlayerWithPlayer2 trigger = ((WiredTriggerCollisionPlayerWithPlayer2) floorItem);
            wasExecuted = trigger.evaluate(entity, null);

        }
        return wasExecuted;
    }
}

