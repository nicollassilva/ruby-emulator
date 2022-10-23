package com.cometproject.server.game.rooms.objects.items.types.floor.totem;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums.TotemPlanetType;
import com.cometproject.server.game.rooms.types.Room;

public class TotemPlanetFloorItem extends TotemPartFloorItem {
    public TotemPlanetFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public TotemPlanetType getPlanetType() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getItemData().getData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }

        return TotemPlanetType.fromInt(extraData);
    }
}
