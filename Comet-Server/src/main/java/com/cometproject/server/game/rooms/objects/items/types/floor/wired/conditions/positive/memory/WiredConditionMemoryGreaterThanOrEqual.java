package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.types.Room;

public class WiredConditionMemoryGreaterThanOrEqual extends WiredConditionMemory{
    public WiredConditionMemoryGreaterThanOrEqual(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public boolean canOp(double wiredValue, double value) {
        return value >= wiredValue;
    }
}
