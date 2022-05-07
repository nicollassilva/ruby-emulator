package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredCustomForwardRoom;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.utilities.RandomUtil;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WiredActionExecuteStacks extends WiredActionItem {

    public WiredActionExecuteStacks(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final List<Position> tilesToExecute = Lists.newArrayList();
        final List<RoomItemFloor> actions = Lists.newArrayList();

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

            if(hasAddonRandomEffect) {
                final List<RoomItemFloor> randomEffect = itemsOnTile.stream().filter(item -> item instanceof WiredActionItem).collect(Collectors.toList());

                if(randomEffect.size() > 0) {
                    actions.add(randomEffect.get(RandomUtil.getRandomInt(0, randomEffect.size() - 1)));
                }
            }

            for (final RoomItemFloor roomItemFloor : itemsOnTile) {
                if (actions.size() > 1000)
                    break;

                if (!(roomItemFloor instanceof WiredCustomForwardRoom)) {
                    if(roomItemFloor instanceof WiredActionItem && !hasAddonRandomEffect) {
                        actions.add(roomItemFloor);
                    }
                }
            }
        }

        if (actions.size() > 0) {
            WiredTriggerItem.startExecute(event.entity, event.data, actions, false);
        }

        tilesToExecute.clear();
        actions.clear();
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
