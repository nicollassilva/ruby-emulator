package com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.data.ScoreboardItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerScoreAchieved;
import com.cometproject.server.game.rooms.types.Room;

import java.util.List;

public class HighscoreClassicFloorItem extends HighscoreFloorItem {
    public HighscoreClassicFloorItem(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public void onTeamWins(List<String> usernames, List<PlayerEntity> players, int score) {
        final ScoreboardItemData.HighscoreEntry entry = this.getScoreData().getEntryByTeam(usernames);

        if (entry != null) {
            entry.incrementScore(score);
            this.updateEntry(entry);
        } else {
            this.addEntry(usernames, score);
        }

        final int totalScore = this.getScoreData().getEntryByTeam(usernames).getScore();
        for(final PlayerEntity player : players) {
            WiredTriggerScoreAchieved.executeTriggers(totalScore, player);
        }
    }

    @Override
    public int getScoreType() {
        return 2;
    }
}
