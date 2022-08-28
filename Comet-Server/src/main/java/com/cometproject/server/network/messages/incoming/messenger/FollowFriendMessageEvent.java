package com.cometproject.server.network.messages.incoming.messenger;

import com.cometproject.api.game.players.data.components.messenger.IMessengerFriend;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.navigator.types.search.NavigatorSearchService;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class FollowFriendMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        final int friendId = msg.readInt();
        if (client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1, 1));
            return;
        }

        final IMessengerFriend friend = client.getPlayer().getMessenger().getFriendById(friendId);
        if (friend == null || !friend.isInRoom())
            return;

        final Room friendRoom = (Room) friend.getSession().getPlayer().getEntity().getRoom();
        if (friendRoom == null || !NavigatorSearchService.checkRoomVisibility(client.getPlayer(), friendRoom)) {
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            final Room currentRoom = client.getPlayer().getEntity().getRoom();

            if (currentRoom.getId() == friendRoom.getId()) {
                client.getPlayer().getEntity().leaveRoom(false, false, false);
            }
        }

        client.send(new RoomForwardMessageComposer(friendRoom.getId()));
    }
}
