package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredTriggerCustomTotalIdle extends WiredTriggerItem {
    private static final int PARAM_TICK_LENGTH = 0;

    public WiredTriggerCustomTotalIdle(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        this.getWiredData().getParams().putIfAbsent(PARAM_TICK_LENGTH, 30); // 15s
    }

    public static boolean executeTriggers(PlayerEntity playerEntity) {
        boolean wasExecuted = false;

        for (final WiredTriggerCustomTotalIdle trigger : getTriggers(playerEntity.getRoom(), WiredTriggerCustomTotalIdle.class)) {
            if(trigger == null) continue;

            if(playerEntity.getAfkTime() == trigger.getTime() || (playerEntity.getIdleTime() == trigger.getTime() && !playerEntity.isWalking())) {
                wasExecuted |= trigger.evaluate(playerEntity, trigger);
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
        return Math.max(5, this.getWiredData().getParams().get(PARAM_TICK_LENGTH));
    }
}
