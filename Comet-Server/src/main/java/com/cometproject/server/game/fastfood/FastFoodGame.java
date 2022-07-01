package com.cometproject.server.game.fastfood;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.fastfood.messages.outgoing.GameCenterGameFrameURLComposer;
import com.cometproject.server.game.gamecenter.GameCenterInfo;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.snowwar.Game;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class FastFoodGame extends Game {

    public FastFoodGame(int gameId) {
        super(gameId);
    }

    @Override
    public String getGamecenterCode() {
        return "basejump";
    }

    @Override
    public String getGamecenterBackgroundColour() {
        return "68bbd2";
    }

    @Override
    public String getGamecenterTextColour() {
        return "";
    }

    @Override
    public String getGamecenterAssetsPath() {
        return "https://assets.thefastfoodgame.com/gamecenter/gamecenter_basejump/";
    }

    @Override
    public Boolean getGameStatus() {
        return true;
    }

    @Override
    public int getGamesLeftCount(Player player) {
        return 3;
    }

    public static void onPlayButton(Player player) {
        final PlayerData playerData = player.getData();

        try {
            String postData = "";
            postData += "api-key=" + URLEncoder.encode(Locale.getOrDefault("gamecenter.fastfood.api_key", "8EEB61-C03306-3101AB-84A57D-8106D1"), "UTF-8") + "&";
            postData += "user-id=" + URLEncoder.encode(playerData.getId() + "", "UTF-8") + "&";
            postData += "user-name=" + URLEncoder.encode(playerData.getUsername(), "UTF-8") + "&";
            postData += "user-avatar=" + URLEncoder.encode(playerData.getFigure(), "UTF-8") + "&";
            postData += "theme=" + URLEncoder.encode(Locale.getOrDefault("gamecenter.fastfood.theme", "default"), "UTF-8");

            final String url = "https://api.thefastfoodgame.com/api";
            final URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "arcturus");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", "" + postData.getBytes().length);
            con.setDoOutput(true);

            final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(postData.getBytes());
            wr.flush();
            wr.close();

            final int responseCode = con.getResponseCode();

            if(responseCode != 200) {
                throw new Exception("FastFood - Invalid http response code " + responseCode);
            }

            final InputStream stream = con.getInputStream();

            final byte[] d = new byte[stream.available()];
            stream.read(d,0, d.length);
            final String responseBody = new String(d);

            stream.close();
            con.disconnect();

            final Gson gson = new Gson();
            final JsonObject apiResponse = gson.fromJson(responseBody, JsonObject.class);

            if(apiResponse.get("status").getAsString().equals("success")) {
                playerData.getPlayer().getSession().send(new GameCenterGameFrameURLComposer(apiResponse.get("url").getAsString(), 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
