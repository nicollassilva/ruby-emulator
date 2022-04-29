package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.utilities.RandomUtil;
import org.apache.commons.lang.StringUtils;

public class WiredCustomRotationPlayer extends WiredActionItem {

    public WiredCustomRotationPlayer(RoomItemData itemData, Room room) {
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

        if(!StringUtils.isNumeric(this.getWiredData().getText())) return;

        final String finalText = this.getWiredData().getText();

        int i = Integer.parseInt(finalText);

        if(i < 0 || i > 7) return;

        playerEntity.setBodyRotation(i);
        playerEntity.setHeadRotation(i);
        playerEntity.markNeedsUpdate();

        if(finalText.equals("-1")) {
            playerEntity.setBodyRotation(RandomUtil.getRandomInt(0, 7));
            playerEntity.markNeedsUpdate();
        }
    }
}
