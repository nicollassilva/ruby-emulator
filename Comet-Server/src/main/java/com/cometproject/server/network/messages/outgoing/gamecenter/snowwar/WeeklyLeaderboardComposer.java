package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.GamesLeaderboard;
import com.cometproject.server.game.snowwar.data.SnowWarPlayerData;
import com.cometproject.server.protocol.messages.MessageComposer;

public class WeeklyLeaderboardComposer extends MessageComposer {

    private final GamesLeaderboard leaderboard;

    public WeeklyLeaderboardComposer(GamesLeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(2013);
        msg.writeInt(0); // week offset?
        msg.writeInt(0); // week offset limit?
        msg.writeInt(0); // 0 = this week, other = prev week
        msg.writeInt(23); // day
        msg.writeInt(leaderboard.rankedList.size());
        for(final SnowWarPlayerData player : leaderboard.rankedList) {
            msg.writeInt(player.player.getId());
            msg.writeInt(player.getScore());
            msg.writeInt(player.getRank());
            msg.writeString(player.player.getData().getUsername());
            msg.writeString(player.player.getData().getFigure());
            msg.writeString(player.player.getData().getGender().toUpperCase());
        }
        msg.writeInt(0); // position start or end....
        msg.writeInt(leaderboard.gameId);
    }

    @Override
    public short getId() {
        return 2270;
    }
}
