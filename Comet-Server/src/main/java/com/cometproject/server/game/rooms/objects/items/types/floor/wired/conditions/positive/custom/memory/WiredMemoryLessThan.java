package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.custom.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory.WiredMemoryBox;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;

import java.util.List;


public class WiredMemoryLessThan extends WiredConditionItem {
    public WiredMemoryLessThan(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 12;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        final double baseValue = WiredMemoryUtil.parseDoubleOrZero(this.getWiredData().getText());
        for (WiredMemoryBox box : WiredMemoryUtil.getMemoriesBoxFrom(this)) {
            final double value = WiredMemoryUtil.readMemoryFrom(box);
            if(this.isNegative != value < baseValue)
                return false;
        }

        return true;
    }
}
