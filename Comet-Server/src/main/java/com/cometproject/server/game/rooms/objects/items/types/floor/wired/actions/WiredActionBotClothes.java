package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;

public class WiredActionBotClothes extends WiredActionItem {

    public WiredActionBotClothes(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 26;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!this.getWiredData().getText().contains("\t")) {
            return;
        }

        if (this.getWiredData().getText().isEmpty()) {
            return;
        }

        final String[] data = this.getWiredData().getText().split("\t");

        if (data.length != 2) {
            return;
        }

        final String botName = data[0];
        String figure = data[1];

        final BotEntity botEntity = this.getRoom().getBots().getBotByName(botName);

        if (botEntity != null) {
            botEntity.getData().setFigure(figure);
            this.getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(botEntity));

            botEntity.getData().save();
        }
    }
}
