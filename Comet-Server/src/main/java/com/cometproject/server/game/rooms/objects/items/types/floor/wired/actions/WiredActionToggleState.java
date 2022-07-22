package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.DiceFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.GenericLargeScoreFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.GenericSmallScoreFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.football.FootballTimerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.freeze.FreezeTimerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.google.common.collect.Lists;

import java.util.List;


public class WiredActionToggleState extends WiredActionItem {
    public WiredActionToggleState(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 0;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final List<Position> tilesToUpdate = Lists.newArrayList();

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null || floorItem instanceof WiredFloorItem || floorItem instanceof DiceFloorItem)
                continue;

            floorItem.onInteract(null, this.getCorrectRequestDataByFloorItem(floorItem), true);
            tilesToUpdate.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY()));
        }

        for (final Position tileToUpdate : tilesToUpdate) {
            this.getRoom().getMapping().updateTile(tileToUpdate.getX(), tileToUpdate.getY());
        }

        tilesToUpdate.clear();
    }

    public int getCorrectRequestDataByFloorItem(RoomItemFloor floorItem) {
        if(floorItem instanceof FootballTimerFloorItem || floorItem instanceof FreezeTimerFloorItem) {
            return 1;
        }

        if(floorItem instanceof GenericSmallScoreFloorItem || floorItem instanceof GenericLargeScoreFloorItem) {
            return 2;
        }

        return 0;
    }
}
