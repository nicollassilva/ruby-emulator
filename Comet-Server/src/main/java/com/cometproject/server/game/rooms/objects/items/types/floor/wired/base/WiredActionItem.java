package com.cometproject.server.game.rooms.objects.items.types.floor.wired.base;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredAddonNoItemsAnimateEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredAddonNoPlayersAnimateEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.data.WiredActionItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.wired.dialog.WiredActionMessageComposer;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.google.common.collect.Lists;

import java.util.List;


public abstract class WiredActionItem extends WiredFloorItem {
    private boolean usesItemsAnimations = true;
    private boolean usesPlayerAnimations = true;

    public WiredActionItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public MessageComposer getDialog() {
        return new WiredActionMessageComposer(this);
    }

    @Override
    public final boolean evaluate(RoomEntity entity, Object data) {
        if (!CometSettings.WIRED_WORKING_ACTIVE) return false;

        final WiredItemEvent itemEvent = new WiredItemEvent(entity, data);

        if (this.getWiredData().getDelay() >= 1) {
            itemEvent.setTotalTicks(RoomItemFactory.getProcessTime((double) this.getWiredData().getDelay() / 2));
            this.setTicks(RoomItemFactory.getProcessTime((double) this.getWiredData().getDelay() / 2));

            this.queueEvent(itemEvent);
        } else {
            itemEvent.onCompletion(this);
            this.onEventComplete(itemEvent);
        }

        return true;
    }

    public void setUseItemsAnimation(boolean usesItemsAnimations) {
        this.usesItemsAnimations = usesItemsAnimations;
    }

    public void setUsesPlayersAnimations(boolean usesPlayerAnimations) {
        this.usesPlayerAnimations = usesPlayerAnimations;
    }

    public boolean usePlayersAnimation() {
        for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
            if (floorItem instanceof WiredAddonNoPlayersAnimateEffect) {
                return true;
            }
        }

        return false;
    }

    public boolean useItemsAnimation() {
        for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
            if (floorItem instanceof WiredAddonNoItemsAnimateEffect) {
                return true;
            }
        }

        return false;
    }

    @Override
    public WiredActionItemData getWiredData() {
        return (WiredActionItemData) super.getWiredData();
    }

    public List<WiredTriggerItem> getIncompatibleTriggers() {
        final List<WiredTriggerItem> incompatibleTriggers = Lists.newArrayList();

        if (this.requiresPlayer()) {
            for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
                if (!(floorItem instanceof WiredTriggerItem)) continue;

                if (!((WiredTriggerItem) floorItem).suppliesPlayer()) {
                    incompatibleTriggers.add(((WiredTriggerItem) floorItem));
                }
            }
        }

        return incompatibleTriggers;
    }

    public abstract boolean requiresPlayer();
}
