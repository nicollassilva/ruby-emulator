package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;

public class WiredActionBotTalkToAvatar extends WiredActionItem {
    public static final int PARAM_MESSAGE_TYPE = 0;

    public WiredActionBotTalkToAvatar(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getWiredData().getParams().size() != 1) {
            this.getWiredData().getParams().put(PARAM_MESSAGE_TYPE, 1);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 27;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!this.getWiredData().getText().contains("\t")) {
            return;
        }

        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        final String[] talkData = this.getWiredData().getText().split("\t");

        if (talkData.length != 2) {
            return;
        }

        final String botName = talkData[0];
        String message = talkData[1];

        if (botName.isEmpty() || message.isEmpty()) {
            return;
        }


        message = message.replace("%username%", event.entity.getUsername())
                  .replace("%points%", String.valueOf(((PlayerEntity) event.entity).getPoints()));

        message = message.replace("<", "").replace(">", "");

        final BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);

        if (botEntity != null) {
            boolean isWhisper = (this.getWiredData().getParams().size() == 1 && (this.getWiredData().getParams().get(PARAM_MESSAGE_TYPE) == 1));
            if (((PlayerEntity) event.entity).getPlayer() != null && ((PlayerEntity) event.entity).getPlayer().getSession() != null) {
                if (isWhisper) {
                    ((PlayerEntity) event.entity).getPlayer().getSession().send(new WhisperMessageComposer(botEntity.getId(), message, 2));
                } else {
                    ((PlayerEntity) event.entity).getPlayer().getSession().send(new TalkMessageComposer(botEntity.getId(), message, ChatEmotion.NONE, 2));
                }
            }
        }
    }
}