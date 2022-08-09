package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory.WiredConditionMemoryEquals;
import com.cometproject.server.game.rooms.types.Room;

public class WiredNegativeConditionMemoryEquals extends WiredConditionMemoryEquals {
    public WiredNegativeConditionMemoryEquals(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }
}
