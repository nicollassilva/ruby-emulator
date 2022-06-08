package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.MagicStackFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackHeightTileHeightComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.List;


public class SaveStackToolMessageEvent implements Event {
    private static final int MAX_HEIGHT = 100;

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }
        if (!room.getRights().hasRights(client.getPlayer().getEntity().getPlayerId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            client.disconnect();
            return;
        }

        final int itemId = msg.readInt();
        double height = msg.readInt();

        if (height < 0 && height != -100) {
            return;
        }

        final RoomItemFloor floorItem = room.getItems().getFloorItem(itemId);

        if (!(floorItem instanceof MagicStackFloorItem))
            return;

        final MagicStackFloorItem magicStackFloorItem = ((MagicStackFloorItem) floorItem);

        final boolean placeOverFurni = height == -100;
        height = height / 100d;

        if(!placeOverFurni){
            // height >= floor height <= 100
            if(height < magicStackFloorItem.getTile().getTileHeight()){
                height = magicStackFloorItem.getTile().getTileHeight();
            }

            if(height > MAX_HEIGHT){
                height = MAX_HEIGHT;
            }

            magicStackFloorItem.setOverrideHeight(height);
        }

        double highestHeight = magicStackFloorItem.getTile().getTopHeight(magicStackFloorItem);

        final List<AffectedTile> affectedTiles = AffectedTile.getAffectedBothTilesAt(
                magicStackFloorItem.getDefinition().getLength(),
                magicStackFloorItem.getDefinition().getWidth(),
                magicStackFloorItem.getPosition().getX(),
                magicStackFloorItem.getPosition().getY(),
                magicStackFloorItem.getRotation()
        );

        for (final AffectedTile affectedTile : affectedTiles) {
            final RoomTile tile = magicStackFloorItem.getRoom().getMapping().getTile(affectedTile.x, affectedTile.y);

            if(placeOverFurni) {
                final double affectedTileHeight = tile.getTopHeight(magicStackFloorItem);

                if (affectedTileHeight > highestHeight) {
                    highestHeight = affectedTileHeight;
                }
            }

            tile.reload();
            room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
        }

        if(placeOverFurni) {
            height = highestHeight;
            magicStackFloorItem.setOverrideHeight(highestHeight);
        }

        magicStackFloorItem.sendUpdate();
        magicStackFloorItem.saveData();

        client.send(new UpdateStackHeightTileHeightComposer(itemId, (int) (height > 0 ? (height * 100) : 0)));
    }
}
