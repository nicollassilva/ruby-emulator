package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.types.PlayerAvatarActions;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;

public class WiredCustomActionsPlayer extends WiredActionItem {

    public WiredCustomActionsPlayer(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        final PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        if (this.getWiredData() == null || this.getWiredData().getText() == null) {
            return;
        }

        final String finalText = this.getWiredData().getText();

        switch (finalText) {
            case "o/":
                playerEntity.getRoom().getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), PlayerAvatarActions.EXPRESSION_WAVE.getValue())); // wave o/
                break;

            case "kiss":
                playerEntity.getRoom().getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), PlayerAvatarActions.EXPRESSION_BLOW_A_KISS.getValue())); // :kiss
                break;

            case "_b":
                playerEntity.getRoom().getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), PlayerAvatarActions.EXPRESSION_RESPECT.getValue())); // _b
                break;

            case ":D":
                playerEntity.getRoom().getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), PlayerAvatarActions.EXPRESSION_LAUGH.getValue())); // idle
                break;

            case ":(":
                playerEntity.getRoom().getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), PlayerAvatarActions.EXPRESSION_CRY.getValue())); // idle
                break;

            case ":o":
                playerEntity.getPlayer().getSession().send(new TalkMessageComposer(-1, null, ChatEmotion.SHOCKED, 0));
        }
    }
}