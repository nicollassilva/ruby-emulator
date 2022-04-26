package com.cometproject.server.network.messages.outgoing.room.items;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.wall.PostItWallItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.Map;


public class WallItemsMessageComposer extends MessageComposer {
    private final Room room;

    public WallItemsMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    public short getId() {
        return Composers.ItemsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        int size = room.getItems().getWallItems().size();

        if (size > 0) {
            msg.writeInt(room.getItems().getItemOwners().size());

            for (final Map.Entry<Integer, String> itemOwner : room.getItems().getItemOwners().entrySet()) {
                msg.writeInt(itemOwner.getKey());
                msg.writeString(itemOwner.getValue());
            }

        } else {
            msg.writeInt(0);
        }

        msg.writeInt(size);

        for (final RoomItemWall item : room.getItems().getWallItems().values()) {
            msg.writeString(item.getVirtualId());
            msg.writeInt(item.getDefinition().getSpriteId());
            msg.writeString(item.getWallPosition());

            msg.writeString(item instanceof PostItWallItem ? item.getItemData().getData().split(" ")[0] : item.getItemData().getData());

            msg.writeInt(-1);
            msg.writeInt(item.isUsable() ? 1 : 0);
            msg.writeInt(item.getItemData().getOwnerId());
        }
    }
}
