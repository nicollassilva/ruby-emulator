package com.cometproject.server.network.battleball.incoming.handshake;

import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.battleball.gameserver.GameServer;
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

public class SSOLoginEvent extends IncomingEvent {
    private static final Logger log = LogManager.getLogger(SSOLoginEvent.class.getName());
    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
        final String ssoTicket = this.data.getJSONObject("data").getString("auth_ticket");
        final JSONObject output = new JSONObject();

        if(PlayerManager.getInstance().getSsoTicketToPlayerId().containsKey(ssoTicket)) {
            final int userId = PlayerManager.getInstance().getSsoTicketToPlayerId().get(ssoTicket);

            if(userId <= 0)
                return;

            final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(userId);
            if(session == null || session.getWsChannel() != null)
                return;

            final PlayerData userData = PlayerManager.getInstance().getDataByPlayerId(userId);
            if(userData == null)
                return;

            userData.setWebsocketSession(this.session);

            final HashMap<String, String> player = new HashMap<>();

            player.put("id", String.valueOf(userData.getId()));
            player.put("username", userData.getUsername());
            player.put("auth_ticket", ssoTicket);

            GameServer.userMap.put(this.session, player);
            output.put("authenticated", true);
        } else {
            output.put("authenticated", false);
        }

        final OutgoingMessage message = OutgoingMessageManager.getInstance().getMessageInstance(Outgoing.SSOVerifiedMessage);

        if(message == null) return;

        message.client = this.session;
        message.data = output;

        message.compose();
    }
}
