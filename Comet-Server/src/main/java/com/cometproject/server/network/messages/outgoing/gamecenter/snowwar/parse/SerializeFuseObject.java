package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.GamefuseObject;
import com.cometproject.server.game.snowwar.Tile;
import com.cometproject.server.game.snowwar.items.MapStuffData;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeFuseObject {
    public static void parse(final IComposer msg, final GamefuseObject fuseItem) {
        msg.writeString(fuseItem.baseItem.Name);
        msg.writeInt(fuseItem.itemId);
        msg.writeInt(fuseItem.X);
        msg.writeInt(fuseItem.Y);
        msg.writeInt(fuseItem.baseItem.xDim);
        msg.writeInt(fuseItem.baseItem.yDim);
        msg.writeInt((int) (fuseItem.baseItem.Height * Tile.TILE_SIZE));
        msg.writeInt(fuseItem.Rot);
        msg.writeInt(fuseItem.Z);
        msg.writeBoolean(fuseItem.baseItem.allowWalk);
        if (fuseItem.extraData instanceof MapStuffData) {
            msg.writeInt(1);
            fuseItem.extraData.serializeComposer(msg);
        } else {
            msg.writeInt(0);
            fuseItem.extraData.serializeComposer(msg);
        }
    }
}
