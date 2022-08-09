package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.wired.dialog.WiredConditionMessageComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

public class WiredConditionMemoryUserEquals extends WiredConditionMemoryUser{
    public WiredConditionMemoryUserEquals(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public boolean canOp(double wiredValue, double value) {
        return wiredValue == value;
    }
}
