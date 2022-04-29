package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerAtGivenTime;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerAtGivenTimeLong;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerPeriodically;
import com.cometproject.server.game.rooms.types.Room;

import java.util.List;


public class WiredActionResetTimers extends WiredActionItem {

    public WiredActionResetTimers(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 1;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final List<WiredTriggerAtGivenTime> items = this.getRoom().getItems().getByClass(WiredTriggerAtGivenTime.class);

        items.addAll(this.getRoom().getItems().getByClass(WiredTriggerAtGivenTimeLong.class));

        for (final WiredTriggerAtGivenTime floorItem : items) {
            floorItem.setNeedsReset(false);
        }

        this.getRoom().resetWiredTimer();
    }
}
