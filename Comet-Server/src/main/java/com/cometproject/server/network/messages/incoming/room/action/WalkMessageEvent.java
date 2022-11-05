package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom.WiredTriggerCustomSensorMovement;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.LinkedList;
import java.util.List;


public class WalkMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        final long startTime = System.currentTimeMillis();

        final int goalX = msg.readInt();
        final int goalY = msg.readInt();

        try {
            if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().hasAttribute("warp")) {
                return;
            }

            final PlayerEntity entity = client.getPlayer().getEntity();

            if (!entity.isVisible())
                return;

            WiredTriggerCustomSensorMovement.executeTriggers(client.getPlayer().getEntity());

            if (entity.hasAttribute("tp")) {
                return;
            }


            if (entity.hasAttribute("teleport")) {
                final Position newPosition = new Position(goalX, goalY);
                final RoomTile nextTile = client.getPlayer().getEntity().getRoom().getMapping().getTile(newPosition);
                final RoomTile currentTile = client.getPlayer().getEntity().getTile();

                entity.cancelWalk();
                entity.unIdle();

                if (currentTile != null) {
                    entity.removeFromTile(currentTile);

                    if (currentTile.getTopItemInstance() != null) {
                        currentTile.getTopItemInstance().onEntityStepOff(entity);
                    }
                }

                newPosition.setZ(nextTile.getWalkHeight());
                entity.warpImmediately(newPosition);

                entity.removeStatus(RoomEntityStatus.LAY);
                entity.removeStatus(RoomEntityStatus.SIT);

                return;
            }

            if (!entity.sendUpdateMessage()) {
                entity.setSendUpdateMessage(true);
            }

            if (entity.canWalk() && (!entity.isOverriden() || entity.isOverriden() && entity.isOverrideA()) && entity.isVisible()) {
                entity.moveTo(goalX, goalY);
            }

        } catch (Exception e) {
            //client.getLogger().error("Error while finding path", e);
            e.printStackTrace();
        }

        System.out.println("pathfinding took " + (System.currentTimeMillis() - startTime) + "ms");

    }

}
