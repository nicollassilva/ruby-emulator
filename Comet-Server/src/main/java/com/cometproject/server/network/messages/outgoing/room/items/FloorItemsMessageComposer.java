package com.cometproject.server.network.messages.outgoing.room.items;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredAddonNoItemsAnimateEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonKebBar;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;


public class FloorItemsMessageComposer extends MessageComposer {
    private final Room room;

    public FloorItemsMessageComposer(final Room room) {
        this.room = room;
    }

    @Override
    public short getId() {
        return Composers.ObjectsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (room.getItems().getFloorItems().size() > 0) {
            msg.writeInt(room.getItems().getItemOwners().size());

            for (final Map.Entry<Integer, String> itemOwner : room.getItems().getItemOwners().entrySet()) {
                msg.writeInt(itemOwner.getKey());
                msg.writeString(itemOwner.getValue());
            }

            if (room.getData().isWiredHidden()) {
                final List<RoomItemFloor> items = Lists.newArrayList();

                for (final RoomItemFloor item : room.getItems().getFloorItems().values()) {
                    if (!(item instanceof WiredFloorItem) && !(item instanceof WiredAddonRandomEffect) && !(item instanceof WiredAddonUnseenEffect) && !(item instanceof WiredAddonNoItemsAnimateEffect) && !(item instanceof WiredAddonKebBar)) {
                        items.add(item);
                    }
                }

                msg.writeInt(items.size());

                for (final RoomItemFloor item : items) {
                    item.serialize(msg);
                }
            } else {
                msg.writeInt(room.getItems().getFloorItems().size());

                for (final RoomItemFloor item : room.getItems().getFloorItems().values()) {
                    item.serialize((msg));
                }
            }

        } else {
            msg.writeInt(0);
            msg.writeInt(0);
        }

    }
}
