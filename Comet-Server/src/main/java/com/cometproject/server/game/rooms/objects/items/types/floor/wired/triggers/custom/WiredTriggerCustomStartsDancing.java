package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredTriggerCustomStartsDancing extends WiredTriggerItem {

    public WiredTriggerCustomStartsDancing(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static boolean executeTriggers(PlayerEntity playerEntity, int dance) {
        boolean wasExecuted = false;

        for (WiredTriggerCustomStartsDancing floorItem : getTriggers(playerEntity.getRoom(), WiredTriggerCustomStartsDancing.class)) {
            if(floorItem == null) continue;

            if (!floorItem.getWiredData().getText().isEmpty() && String.valueOf(dance).equals(floorItem.getWiredData().getText().toLowerCase())) {
                    wasExecuted = true;
                    floorItem.evaluate(playerEntity, dance);
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
        return 0;
    }
}
