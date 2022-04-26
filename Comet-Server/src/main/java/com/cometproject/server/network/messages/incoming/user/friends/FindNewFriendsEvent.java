package com.cometproject.server.network.messages.incoming.user.friends;

import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.friends.FriendFindingRoomComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class FindNewFriendsEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().antiSpam("FindNewFriendsMessageEvent", 5)) {
            return;
        }

        final int roomId = RoomManager.getInstance().getRandomActiveRoom();

        if(roomId < 0) {
            client.send(new FriendFindingRoomComposer(FriendFindingRoomComposer.NO_ROOM_FOUND));
            return;
        }

        client.send(new RoomForwardMessageComposer(roomId));
    }
}
