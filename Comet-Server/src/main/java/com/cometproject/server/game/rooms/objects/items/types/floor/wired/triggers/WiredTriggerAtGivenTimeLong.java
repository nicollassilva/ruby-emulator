package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;


public class WiredTriggerAtGivenTimeLong extends WiredTriggerAtGivenTime {
    private static final int PARAM_TICK_LENGTH = 0;

    private boolean needsReset = false;

    public WiredTriggerAtGivenTimeLong(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        this.getWiredData().getParams().putIfAbsent(PARAM_TICK_LENGTH, 2); // 10s
    }

    public static boolean executeTriggers(Room room, int timer) {
        boolean wasExecuted = false;

        for (final WiredTriggerAtGivenTimeLong wiredItem : getTriggers(room, WiredTriggerAtGivenTimeLong.class)) {
            if(wiredItem == null) continue;

            if (timer >= wiredItem.getTime() && !wiredItem.needsReset()) {
                if (wiredItem.evaluate(null, null)) {
                    wasExecuted = true;

                    wiredItem.setNeedsReset(true);
                }
            }
        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 3;
    }

    public int getTime() {
        return this.getWiredData().getParams().get(PARAM_TICK_LENGTH);
    }

    public void setNeedsReset(boolean needsReset) {
        this.needsReset = needsReset;
    }

    public boolean needsReset() {
        return this.needsReset;
    }
}
