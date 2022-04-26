package com.cometproject.server.network.messages.incoming.user.profile;

import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.profile.LoadProfileMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class GetProfileByUsernameMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String username = msg.readString();

        IPlayer player = username.equals(client.getPlayer().getData().getUsername()) ? client.getPlayer() : getPlayer(username);

        if (player == null) {
            return;
        }

        client.send(new LoadProfileMessageComposer(player, client.getPlayer().getMessenger().getFriendById(player.getData().getId()) != null, false));
    }

    public IPlayer getPlayer(String username) {
        final ISession playerSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if(playerSession != null) {
            return playerSession.getPlayer();
        }

        return PlayerManager.getInstance().getPlayerByUsername(username);
    }
}
