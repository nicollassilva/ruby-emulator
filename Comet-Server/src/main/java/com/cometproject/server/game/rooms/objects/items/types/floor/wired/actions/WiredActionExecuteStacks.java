package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
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
    public final boolean evaluate(RoomEntity entity, Object data) {
        if (this.hasTicks()) return false;

        final WiredItemEvent itemEvent = new WiredItemEvent(entity, data);

        if (this.getWiredData().getDelay() >= 1 && this.usesDelay()) {
            itemEvent.setTotalTicks(RoomItemFactory.getProcessTime(this.getWiredData().getDelay() / 2));

            this.queueEvent(itemEvent);
        } else {
            itemEvent.onCompletion(this);
            this.onEventComplete(itemEvent);
        }

        return true;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final List<Position> tilesToExecute = Lists.newArrayList();
        final List<RoomItemFloor> actions = Lists.newArrayList();
        int nbEffectMsg = 0;

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null || (floorItem.getPosition().getX() == this.getPosition().getX() && floorItem.getPosition().getY() == this.getPosition().getY()))
                continue;

            for (final Position positions : floorItem.getPositions()) {
                if (!tilesToExecute.contains(positions))
                    tilesToExecute.add(positions);
            }
        }

        for (final Position tileToUpdate : tilesToExecute) {
            final List<RoomItemFloor> itemsOnTile = this.getRoom().getMapping().getTile(tileToUpdate).getItems();
            final boolean hasAddonRandomEffect = itemsOnTile.stream().anyMatch(item -> item instanceof WiredAddonRandomEffect);

            if (hasAddonRandomEffect) {
                final List<RoomItemFloor> randomEffect = itemsOnTile.stream().filter(item -> item instanceof WiredActionItem).collect(Collectors.toList());

                if (randomEffect.size() > 0) {
                    actions.add(randomEffect.get(RandomUtil.getRandomInt(0, randomEffect.size() - 1)));
                }
            }

            for (final RoomItemFloor roomItemFloor : itemsOnTile) {
                if (actions.size() > 1000)
                    break;

                if (roomItemFloor instanceof WiredActionItem && hasAddonRandomEffect)
                    continue;

                if (roomItemFloor instanceof WiredActionItem && !(roomItemFloor instanceof WiredActionExecuteStacks)) {
                    actions.add(roomItemFloor);
                }
            }

            if (actions.size() > 0) {
                WiredTriggerItem.startExecute(event.entity, event.data, actions, true);
                actions.clear();
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