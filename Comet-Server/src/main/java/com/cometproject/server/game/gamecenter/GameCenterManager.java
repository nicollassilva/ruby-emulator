package com.cometproject.server.game.gamecenter;


import com.cometproject.api.utilities.Initialisable;
import com.cometproject.server.game.players.data.GamePlayer;
import com.cometproject.server.storage.queries.crafting.CraftingDao;
import com.cometproject.server.storage.queries.gamecenter.GameDao;
import com.cometproject.server.tasks.CometThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GameCenterManager implements Initialisable {
    private static GameCenterManager gameCenterManagerInstance;
    private static int gameId;
    private List<GamePlayer> currentWeek;
    private List<GamePlayer> lastWeek;
    private final Logger log = LogManager.getLogger(GameCenterManager.class.getName());

    private List<GameCenterInfo> gamesList;

    public GameCenterManager() {
    }

    @Override
    public void initialize() {
        this.gamesList = new ArrayList<>();

        this.loadLeaderboards();
        this.loadGameCenterList();

        log.info("GameCenter initialised");
    }

    private void loadLeaderboards() {
        if (this.currentWeek != null) {
            this.currentWeek.clear();
        }

        if (this.lastWeek != null) {
            this.lastWeek.clear();
        }

//        this.currentWeek = GameDao.getLeaderBoard();
//        this.lastWeek = GameDao.getLeaderBoard();

//        CometThreadManager.getInstance().executeSchedule(this::loadLeaderboards,1, TimeUnit.MINUTES);
    }

    public List<GamePlayer> getLeaderboardByWeek(boolean isCurrent){
        if(isCurrent) {
            return this.currentWeek;
        }

        return lastWeek;
    }

    public static GameCenterManager getInstance() {
        if (gameCenterManagerInstance == null)
            gameCenterManagerInstance = new GameCenterManager();

        return gameCenterManagerInstance;
    }

    public void loadGameCenterList() {
        if(!this.gamesList.isEmpty()) {
            this.gamesList.clear();
        }

        this.gamesList = CraftingDao.getGames();
    }

    public GameCenterInfo getGameById(int gameId){

        GameCenterInfo gameInfo = null;

        for(final GameCenterInfo infoGame : this.gamesList){
            if(infoGame.getGameId() == gameId){
                gameInfo = infoGame;
                break;
            }
        }
        return gameInfo;
    }

    public List<GameCenterInfo> getGamesList() {
        return this.gamesList;
    }
}
