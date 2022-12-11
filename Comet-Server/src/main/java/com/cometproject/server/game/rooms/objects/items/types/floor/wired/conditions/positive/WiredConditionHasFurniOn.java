package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.types.Room;


public class WiredConditionHasFurniOn extends WiredConditionItem {
    private final static int PARAM_MODE = 0;

    public WiredConditionHasFurniOn(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 7;
    }

    public int getMode() {
        return this.getWiredData().getParams().get(PARAM_MODE);
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        int mode;

        if (this.getWiredData().getSelectedIds().size() == 0) {
            return true; //none selected.
        }

        try {
            mode = this.getWiredData().getParams().get(PARAM_MODE);
        } catch (Exception e) {
            mode = 0;
        }

        int selectedItemsWithFurni = 0;

        boolean result;

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem != null) {
                for (final RoomItemFloor itemOnSq : floorItem.getItemsOnStack()) {

                    if (itemOnSq.getId() == floorItem.getId())
                        continue;

                    if (itemOnSq.getPosition().getZ() != 0.0 && itemOnSq.getPosition().getZ() >= floorItem.getPosition().getZ()) {
                        if (mode == 0) {
                            return !this.isNegative;
                        }
                        selectedItemsWithFurni++;
                    } else {
                        if (mode == 1) {
                            return this.isNegative;
                        }
                    }
                }
            }
        }

        result = selectedItemsWithFurni == this.getWiredData().getSelectedIds().size();

        return this.isNegative != result;
    }
}