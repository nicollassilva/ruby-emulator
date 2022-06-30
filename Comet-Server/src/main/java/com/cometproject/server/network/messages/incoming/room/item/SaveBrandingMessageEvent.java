package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class SaveBrandingMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int brandingId = msg.readInt();

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()))
            return;

        final RoomItemFloor item = room.getItems().getFloorItem(brandingId);

        if(item == null)
            return;

        final int length = msg.readInt();

        if(length > 1000)
            return;

        final StringBuilder data = new StringBuilder("state" + (char) 9 + "0");

        for (int i = 1; i <= length; i++) {
            if (i < length) {
                data.append((char) 9).append(msg.readString());
            } else {
                int offsetz;

                try {
                    offsetz = Integer.parseInt(msg.readString());
                } catch (NumberFormatException e) {
                    offsetz = 0;
                }
                if (offsetz < 140000000) {
                    data.append((char) 9).append(offsetz);
                } else {
                    data.append((char) 9).append("0");
                }
            }
        }

        final boolean isEmptyImageUrl = data.toString().startsWith("state" + (char) 9 + "0" + (char) 9 + "imageUrl" + (char) 9 + (char) 9);
        final boolean securityImageUrl = data.toString().startsWith("state" + (char) 9 + "0" + (char) 9 + "imageUrl" + (char) 9 + (Comet.isDebugging ? "http://localhost:3000" : "https://rubyhotel.com.br"));

        if(!isEmptyImageUrl && !securityImageUrl)
            return;

        item.getItemData().setData(data.toString());

        item.sendUpdate();
        item.saveData();
    }
}
