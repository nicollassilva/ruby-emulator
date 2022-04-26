package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;

import java.util.ArrayList;
import java.util.List;

public class WiredActionGiveScoreTeam extends WiredActionItem {
    private final static int PARAM_SCORE = 0;
    private final static int PARAM_PER_GAME = 1;
    private final static int PARAM_TEAM_ID = 2;

    public WiredActionGiveScoreTeam(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getWiredData().getParams().size() < 3) {
            this.getWiredData().getParams().clear();

            this.getWiredData().getParams().put(PARAM_SCORE, 1);
            this.getWiredData().getParams().put(PARAM_PER_GAME, 1);
            this.getWiredData().getParams().put(PARAM_TEAM_ID, 1);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 14;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final GameTeam gameTeam = this.getTeam();

        if (gameTeam == GameTeam.NONE)
            return;

        ArrayList<String> teamUsernames = new ArrayList<>();
        this.getRoom().getGame().getTeams().get(gameTeam).forEach( member -> {
            PlayerEntity player = this.getRoom().getEntities().getEntityByPlayerId(member);
            if(player != null)  {
                teamUsernames.add(player.getUsername());
            }
        });

        /*
        This is not how it should work, but it's how people want it
         */
        if(teamUsernames.size() < 1) return;
        
        final List<HighscoreFloorItem> scoreboards = getRoom().getItems().getByClass(HighscoreFloorItem.class);

        for (HighscoreFloorItem scoreboard : scoreboards) {
            scoreboard.onTeamWins(teamUsernames, this.getScore());
        }

        /*
        This is how it should work

        this.getRoom().getGame().increaseScore(gameTeam, this.getScore());
         */

    }

    private GameTeam getTeam() {
        switch (this.getWiredData().getParams().get(PARAM_TEAM_ID)) {

            case 1:
                return GameTeam.RED;
            case 2:
                return GameTeam.GREEN;
            case 3:
                return GameTeam.BLUE;
            case 4:
                return GameTeam.YELLOW;
        }

        return GameTeam.NONE;
    }

    public int getScore() {
        return this.getWiredData().getParams().get(PARAM_SCORE);
    }

}
