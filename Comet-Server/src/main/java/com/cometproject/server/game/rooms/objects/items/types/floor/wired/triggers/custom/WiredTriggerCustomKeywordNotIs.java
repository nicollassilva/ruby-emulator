package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerPlayerSaysKeyword;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;

public class WiredTriggerCustomKeywordNotIs extends WiredTriggerItem {
    public static final int PARAM_OWNERONLY = 0;

    public WiredTriggerCustomKeywordNotIs(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static boolean executeTriggers(PlayerEntity playerEntity, String message) {
        boolean wasExecuted = false;

        for (final WiredTriggerCustomKeywordNotIs floorItem : getTriggers(playerEntity.getRoom(), WiredTriggerCustomKeywordNotIs.class)) {
            if(floorItem == null) continue;

            final boolean ownerOnly = floorItem.getWiredData().getParams().containsKey(PARAM_OWNERONLY) && floorItem.getWiredData().getParams().get(PARAM_OWNERONLY) != 0;
            final boolean isOwner = playerEntity.getPlayerId() == floorItem.getRoom().getData().getOwnerId();

            if (!ownerOnly || isOwner) {
                if (!floorItem.getWiredData().getText().isEmpty() && !message.equalsIgnoreCase(floorItem.getWiredData().getText())) {
                    wasExecuted = true;
                    floorItem.evaluate(playerEntity, message);
                } else if (!floorItem.getWiredData().getText().isEmpty() && message.equalsIgnoreCase(floorItem.getWiredData().getText())) {
                    wasExecuted = false;
                }
            }
        }

        if (wasExecuted) {
            playerEntity.getPlayer().getSession().send(new TalkMessageComposer(playerEntity.getId(), message, ChatEmotion.NONE, 34));
        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 0;
    }
}

