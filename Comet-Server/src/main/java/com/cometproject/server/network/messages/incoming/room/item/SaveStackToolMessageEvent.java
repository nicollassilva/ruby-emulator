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

import java.util.ArrayList;
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
        final double height = msg.readInt() / 100d;

        if (height < 0 && height != -1) {
            return;
        }

        final RoomItemFloor floorItem = room.getItems().getFloorItem(itemId);

        if (!(floorItem instanceof MagicStackFloorItem))
            return;

        final MagicStackFloorItem magicStackFloorItem = ((MagicStackFloorItem) floorItem);
        final List<AffectedTile> affectedTiles = AffectedTile.getAffectedBothTilesAt(
                magicStackFloorItem.getDefinition().getLength(),
                magicStackFloorItem.getDefinition().getWidth(),
                magicStackFloorItem.getPosition().getX(),
                magicStackFloorItem.getPosition().getY(),
                magicStackFloorItem.getRotation()
        );
        final List<RoomTile> tiles = new ArrayList<>(affectedTiles.size());

        final boolean placeOverFurni = height == -1;
        if(placeOverFurni){
            double highestHeight = 0d;
            for (final AffectedTile affectedTile : affectedTiles) {
                final RoomTile tile = magicStackFloorItem.getRoom().getMapping().getTile(affectedTile.x, affectedTile.y);
                tile.reload();
                tiles.add(tile);
                final double affectedTileHeight = tile.getTopHeight(magicStackFloorItem);
                if (affectedTileHeight > highestHeight) {
                    highestHeight = affectedTileHeight;
                }
            }

            magicStackFloorItem.setOverrideHeight(Math.min(highestHeight, RoomTile.INVALID_STACK_HEIGHT));
        }
        else{
            // height >= floor height <= 100
            final double tileOrHeight = Math.max(magicStackFloorItem.getTile().getTileHeight(), height);
            final double heightOrMaxAllowedValue = Math.min(tileOrHeight, RoomTile.INVALID_STACK_HEIGHT);
            magicStackFloorItem.setOverrideHeight(heightOrMaxAllowedValue);
            for (final AffectedTile affectedTile : affectedTiles) {
                final RoomTile tile = magicStackFloorItem.getRoom().getMapping().getTile(affectedTile.x, affectedTile.y);
                tile.reload();
                tiles.add(tile);
            }
        }


        magicStackFloorItem.sendUpdate();
        magicStackFloorItem.saveData();
        client.send(new UpdateStackMapMessageComposer(tiles));
        client.send(new UpdateStackHeightTileHeightComposer(itemId, (int) (magicStackFloorItem.getOverrideHeight() > 0 ? (magicStackFloorItem.getOverrideHeight() * 100) : 0)));
    }
}
