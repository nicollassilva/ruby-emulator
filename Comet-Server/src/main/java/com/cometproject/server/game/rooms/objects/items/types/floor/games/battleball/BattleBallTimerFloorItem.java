package com.cometproject.server.game.rooms.objects.items.types.floor.games.battleball;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.AbstractGameTimerFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameType;

public class BattleBallTimerFloorItem extends AbstractGameTimerFloorItem {
    public BattleBallTimerFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public GameType getGameType() {
        return GameType.BATTLEBALL;
    }
}
