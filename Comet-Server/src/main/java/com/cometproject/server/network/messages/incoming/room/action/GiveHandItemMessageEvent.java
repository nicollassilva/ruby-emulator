package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class GiveHandItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int userId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        if (!client.getPlayer().getEntity().isVisible())
            return;

        final PlayerEntity providerEntity = client.getPlayer().getEntity();
        final PlayerEntity receivingEntity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlayerId(userId);

        if (receivingEntity == null)
            return;

        if(!providerEntity.getPosition().touching(receivingEntity.getPosition())) {
            if(!providerEntity.getPlayer().getEntity().isFreeze() && !providerEntity.hasAttribute("interacttpencours") && !providerEntity.hasAttribute("tptpencours")) {
                final Position sqInFront = receivingEntity.getPosition().squareInFront(receivingEntity.getBodyRotation());

                providerEntity.moveTo(sqInFront.getX(), sqInFront.getY());
            }

            return;
        }

        receivingEntity.carryItem(providerEntity.getHandItem());
        providerEntity.carryItem(0);
    }
}