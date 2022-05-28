package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;


public class WiredTriggerScoreAchieved extends WiredTriggerItem {
    private static final int PARAM_SCORE_TO_ACHIEVE = 0;

    public WiredTriggerScoreAchieved(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static boolean executeTriggers(int score, RoomEntity entity) {
        boolean wasExecuted = false;
        for (final WiredTriggerScoreAchieved floorItem : getTriggers(entity.getRoom(), WiredTriggerScoreAchieved.class)) {
            if(floorItem == null) continue;

            if (score >= floorItem.scoreToAchieve()) {
                wasExecuted |= floorItem.evaluate(entity, null);
            }
        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 10;
    }

    public int scoreToAchieve() {
        if (this.getWiredData().getParams().size() == 1) {
            return this.getWiredData().getParams().get(PARAM_SCORE_TO_ACHIEVE);
        }

        return 0;
    }
}
