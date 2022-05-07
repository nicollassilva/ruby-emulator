package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionExecuteStacks;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionShowMessage;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.google.common.collect.Lists;

import java.util.List;

public class WiredCustomExecuteStacksConditions extends WiredActionItem {

    public WiredCustomExecuteStacksConditions(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final List<Position> tilesToExecute = Lists.newArrayList();
        final List<RoomItemFloor> itemFloors = Lists.newArrayList();
        int nbEffect = 0;
        int nbEffectMsg = 0;

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null)
                continue;

            for (final Position positions : floorItem.getPositions()) {
                if (!tilesToExecute.contains(positions))
                    tilesToExecute.add(positions);
            }
        }

        for (final Position tileToUpdate : tilesToExecute) {
            final List<RoomItemFloor> itemsOnTile = this.getRoom().getMapping().getTile(tileToUpdate).getItems();
            final boolean hasAddonRandomEffect = itemsOnTile.stream().anyMatch(item -> item instanceof WiredAddonRandomEffect);
            boolean randomEffectAdded = false;

            for (final RoomItemFloor roomItemFloor : itemsOnTile) {
                if (nbEffect > 1000)
                    break;

                if (roomItemFloor instanceof WiredActionItem || roomItemFloor instanceof WiredConditionItem || roomItemFloor instanceof WiredAddonUnseenEffect || roomItemFloor instanceof WiredAddonRandomEffect || roomItemFloor instanceof WiredAddonNoItemsAnimateEffect) {
                    if (roomItemFloor instanceof WiredActionShowMessage || roomItemFloor instanceof WiredCustomShowMessageRoom) {
                        if (nbEffectMsg >= 10) {
                            continue;
                        }

                        nbEffectMsg++;
                    }

                    if (roomItemFloor instanceof WiredCustomForwardRoom) continue;

                    if (hasAddonRandomEffect) {
                        if (roomItemFloor instanceof WiredActionItem) {
                            if (randomEffectAdded) continue;

                            randomEffectAdded = true;
                        }
                    }

                    itemFloors.add(roomItemFloor);
                    nbEffect++;
                }
            }

            if (itemFloors.size() > 0) {
                WiredTriggerItem.startExecute(event.entity, event.data, itemFloors, true);
                itemFloors.clear();
            }
        }

        tilesToExecute.clear();
    }


    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 18;
    }
}
