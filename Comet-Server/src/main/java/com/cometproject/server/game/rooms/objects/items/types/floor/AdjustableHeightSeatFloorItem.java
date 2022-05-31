package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.types.Room;
import org.apache.commons.lang.StringUtils;


public class AdjustableHeightSeatFloorItem extends SeatFloorItem {
    public AdjustableHeightSeatFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getItemData().getData().isEmpty()) {
            this.getItemData().setData("0");
        }
    }

    @Override
    public double getSitHeight() {
        if(this.getDefinition().getVariableHeights() == null) {
            return 0D;
        }

        final String itemData = this.getItemData().getData();

        if(itemData.isEmpty() || !StringUtils.isNumeric(itemData)) {
            return 0D;
        }

        int currentSitHeight;

        try {
            currentSitHeight = Integer.parseInt(itemData);
        } catch (NumberFormatException e) {
            currentSitHeight = 0;
        }

        return this.getDefinition().getVariableHeights()[currentSitHeight];
    }
}
