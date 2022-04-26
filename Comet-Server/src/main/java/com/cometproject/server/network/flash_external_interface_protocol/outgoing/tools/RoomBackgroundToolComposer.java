package com.cometproject.server.network.flash_external_interface_protocol.outgoing.tools;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class RoomBackgroundToolComposer extends OutgoingExternalInterfaceMessage {
    public RoomBackgroundToolComposer(RoomItemFloor[] items) {
        super("room_bg");
        JsonArray itemsJson = new JsonArray();
        for(RoomItemFloor item : items) {
            JsonObject itemJson = new JsonObject();
            String[] dataArray = item.getItemData().getData().split(String.valueOf((char) 9));
            itemJson.add("id", new JsonPrimitive(item.getVirtualId()));
            itemJson.add("data", new JsonPrimitive(dataArray.length >=3 ? dataArray[3] : ""));
            itemJson.add("x", new JsonPrimitive(dataArray.length >= 5 ? Integer.parseInt(dataArray[5]): 0));
            itemJson.add("y", new JsonPrimitive(dataArray.length >= 7 ? Integer.parseInt(dataArray[7]): 0));
            itemJson.add("z", new JsonPrimitive(dataArray.length >= 9 ? Integer.parseInt(dataArray[9]): 0));
            itemsJson.add(itemJson);
        }
        this.data.add("items", itemsJson);
    }
}
