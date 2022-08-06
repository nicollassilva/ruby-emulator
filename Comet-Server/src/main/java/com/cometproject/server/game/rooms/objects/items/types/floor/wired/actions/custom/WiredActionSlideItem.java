package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.DiceFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;

public class WiredActionSlideItem extends WiredActionItem {

    public WiredActionSlideItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        try {
            final String[] values = this.getWiredData().getText().split(",");

            if (values.length <= 0) {
                return;
            }

            int x = 0;
            int y = 0;
            double z = 0;

            for (final String value : values) {
                final String[] entries = value.split(":");

                if (entries.length != 2) {
                    return;
                }

                switch (entries[0]) {
                    case "x":
                        x = Integer.parseInt(entries[1]);
                        break;
                    case "y":
                        y = Integer.parseInt(entries[1]);
                        break;
                    case "z":
                        z = Double.parseDouble(entries[1]);
                        break;
                }
            }

            synchronized (this.getWiredData().getSelectedIds()) {
                for (final long itemId : this.getWiredData().getSelectedIds()) {
                    final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

                    if (floorItem == null || floorItem instanceof DiceFloorItem) continue;

                    final Position currentPosition = floorItem.getPosition().copy();
                    final Position newPosition = new Position(currentPosition.getX() + x, currentPosition.getY() + y, currentPosition.getZ() + z);

                    if (newPosition.getX() > this.getRoom().getModel().getSizeX() || newPosition.getY() > this.getRoom().getModel().getSizeY() || newPosition.getZ() > 100) {
                        return;
                    }

                    if (newPosition.getX() == 0 || newPosition.getY() == 0 || newPosition.getZ() < -100) {
                        return;
                    }


                    if (this.getRoom().getItems().moveFloorItemWired(floorItem, newPosition, floorItem.getRotation(), false, false)) {
                        this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
                    }

                    floorItem.setPosition(newPosition);
                    floorItem.save();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getInterface() {
        return 22;
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }
}

