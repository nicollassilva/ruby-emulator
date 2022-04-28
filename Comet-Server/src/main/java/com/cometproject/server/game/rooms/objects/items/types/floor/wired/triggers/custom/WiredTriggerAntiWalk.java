package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredTriggerAntiWalk extends WiredTriggerItem {
    private static final int PARAM_TICK_LENGTH = 0;

    public WiredTriggerAntiWalk(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        if (this.getWiredData().getParams().get(PARAM_TICK_LENGTH) == null) {
            this.getWiredData().getParams().put(PARAM_TICK_LENGTH, 30); // 15s
        }
    }

    public static boolean executeTriggers(PlayerEntity entity) {
        boolean wasExecuted = false;

        for (RoomItemFloor floorItem : getTriggers(entity.getRoom(), WiredTriggerAntiWalk.class)) {
            WiredTriggerAntiWalk trigger = ((WiredTriggerAntiWalk) floorItem);//

            if (entity.getIdleTimeWiredWalk() >= trigger.getTime()) {
                //entity.resetIdleTimeWiredWalk();
                wasExecuted = trigger.evaluate(entity, trigger);
            }

            if (wasExecuted) {
                entity.resetIdleTimeWiredWalk();
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
        return 3;
    }

    public int getTime() {
        return this.getWiredData().getParams().get(PARAM_TICK_LENGTH);
    }
}
