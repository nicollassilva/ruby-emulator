package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredTriggerBotReachedFurni extends WiredTriggerItem {

    public WiredTriggerBotReachedFurni(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 13;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    public static void executeTriggers(RoomEntity entity, RoomItemFloor floorItem, String username) {
        for (final WiredTriggerBotReachedFurni floorItemm : getTriggers(entity.getRoom(), WiredTriggerBotReachedFurni.class)) {
            if(floorItem == null) continue;

            if (floorItemm.getWiredData().getText().isEmpty() || floorItemm.getWiredData().getText().equals(username)) {
                if (floorItemm.getWiredData().getSelectedIds().contains(floorItem.getId())) {
                    floorItemm.evaluate(entity, null);
                }
            }

        }
    }
}
