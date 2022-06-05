package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class WiredCustomRemoveScore extends WiredActionItem {
    private final static int PARAM_SCORE = 0;
    private final static int PARAM_PER_GAME = 1;

    public WiredCustomRemoveScore(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        if (this.getWiredData().getParams().size() < 2) {
            this.getWiredData().getParams().clear();

            this.getWiredData().getParams().put(PARAM_SCORE, 1);
            this.getWiredData().getParams().put(PARAM_PER_GAME, 1);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 6;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        /*
        This is not how this wired should work, but it's how people like it
         */
        final List<HighscoreFloorItem> scoreboards = getRoom().getItems().getByClass(HighscoreFloorItem.class);

        final ArrayList<PlayerEntity> players = Lists.newArrayList(playerEntity);
        final ArrayList<String> usernames =Lists.newArrayList(playerEntity.getUsername());
        for (HighscoreFloorItem scoreboard : scoreboards) {
            scoreboard.onTeamWins(usernames, players, this.getScore());
        }

        /*
        This is how it should work below

        if (playerEntity.getGameTeam() == null) {
            return;
        }

        this.getRoom().getGame().increaseScore(playerEntity.getGameTeam(), this.getScore());
         */
    }

    public int getScore() {
        return this.getWiredData().getParams().get(PARAM_SCORE);
    }
}
