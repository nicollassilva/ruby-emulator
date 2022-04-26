package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class SaveBrandingMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int brandingId = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        RoomItemFloor item = room.getItems().getFloorItem(brandingId);
        if(item == null ) {
            return;
        }

        int length = msg.readInt();
        if(length > 1000) {
            return;
        }

        String data = "state" + (char) 9 + "0";

        for (int i = 1; i <= length; i++) {
            if (i < length) {
                data = data + (char) 9 + msg.readString();
            } else {
                int offsetz;
                try {
                    offsetz = Integer.valueOf(msg.readString());
                } catch (NumberFormatException e) {
                    offsetz = 0;
                }
                if (offsetz < 140000000) {
                    data = data + (char) 9 + offsetz;
                } else {
                    data = data + (char) 9 + "0";
                }
            }
        }

        data = data.replace("https", "http");

        item.getItemData().setData(data);

        item.sendUpdate();
        item.saveData();
    }
}
