package com.cometproject.server.network.messages.rcon;

import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.rcon.utils.RCONMessage;
import com.cometproject.server.network.sessions.Session;
import com.google.gson.Gson;

import java.util.List;

public class DisconnectUser extends RCONMessage<DisconnectUser.DisconnectUserJSON> {

    public DisconnectUser() {
        super(DisconnectUserJSON.class);
    }

    @Override
    public void handle(Gson gson, DisconnectUserJSON json) {
        int playerId = -1;

        try {
            playerId = Integer.parseInt(json.user_data);
        } catch (NumberFormatException ignored) {}

        if(playerId <= 0) return;

        switch (json.type) {
            case "user" -> {
                final Session client = NetworkManager.getInstance().getSessions().fromPlayer(playerId);

                if (client == null)
                    return;

                client.disconnect();
            }
            case "ip" -> {
                final Session client = NetworkManager.getInstance().getSessions().fromPlayer(playerId);

                if(client == null)
                    return;

                final List<Integer> ids = PlayerManager.getInstance().getPlayerIdsByIpAddress(client.getIpAddress());

                if(ids.isEmpty())
                    return;

                for (Integer id : ids) {
                    final Session session = NetworkManager.getInstance().getSessions().fromPlayer(id);

                    if (session == null)
                        continue;

                    session.disconnect();
                }
            }
            case "machine" -> {
                final List<ISession> sessions = NetworkManager.getInstance().getSessions().getPlayersIdFromUniqueId(playerId);

                if(sessions == null || sessions.isEmpty())
                    return;

                for (ISession session : sessions) {
                    if (session == null)
                        continue;

                    session.disconnect();
                }
            }
        }
    }

    static class DisconnectUserJSON {
        public String user_data;
        public String type;
    }
}
