package com.cometproject.server.game;

import com.cometproject.server.game.snowwar.data.SnowWarPlayerData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamesLeaderboard {
    public static final Map<Integer, GamesLeaderboard> leaderboards = new ConcurrentHashMap<>();

    public final int gameId;

    public List<SnowWarPlayerData> rankedList;

    public GamesLeaderboard(int id) {
        gameId = id;
    }
}
