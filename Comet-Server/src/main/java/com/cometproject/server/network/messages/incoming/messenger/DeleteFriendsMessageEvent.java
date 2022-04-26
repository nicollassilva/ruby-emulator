package com.cometproject.server.network.messages.incoming.messenger;

import com.cometproject.api.game.players.data.components.messenger.IMessengerFriend;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.config.Locale;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.messenger.MessengerDao;
import com.cometproject.server.storage.queries.player.relationships.RelationshipDao;


public class DeleteFriendsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int friendCount = msg.readInt();

        if(client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1,1));

            return;
        }

        for (int i = 0; i < friendCount; i++) {
            int userId = msg.readInt();

            final IMessengerFriend friend = client.getPlayer().getMessenger().getFriendById(userId);

            if (friend == null)
                continue;

            final ISession friendClient = friend.getSession();

            if (friendClient != null && friendClient.getPlayer() != null) {
                friendClient.getPlayer().getMessenger().removeFriend(client.getPlayer().getId());

                if (friendClient.getPlayer().getRelationships().get(client.getPlayer().getId()) != null) {
                    RelationshipDao.deleteRelationship(userId, client.getPlayer().getId());
                    friendClient.getPlayer().getRelationships().remove(client.getPlayer().getId());
                }
            } else {
                MessengerDao.deleteFriendship(userId, client.getPlayer().getId());
                RelationshipDao.deleteRelationship(userId, client.getPlayer().getId());
            }

            if (client.getPlayer().getRelationships().get(userId) != null) {
                RelationshipDao.deleteRelationship(client.getPlayer().getId(), userId);
                client.getPlayer().getRelationships().remove(userId);
            }

            client.getPlayer().getMessenger().removeFriend(userId);
        }
    }
}
