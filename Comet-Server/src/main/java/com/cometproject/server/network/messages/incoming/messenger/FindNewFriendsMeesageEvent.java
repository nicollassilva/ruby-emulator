package com.cometproject.server.network.messages.incoming.messenger;

import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class FindNewFriendsMeesageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().antiSpam("FindNewFriendsMessageEvent", 1)) {
            return;
        }

        final int roomId = RoomManager.getInstance().getRandomActiveRoom();

        client.send(new RoomForwardMessageComposer(roomId));
    }
}
