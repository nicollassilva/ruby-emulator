package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;

import java.util.regex.Pattern;

public class WiredMemoryBox extends WiredActionItem {/*
    private static final Pattern variablePattern = Pattern.compile("\\$\\{box-value}");
    private static final Pattern userVariablePattern = Pattern.compile("\\$\\{user-value}");*/
    public WiredMemoryBox(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if(!(event.entity instanceof PlayerEntity)){
            return;
        }
/*
        final PlayerEntity playerEntity = (PlayerEntity) event.entity;
        final double value = WiredMemoryUtil.readMemoryFrom(this);
        final double userValue = WiredMemoryUtil.readMemoryFrom(playerEntity);
        final String rawValue = this.getWiredData().getText();
        final String boxValueReplaced = userVariablePattern.matcher(rawValue).replaceAll(WiredMemoryUtil.doubleToString(userValue));
        final String userValueReplaced = variablePattern.matcher(boxValueReplaced).replaceAll(WiredMemoryUtil.doubleToString(value));

        playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getPlayerId(), userValueReplaced));*/
    }
}
