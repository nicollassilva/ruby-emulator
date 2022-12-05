package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredAddonNoItemsAnimateEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredCustomForwardRoom;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredCustomShowMessageRoom;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.utilities.RandomUtil;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class WiredActionExecuteStacks extends WiredActionItem {

    public WiredActionExecuteStacks(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final List<Position> tilesToExecute = Lists.newArrayList();
        final List<RoomItemFloor> actions = Lists.newArrayList();
        int nbEffectMsg = 0;

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null || floorItem instanceof WiredActionExecuteStacks)
                continue;

            if ((floorItem.getPosition().getX() == this.getPosition().getX() && floorItem.getPosition().getY() == this.getPosition().getY()))
                continue;

            tilesToExecute.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY()));
        }

        for (final Position tileToUpdate : tilesToExecute) {
            final List<RoomItemFloor> itemsOnTile = this.getRoom().getMapping().getTile(tileToUpdate).getItems();

            /*
            final boolean hasAddonRandomEffect = itemsOnTile.stream().anyMatch(item -> item instanceof WiredAddonRandomEffect);

            if (hasAddonRandomEffect) {
                final List<RoomItemFloor> randomEffect = itemsOnTile.stream().filter(item -> item instanceof WiredActionItem).toList();

                if (randomEffect.size() > 0) {
                    actions.add(randomEffect.get(RandomUtil.getRandomInt(0, randomEffect.size() - 1)));
                }
            }
            */

            final int max = 50;
            int limiter = 0;

            for (final RoomItemFloor roomItemFloor : itemsOnTile) {

                if (limiter >= max) {
                    break;
                }


                if (roomItemFloor instanceof WiredCustomForwardRoom || roomItemFloor instanceof WiredActionExecuteStacks)
                    continue;

               /* if (roomItemFloor instanceof WiredActionItem && hasAddonRandomEffect)
                    continue;
*/

                if (roomItemFloor instanceof WiredActionShowMessage || roomItemFloor instanceof WiredCustomShowMessageRoom) {
                    if (nbEffectMsg >= 10) {
                        continue;
                    }

                    nbEffectMsg++;
                } else {
                    limiter++;
                }

                actions.add(roomItemFloor);
            }
        }

        if (actions.size() > 0) {
            WiredTriggerItem.startExecute(event.entity, event.data, actions, true);
        }
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