package com.cometproject.server.network.flash_external_interface_protocol.incoming.common;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.VideoPlayerFloorItem;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;

public class EditTVEvent extends IncomingExternalInterfaceMessage<EditTVEvent.JSONEditTVEvent> {

    public EditTVEvent() {
        super(JSONEditTVEvent.class);
    }

    @Override
    public void handle(Session client, JSONEditTVEvent message) {
        if(!client.getPlayer().getEntity().hasRights() && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;
        RoomItemFloor item = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(message.itemId);
        if(!(item instanceof VideoPlayerFloorItem))
            return;

        VideoPlayerFloorItem tv = (VideoPlayerFloorItem) item;
        tv.setAttribute("video", message.videoId);
        tv.getItemData().setData(message.videoId);
        tv.saveData();
        item.sendUpdate();
    }

    static class JSONEditTVEvent {
        int itemId;
        String videoId;
    }
}
