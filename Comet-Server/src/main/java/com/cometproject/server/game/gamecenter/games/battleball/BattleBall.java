package com.cometproject.server.game.gamecenter.games.battleball;

import com.cometproject.server.game.gamecenter.games.battleball.room.BattleBallRoom;

import java.util.HashMap;

public class BattleBall {

    public static HashMap<Integer, BattleBallRoom> PLAYERS = new HashMap<>();

    /*
     *    Game Settings
     */

    public static final int GAME_LENGTH = 120; // Duration of a battle ball game in seconds.
    public static final int GAME_MAX_PLAYERS = 12; // Max players in a battle ball game.
    public static final int GAME_MAX_PLAYERS_PER_TEAMS = 4; // Max players in a battle ball team.
    public static final int GAME_TIME_TO_START = 5; // Time to start in seconds when there is enough players.
    public static final int GAME_MIN_PLAYERS = 1; // Min players to start a battle ball game.
    public static final int GAME_TURN_MILLIS = 150;
    public static final int GAME_TURNS = (GAME_LENGTH * 1000) / GAME_TURN_MILLIS;

}
