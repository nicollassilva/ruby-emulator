package com.cometproject.server.network.messages.incoming.room.bots;

import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.bots.BotConfigMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class BotConfigMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int botId = msg.readInt();
        int skillId = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        BotEntity entity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByBotId(botId);

        if (entity == null) {
            return;
        }

        StringBuilder message = new StringBuilder();

        switch (skillId) {
            case 2:
                for (int i = 0; i < entity.getData().getMessages().length; i++) {
                    message.append(entity.getData().getMessages()[i]).append("\r");
                }

                message.append(";#;");
                message.append(entity.getData().isAutomaticChat() ? "true" : "false");
                message.append(";#;");
                message.append(entity.getData().getChatDelay());
                message.append(";#;");
                message.append(entity.getData().isRandomMessages() ? "true" : "false");
                break;

            case 5:
                message = new StringBuilder(entity.getUsername());
                break;

            case 9:
                message.append(entity.getData().getMotto());
                break;
        }

        client.send(new BotConfigMessageComposer(entity.getBotId(), skillId, message.toString()));
    }
}
