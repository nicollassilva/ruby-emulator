package com.cometproject.server.network.messages.rcon;

import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.rcon.utils.RCONMessage;
import com.cometproject.server.network.sessions.Session;
import com.google.gson.Gson;

public class ForwardUser extends RCONMessage<ForwardUser.ForwardUserJSON> {
    public ForwardUser() {
        super(ForwardUserJSON.class);
    }

    @Override
    public void handle(Gson gson, ForwardUserJSON object) {
        Session client = NetworkManager.getInstance().getSessions().fromPlayer(object.user_id);

        if(client == null || client.getPlayer() == null) {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }

        if (System.currentTimeMillis() - client.getPlayer().getLastRoomRequest() < 500) {
            return;
        }

        if (client.getPlayer().getEntity() != null && !client.getPlayer().isSpectating(object.room_id) && !client.getPlayer().hasQueued(object.room_id)) {
            if (!client.getPlayer().getEntity().isFinalized()) {
                client.getPlayer().setEntity(null);
            }
        }

        client.getPlayer().setLastRoomRequest(System.currentTimeMillis());
        RoomManager.getInstance().initializeRoom(client, object.room_id, "");
    }

    static class ForwardUserJSON {
        public int user_id;
        public int room_id;
    }
}
