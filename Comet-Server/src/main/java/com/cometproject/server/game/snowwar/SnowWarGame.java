package com.cometproject.server.game.snowwar;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.players.types.Player;

public class SnowWarGame extends Game{
    public SnowWarGame(int gameId) { super(gameId);
    }

    @Override
    public void onPlayButton(int gameId, Player player) {
        SnowPlayerQueue.addPlayerInQueue(player.getSession());
    }

    @Override
    public String getGamecenterCode() {
        return "snowwar";
    }

    @Override
    public String getGamecenterBackgroundColour() {
        return Locale.getOrDefault("gamecenter.snowwar.game.background.color", "93d4f3");
    }

    @Override
    public String getGamecenterTextColour() {
        return Locale.getOrDefault("gamecenter.snowwar.game.text.color", "000000");
    }

    @Override
    public String getGamecenterAssetsPath() {
        return Locale.getOrDefault("gamecenter.snowwar.assets", "http://localhost/swfz/c_images/gamecenter/gamecenter_snowwar/");
    }

    @Override
    public Boolean getGameStatus() {
        return true;
    }

    @Override
    public int getGamesLeftCount(Player player) {
        return 3;
    }
}
