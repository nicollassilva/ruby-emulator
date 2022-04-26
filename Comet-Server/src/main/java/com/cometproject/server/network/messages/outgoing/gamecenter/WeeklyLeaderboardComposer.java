package com.cometproject.server.network.messages.outgoing.gamecenter;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.players.data.GamePlayer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.storage.queries.gamecenter.GameDao;

import java.util.ArrayList;
import java.util.List;
public class WeeklyLeaderboardComposer extends MessageComposer {
    private List<GamePlayer> data;
    private final int gameId;

    private final int arg_2;
    private final int arg_3;
    private final int arg_4;
    private final int arg_5;
    private final int arg_6;

    public WeeklyLeaderboardComposer(int gameId, int arg_2, int arg_3, int arg_4, int arg_5, int arg_6) {
        this.gameId = gameId;
        this.data = new ArrayList<>();

        this.arg_2 = arg_2;
        this.arg_3 = arg_3;
        this.arg_4 = arg_4;
        this.arg_5 = arg_5;
        this.arg_6 = arg_6;

        this.loadGamePlayers();
    }

    public void loadGamePlayers() {
        GameDao.getLeaderBoard(gameId, true, this.data);
    }

    @Override
    public void compose(IComposer msg) {
        int i = 1;

        msg.writeInt(2022); // year
        msg.writeInt(arg_2); // week offset?
        msg.writeInt(arg_3); // week offset limit?
        msg.writeInt(arg_4); // 0 = this week, other = prev week
        msg.writeInt(23); // day
        msg.writeInt(data.size());

        for(final GamePlayer player : data) {
            msg.writeInt(player.getId());
            msg.writeInt(player.getPoints());
            msg.writeInt(i++);
            msg.writeString(player.getUsername());
            msg.writeString(player.getFigure());
            msg.writeString(player.getGender().toUpperCase());
        }

        msg.writeInt(arg_6); // position start or end....
        msg.writeInt(gameId);
    }

    @Override
    public short getId() {
        return Composers.Game2WeeklySmallLeaderboardMessageComposer;
    }
}
