package com.cometproject.server.game.snowwar;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameManager {
    private static final Logger log = LogManager.getLogger(GameManager.class.getName());
    private static HashMap<Integer, Game> games;

    private static int gameIdCounter = 1;

    public static void makeSet(){
        games = new HashMap<>();
    }

    public static Game getGameById(int gameId) {
        return games.get(gameId);
    }

    public static Game getGameByCode(String code) {
        for (Game game : games.values()) {
            if (game.getGamecenterCode().equals(code)) {
                return game;
            }
        }
        return null;
    }



    public static Game registerGame(Class<? extends Game> gameClass) {
        try {
            int gameId = gameIdCounter;
            Game game = gameClass.getDeclaredConstructor(int.class).newInstance(gameIdCounter);
            games.put(gameId, game);
            gameIdCounter++;
            log.info("[GameCenter] Game '" + gameClass.getSimpleName() + "' registered");
            return game;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            log.info((Supplier<String>) ex);
        }
        return null;
    }

    public static int getIdFromGame(Game game) {
        for(final Integer gameId : games.keySet()) {
            if(games.get(gameId) == game) {
                return gameId;
            }
        }

        return -1;
    }


    public static List<Game> getGames() {
        return new ArrayList<>(games.values());
    }
}
