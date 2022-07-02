package com.cometproject.server.network.messages.incoming.user.profile;

import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.profile.LoadProfileMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class GetProfileMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        final int userId = msg.readInt();

        final IPlayer player = userId == client.getPlayer().getId() ? client.getPlayer() : getPlayer(userId);

        if (player == null) {
            return;
        }

        client.send(new LoadProfileMessageComposer(player, client.getPlayer().getMessenger().getFriendById(userId) != null, false));
    }

    public IPlayer getPlayer(int userId) {
        final ISession playerSession = NetworkManager.getInstance().getSessions().getByPlayerId(userId);

        if(playerSession != null) {
            return playerSession.getPlayer();
        }

        return PlayerManager.getInstance().getPlayerById(userId);
    }
}
