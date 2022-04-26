package com.cometproject.server.network.messages.incoming.messenger;

import com.cometproject.api.game.players.IPlayer;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.components.types.messenger.MessengerFriend;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.messenger.UpdateFriendStateMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.messenger.MessengerDao;

import java.util.ArrayList;
import java.util.List;


public class AcceptFriendshipMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int amount = msg.readInt();
        List<Integer> requests = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            requests.add(client.getPlayer().getMessenger().getRequestBySender(msg.readInt()));
        }

        for (Integer request : requests) {
            if (request == null) continue;

            MessengerDao.createFriendship(request, client.getPlayer().getId());
            MessengerDao.deleteRequestData(request, client.getPlayer().getId());

            Session friend = NetworkManager.getInstance().getSessions().getByPlayerId(request);

            if (friend != null) {
                final boolean clientIsOnline = PlayerManager.getInstance().isOnline(client.getPlayer().getData().getId()) && !client.getPlayer().getSettings().getHideOnline();
                final boolean friendIsOnline = PlayerManager.getInstance().isOnline(friend.getPlayer().getData().getId()) && !friend.getPlayer().getSettings().getHideOnline();

                friend.getPlayer().getMessenger().addFriend(new MessengerFriend(client.getPlayer().getId(), client.getPlayer().getData()));
                friend.getPlayer().getMessenger().sendStatus(friendIsOnline, friend.getPlayer().getSettings().allowedFollowToRoom());

                client.getPlayer().getMessenger().addFriend(new MessengerFriend(request, friend.getPlayer().getData()));
                client.getPlayer().getMessenger().sendStatus(clientIsOnline, friend.getPlayer().getSettings().allowedFollowToRoom());
            } else {
                IPlayer playerFriend = PlayerManager.getInstance().getPlayerById(request);

                if(playerFriend == null) {
                    return;
                }

                client.getPlayer().getMessenger().addFriend(new MessengerFriend(request, playerFriend.getData()));
                client.send(new UpdateFriendStateMessageComposer(playerFriend.getData(), false, false, null));
            }

            client.getPlayer().getMessenger().removeRequest(request);
        }

        requests.clear();
    }
}
