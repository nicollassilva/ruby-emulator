package com.cometproject.server.network.battleball.incoming.battlepass;

import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.battleball.Server;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.sessions.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class OpenBattlePassEvent extends IncomingEvent {

    private static Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {

        HashMap<String, String> player = Server.userMap.get(this.session);
        Session client = (Session) SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));

        double currentLevel = client.getPlayer().getData().getLevel();
        double xpNeedCurrent = currentLevel * 500 + ((currentLevel - 1) * currentLevel) * 2.5;
        double xpNeedNext = (currentLevel + 1) * 500 + (((currentLevel + 1) -1) * (currentLevel + 1)) * 2.5;
        double xpNeed = xpNeedNext - xpNeedCurrent;
        double xpCurrent = client.getPlayer().getData().getXp() - xpNeedCurrent;

        //System.out.println(client.getPlayer().getData().getXp());
        //client.getPlayer().getData().increaseXp(100);
        //client.getPlayer().getData().save();


        JSONObject output = new JSONObject();

        JSONObject info = new JSONObject();

        info.put("xp_need", xpNeed);
        info.put("xp_current", xpCurrent);

        JSONObject noVip = new JSONObject();

        for (int i=1; i<=8; i++) {
            JSONObject passData = new JSONObject();
            passData.put("level", i);
            passData.put("type", client.getPlayer().getData().battlePassType(i, false).get("type"));
            passData.put("color", client.getPlayer().getData().battlePassType(i, false).get("color"));
            passData.put("image", client.getPlayer().getData().battlePassType(i, false).get("image"));
            passData.put("unlocked", client.getPlayer().getData().battlePassGiftUnlocked(i, false));

            noVip.put(String.valueOf(i), passData);
        }

        JSONObject vip = new JSONObject();

        for (int i=1; i<=8; i++) {
            JSONObject passData = new JSONObject();
            passData.put("level", i);
            passData.put("type", client.getPlayer().getData().battlePassType(i, true).get("type"));
            passData.put("color", client.getPlayer().getData().battlePassType(i, true).get("color"));
            passData.put("image", client.getPlayer().getData().battlePassType(i, true).get("image"));
            passData.put("unlocked", client.getPlayer().getData().battlePassGiftUnlocked(i, true));

            vip.put(String.valueOf(i), passData);
        }

        output.put("info", info);

        output.put("novip", noVip);
        output.put("vip", vip);

        Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenBattlePassMessage);

        OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();
        message.client = this.session;
        message.data = output;

        message.compose();

    }
}

