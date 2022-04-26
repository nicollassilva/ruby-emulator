package com.cometproject.server.network.messages.outgoing.gamecenter;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.players.data.GamePlayer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.storage.queries.gamecenter.GameDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class LastWeekLeaderboardComposer extends MessageComposer {
    private List<GamePlayer> data = null;
    private final int gameId;

    public LastWeekLeaderboardComposer(int gameId) {
        this.gameId = gameId;
        this.data = new ArrayList<>();

        this.loadGamePlayers();
    }

    public void loadGamePlayers() {
        GameDao.getLeaderBoard(gameId, false, this.data);
    }

    @Override
    public void compose(IComposer msg) {
        int i = 1;

        msg.writeInt(2022); // year
        msg.writeInt(1); // week offset?
        msg.writeInt(0); // week offset limit?
        msg.writeInt(1); // 0 = this week, other = prev week
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

        msg.writeInt(data.size()); // position start or end....
        msg.writeInt(gameId);
    }

    @Override
    public short getId() {
        return Composers.Game2WeeklyLeaderboardMessageComposer;
    }
}
