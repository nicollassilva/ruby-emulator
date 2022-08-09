package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;

public abstract class WiredMemoryUserOperation extends WiredMemoryOperation {
    public WiredMemoryUserOperation(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final PlayerEntity entity = ((PlayerEntity)event.entity);
        final double baseValue = this.getInputValueOrDefault();
        final double value = WiredMemoryUtil.readMemoryFrom(entity);

        WiredMemoryUtil.setMemoryInto(entity, this.doOp(baseValue, value));
    }
}
