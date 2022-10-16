package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;

public class WiredConditionCustomHasRights extends WiredConditionItem {

    public WiredConditionCustomHasRights(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 10;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (entity == null) return false;

        boolean hasRights = false;
        final PlayerEntity playerEntity = (PlayerEntity) entity;

        if (playerEntity.getRoom().getRights().hasRights(playerEntity.getPlayer().getId()) || playerEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
            hasRights = true;
        }

        return isNegative != hasRights;
    }
}
