package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;

public class WiredCustomMuteTriggerer extends WiredActionItem {
    public static final int PARAM_MUTE_TIME = 0;

    public WiredCustomMuteTriggerer(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getWiredData().getParams().size() < 1) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(PARAM_MUTE_TIME, 0);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 20;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        final int time = this.getWiredData().getParams().get(PARAM_MUTE_TIME);
        final String message = this.getWiredData().getText();

        if (time > 0) {
            ((PlayerEntity)event.entity).getPlayer().getSession().send(new WhisperMessageComposer(((PlayerEntity)event.entity).getPlayerId(), "Wired Mute: Silenciado por " + time + (time > 1 ? "minutos" : "minuto") + "! Mensagem: " + (message == null || message.isEmpty() ? "Nenhuma mensagem" : message)));

            if (this.getRoom().getRights().hasMute(((PlayerEntity)event.entity).getPlayerId())) {
                this.getRoom().getRights().updateMute(((PlayerEntity)event.entity).getPlayerId(), time);
            } else {
                this.getRoom().getRights().addMute(((PlayerEntity)event.entity).getPlayerId(), time);
            }
        }
    }
}
