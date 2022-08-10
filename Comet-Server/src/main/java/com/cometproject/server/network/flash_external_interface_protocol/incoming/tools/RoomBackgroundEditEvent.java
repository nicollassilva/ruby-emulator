package com.cometproject.server.network.flash_external_interface_protocol.incoming.tools;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;

public class RoomBackgroundEditEvent extends IncomingExternalInterfaceMessage<RoomBackgroundEditEvent.JSONRoomBackgroundEditEvent> {

    public RoomBackgroundEditEvent() {
        super(JSONRoomBackgroundEditEvent.class);
    }

    @Override
    public void handle(Session client, JSONRoomBackgroundEditEvent message) {
        Room room = client.getPlayer().getEntity().getRoom();
        if (room != null && (room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().canControlMpu())) {
            RoomItemFloor item = room.getItems().getFloorItem(message.itemId);
            if(item == null ) return;
            String data = "state" + (char) 9 + "0";
            data = data + (char) 9 + "imageUrl" + (char) 9 + message.url + (char) 9 + "offsetX" + (char) 9 + message.x + (char) 9 + "offsetY" + (char) 9 + message.y + (char) 9 + "offsetZ" + (char) 9 + message.z;
            item.getItemData().setData(data);

            item.sendUpdate();
            item.saveData();
        }
    }

    static class JSONRoomBackgroundEditEvent {
        int itemId;
        String url;
        int x;
        int y;
        int z;
    }
}
