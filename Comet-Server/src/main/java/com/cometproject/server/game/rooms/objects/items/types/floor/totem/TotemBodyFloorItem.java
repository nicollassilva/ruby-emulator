package com.cometproject.server.game.rooms.objects.items.types.floor.totem;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums.TotemColor;
import com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums.TotemType;
import com.cometproject.server.game.rooms.types.Room;

public class TotemBodyFloorItem extends TotemPartFloorItem {
    public TotemBodyFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public TotemColor getTotemColor() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getItemData().getData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }
        return TotemColor.fromInt(extraData - (4 * (getTotemType().type - 1)));
    }

    public TotemType getTotemType() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getItemData().getData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }
        return TotemType.fromInt((int)Math.ceil((extraData + 1) / 4.0f));
    }
}
