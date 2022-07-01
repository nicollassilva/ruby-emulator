package com.cometproject.server.network.messages.incoming.navigator;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.GameContext;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.navigator.CreateRoomMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.crafting.CraftingDao;


public class CreateRoomMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        final String name = msg.readString();
        final String description = msg.readString();
        final String model = msg.readString();
        final int category = msg.readInt();
        final int maxVisitors = msg.readInt();
        final int tradeState = msg.readInt();
        final int creationTime = (int)Comet.getTime();

        if (client.getPlayer().antiSpam("CreateRoomMessageEvent", 1)) {
            return;
        }

        //size of rooms for users
        if (client.getPlayer().getRooms().size() >= CometSettings.roomsForUsers) {
            return;
        }

        if(tradeState > 2) {
            return;
        }

        if(name.trim().length() < 3 || name.length() > 25) {
            return;
        }

        if(description.length() > 128) {
            return;
        }

        int lastRoomCreatedDifference = ((int) Comet.getTime()) - client.getPlayer().getLastRoomCreated();

        if (lastRoomCreatedDifference < 30) {
            client.send(new MotdNotificationMessageComposer(Locale.getOrDefault("room.creation.time", "Você pode criar somente um quarto a cada 30 segundos. (Você ainda tem " + (30 - lastRoomCreatedDifference) + " segundos restantes).")));
            return;
        }

        if (GameContext.getCurrent().getRoomModelService().getModel(model) == null) {
            client.send(new MotdNotificationMessageComposer("Modelo de quarto inválido."));
            return;
        }

        final int roomId = RoomManager.getInstance().createRoom(name, description, model, category, maxVisitors, tradeState, creationTime, client);

        client.send(new CreateRoomMessageComposer(roomId, name));
        client.getPlayer().setLastRoomCreated((int) Comet.getTime());
    }
}
