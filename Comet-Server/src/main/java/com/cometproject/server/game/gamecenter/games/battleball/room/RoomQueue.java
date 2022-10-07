package com.cometproject.server.game.gamecenter.games.battleball.room;

import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomQueue {

    public BattleBallRoom room;
    public final Map<Integer, Session> players = new ConcurrentHashMap<>(BattleBall.GAME_MAX_PLAYERS);

    public RoomQueue(BattleBallRoom battleBallRoom) {
        room = battleBallRoom;
    }

    public void broadcast(JSONObject message) {
        for(Session client : players.values()) {
            // TODO: Broadcast message.
        }
    }

}
