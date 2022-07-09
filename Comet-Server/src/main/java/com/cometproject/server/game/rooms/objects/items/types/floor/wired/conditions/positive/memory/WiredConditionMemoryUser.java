package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.wired.dialog.WiredConditionMessageComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

public abstract class WiredConditionMemoryUser extends WiredConditionMemory {
    public WiredConditionMemoryUser(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public int getInterface() {
        return 11;
    }

    @Override
    public MessageComposer getDialog() {
        return new WiredConditionMessageComposer(this);
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (!(entity instanceof PlayerEntity)) return false;

        final double wiredValue = WiredMemoryUtil.parseDoubleOrZero(this.getWiredData().getText());
        final double value = WiredMemoryUtil.readMemoryFrom((PlayerEntity)entity);
        return isNegative != this.canOp(wiredValue, value);
    }
}
