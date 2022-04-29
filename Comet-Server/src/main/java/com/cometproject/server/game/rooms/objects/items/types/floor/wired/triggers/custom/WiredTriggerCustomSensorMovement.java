package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.incoming.room.action.TalkMessageEvent;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;


public class WiredTriggerCustomSensorMovement extends WiredTriggerItem {

    public WiredTriggerCustomSensorMovement(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static boolean executeTriggers(PlayerEntity playerEntity) {
        boolean wasExecuted = false;

        for (final WiredTriggerCustomSensorMovement floorItem : getTriggers(playerEntity.getRoom(), WiredTriggerCustomSensorMovement.class)) {
            if(floorItem == null) continue;

            wasExecuted = floorItem.evaluate(playerEntity, null);
        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 9;
    }
}