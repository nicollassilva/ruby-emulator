package com.cometproject.server.game.snowwar.tasks;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.GameEndingComposer;

public class SnowArenaEnd {
    public static void exec(SnowWarRoom room) {
        room.Winner = 0;
        int blueScore = 0;
        int redScore = 0;

        for (final int TeamId : SnowWar.TEAMS) {
            if (TeamId == SnowWar.TEAM_BLUE) {
                blueScore += room.TeamScore[TeamId - 1];
            } else {
                redScore += room.TeamScore[TeamId - 1];
            }
        }

        if (blueScore > redScore) {
            room.Winner = 1;
            room.Result = 1;
        } else if (redScore > blueScore){
            room.Winner = 2;
            room.Result = 1;
        } else {
            room.Result = 2;
        }

        for (final HumanGameObject player : room.players.values()) {
            if (room.MostHits == null) {
                room.MostHits = player;
            }
            if (room.MostKills == null) {
                room.MostKills = player;
            }
            if (player.hits > room.MostHits.hits) {
                room.MostHits = player;
            }
            if (player.kills > room.MostKills.kills) {
                room.MostKills = player;
            }

            if (player.team == room.Winner) {
                // Snowstorm Winner
                player.cn.getPlayer().getAchievements().progressAchievement(AchievementType.SNOW_USER_WINNER, 1);
            } else {
                //player.cn.getPlayer().increaseXP(25);
            }

            player.cn.getPlayer().getAchievements().progressAchievement(AchievementType.SNOW_TOTAL_SCORE, player.team == SnowWar.TEAM_BLUE ? blueScore : redScore);
        }

        room.broadcast(new GameEndingComposer(room));
    }
}
