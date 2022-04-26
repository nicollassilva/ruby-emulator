package com.cometproject.server.game.snowwar;

import com.cometproject.server.game.players.types.Player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Game {
    public final Logger logger = LogManager.getLogger(Game.class.getName());
    private final int gameId;

    public Game(int gameId){this.gameId = gameId;}

    public int getGameId() {
        return gameId;
    }

    /*
     * Returns the short game name, such as basejump, snowwar, slotcar
     */
    public String getGamecenterCode() {
        return "";
    }

    /*
     * Returns the hex code for gamecenter page background colour
     */
    public String getGamecenterBackgroundColour() {
        return "";
    }

    /*
     * Returns the hex code for gamecenter text colour
     */
    public String getGamecenterTextColour() {
        return "";
    }

    /*
     * Returns the URL for where the asset images are stored (_icon.pg, _theme.png, _logo.png)
     */
    public String getGamecenterAssetsPath() {
        return "";
    }

    /*
     * Returns if the game is active or not. Can be used to check if the game servers are online or not.
     */
    public Boolean getGameStatus() {
        return false;
    }

    /*
     * Returns the number to display next to play button.
     * Could be used as a "Games Left Count" or "Notifications Count".
     */
    public int getGamesLeftCount(Player player) {
        return 0;
    }

    /*
     * This will be called to every time someone clicks the play button.
     */
    public void onPlayButton(int gameId, Player player) {
    }


}
