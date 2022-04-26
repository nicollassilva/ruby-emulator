package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;


public class WiredConditionCustomHasTag extends WiredConditionItem {

    public WiredConditionCustomHasTag(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 11;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (entity == null) return false;

        if (this.getWiredData().getText().length() != 1) {
            return false;
        }

        final String tag = this.getWiredData().getText();
        boolean hasTag = false;

        PlayerEntity playerEntity = (PlayerEntity) entity;
        RoomEntity roomEntity = entity;

        if (roomEntity.getTagUser().equals(tag)) {
            hasTag = true;
        }

        return isNegative != hasTag;
    }
}
