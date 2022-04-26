package com.cometproject.server.network.battleball.incoming.handshake;

import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.battleball.Server;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class SSOLoginEvent extends IncomingEvent {

    private static final Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {

        String ssoTicket = this.data.getJSONObject("data").getString("auth_ticket");
        JSONObject output = new JSONObject();

        System.out.println("GET SSOTICKET TO PLAYER ID " + PlayerManager.getInstance().getSsoTicketToPlayerId());

        if(PlayerManager.getInstance().getSsoTicketToPlayerId().containsKey(ssoTicket)) {

            int userId = PlayerManager.getInstance().getSsoTicketToPlayerId().get(ssoTicket);
            PlayerData userData = PlayerManager.getInstance().getDataByPlayerId(userId);
            //PlayerEntity userEntity = (PlayerEntity) SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(userId).getPlayer().getEntity();
            //userEntity.setAttribute("socket.session", this.session);
            userData.setWebsocketSession(this.session);

            System.out.println(userData.getUsername() + " connected to WebSocket server: " + userData.getWebsocketSession().isOpen());


            HashMap<String, String> player = new HashMap<>();
            player.put("id", String.valueOf(userData.getId()));
            player.put("username", userData.getUsername());
            player.put("auth_ticket", ssoTicket);

            Server.userMap.put(this.session, player);

            output.put("authenticated", true);


        } else {
            log.info("WebSocket SSO verification failed for [" + ssoTicket + "]");

            output.put("authenticated", false);
        }

        Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.SSOVerifiedMessage);
        OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();
        message.client = this.session;
        message.data = output;

        message.compose();

    }
}
