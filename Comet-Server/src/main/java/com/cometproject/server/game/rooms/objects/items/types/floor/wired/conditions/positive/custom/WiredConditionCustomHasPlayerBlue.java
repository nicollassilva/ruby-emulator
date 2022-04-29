package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;

public class WiredConditionCustomHasPlayerBlue extends WiredConditionItem {

    public WiredConditionCustomHasPlayerBlue(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 8;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        int itemsWithPlayers = 0;

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem != null) {
                if (floorItem.getEntitiesBlueOnItem().size() != 0) {
                    itemsWithPlayers++;
                }
            }
        }

        if (isNegative) {
            return itemsWithPlayers == 0;
        } else {
            return itemsWithPlayers == this.getWiredData().getSelectedIds().size();
        }
    }
}
