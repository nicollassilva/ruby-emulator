package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

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
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;


public class WiredCustomToggleStateNegative extends WiredActionItem {
    public WiredCustomToggleStateNegative(RoomItemData itemData, Room room) {
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
        final List<Position> tilesToUpdate = new ArrayList<>(this.getWiredData().getSelectedIds().size());

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null || floorItem instanceof WiredFloorItem || floorItem instanceof DiceFloorItem) {
                continue;
            }

            final int interactionCount = this.getCorrectInteractionCount(floorItem);

            try {
                final int current = Integer.parseInt(floorItem.getItemData().getData());

                if(current >= 1) {
                    floorItem.getItemData().setData("" + (current - 1));
                } else {
                    floorItem.getItemData().setData("" + interactionCount);
                }

                floorItem.sendUpdate();

                if(!tilesToUpdate.contains(floorItem.getPosition())) {
                    tilesToUpdate.add(floorItem.getPosition());
                }
            }
            catch (NumberFormatException e){
                floorItem.getItemData().setData("");
                floorItem.sendUpdate();
                this.getWiredData().getSelectedIds().remove(itemId);
                this.saveData();
            }
        }

        for (final Position tileToUpdate : tilesToUpdate) {
            final RoomTile tile = this.getRoom().getMapping().getTile(tileToUpdate);

            if(tile == null)
                continue;

            tile.reload();
        }
    }

    public int getCorrectInteractionCount(RoomItemFloor floorItem) {
        if(floorItem instanceof GenericSmallScoreFloorItem) {
            return ((GenericSmallScoreFloorItem) floorItem).SCORE_LIMIT;
        }

        if(floorItem instanceof GenericLargeScoreFloorItem) {
            return ((GenericLargeScoreFloorItem) floorItem).SCORE_LIMIT;
        }

        return floorItem.getDefinition().getInteractionCycleCount();
    }
}