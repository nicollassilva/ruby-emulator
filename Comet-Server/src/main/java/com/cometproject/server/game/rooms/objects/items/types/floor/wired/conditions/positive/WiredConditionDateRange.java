package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredConditionDateRange extends WiredConditionItem {
    private final static int DATE_START = 0;
    private final static int DATE_END = 1;

    public WiredConditionDateRange(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 24;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {

        int DateStart = this.getWiredData().getParams().get(DATE_START);
        int DateEnd = this.getWiredData().getParams().get(DATE_END);

        if(DateStart != 0 && DateEnd != 0) {
            if(Comet.getTime() > DateStart && Comet.getTime() < DateEnd) {
                return true;
            }
        }

        return false;
    }
}
