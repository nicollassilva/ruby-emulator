package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WiredCustomSetPoints extends WiredActionItem {

    public WiredCustomSetPoints(RoomItemData itemData, Room room) {
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

        PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        if (this.getWiredData() == null || this.getWiredData().getText() == null) {
            return;
        }

        if (!StringUtils.isNumeric(this.getWiredData().getText()) || this.getWiredData().getText().isEmpty()) {
            return;
        }

        String finalText = this.getWiredData().getText();

        final List<HighscoreFloorItem> scoreboards = getRoom().getItems().getByClass(HighscoreFloorItem.class);

        ArrayList<String> score = new ArrayList<>();
        score.add(playerEntity.getUsername());
        for (HighscoreFloorItem scoreboard : scoreboards) {
            scoreboard.onTeamWins(score, Integer.parseInt(finalText));
        }
    }
}
