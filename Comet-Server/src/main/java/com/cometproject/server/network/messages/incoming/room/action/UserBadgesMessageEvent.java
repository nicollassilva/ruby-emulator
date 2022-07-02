package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.profile.UserBadgesMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class UserBadgesMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        final int userId = msg.readInt();

        if (client.getPlayer() == null || client.getPlayer().getInventory() == null) {
            return;
        }

        if (client.getPlayer().getId() == userId) {
            client.send(new UserBadgesMessageComposer(client.getPlayer().getId(), client.getPlayer().getInventory().equippedBadges()));
            return;
        }

        final String[] playerBadges = getUserBadges(userId);

        client.send(new UserBadgesMessageComposer(userId, playerBadges));
    }

    public String[] getUserBadges(int userId) {
        final ISession playerSession = NetworkManager.getInstance().getSessions().getByPlayerId(userId);

        if(playerSession != null) {
            return playerSession.getPlayer().getInventory().equippedBadges();
        }

        return PlayerDao.getEquippedBadgesByPlayerId(userId);
    }
}