package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2GameResult;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2SnowWarGameStats;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2TeamScoreData;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GameEndingComposer extends MessageComposer {

    private final SnowWarRoom arena;

    public GameEndingComposer(SnowWarRoom arena) {
        this.arena = arena;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
        SerializeGame2GameResult.parse(msg, arena);
        msg.writeInt(SnowWar.TEAMS.length);

        for (final int teamId : SnowWar.TEAMS) {
            SerializeGame2TeamScoreData.parse(msg, teamId, arena.TeamScore[teamId-1], arena.TeamPlayers.get(teamId).values());
        }

        SerializeGame2SnowWarGameStats.parse(msg, arena);
    }

    @Override
    public short getId() {
        return 5022;
    }
}
