package com.cometproject.server.network.messages.incoming.room.bots;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.bots.BotMode;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.filter.FilterResult;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.utilities.RandomUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;


public class ModifyBotMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().antiSpam(this.getClass().getName(), 0.5)) {
            return;
        }

        final PlayerEntity entity = client.getPlayer().getEntity();

        if (entity == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        final int botId = msg.readInt();
        final int action = msg.readInt();
        final String data = msg.readString();

        BotEntity botEntity = room.getEntities().getEntityByBotId(botId);

        if (botEntity.getData() == null) {
            return;
        }

        switch (action) {
            case 1:
                String figure = entity.getFigure();
                String gender = entity.getGender();

                botEntity.getData().setFigure(figure);
                botEntity.getData().setGender(gender);

                room.getEntities().broadcastMessage(new UpdateInfoMessageComposer(botEntity));
                break;

            case 2:
                String[] data1 = data.split(";#;");

                final List<String> messages = Arrays.asList(data1[0].split("\r"));

                final boolean automaticChat = Boolean.parseBoolean(data1[data1.length - 3]);
                final boolean randomChat = Boolean.parseBoolean(data1[data1.length - 1]);
                int speakingInterval = Integer.parseInt(data1[data1.length - 2]);

                if(speakingInterval < CometSettings.minimumBotMessagesDelay) {
                    speakingInterval = CometSettings.minimumBotMessagesDelay;
                    client.sendQueue(new NotificationMessageComposer("generic", Locale.getOrDefault("bot.alert.minimumBotMessagesDelay", "O delay das mensagens foi retornado para o valor padrão.")));
                }

                for (String message : messages) {
                    FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

                    if (filterResult.isBlocked()) {
                        filterResult.sendLogToStaffs(client, "<ModifyBot>");
                        client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                        return;
                    }
//                    else if (filterResult.wasModified()) {
//                        messages.remove(message);
//                        messages.add(filterResult.getMessage());
//                    }
                }

                botEntity.getData().setMessages(messages.toArray(new String[0]));
                botEntity.getData().setRandomMessages(randomChat);
                botEntity.getData().setChatDelay(speakingInterval);
                botEntity.getData().setAutomaticChat(automaticChat);

                botEntity.resetCycleCount();
                break;

            case 3:
                // Relax
                switch (botEntity.getData().getMode()) {
                    case DEFAULT:
                        botEntity.getData().setMode(BotMode.RELAXED);
                        break;

                    case RELAXED:
                        botEntity.getData().setMode(BotMode.DEFAULT);
                        break;
                }

                botEntity.getData().save();
                break;

            case 4:
                // Dance
                final int danceId = botEntity.getDanceId() == 0 ? RandomUtil.getRandomInt(1, 4) : 0;
                botEntity.setDanceId(danceId);

                room.getEntities().broadcastMessage(new DanceMessageComposer(botEntity.getId(), danceId));
                break;

            case 5:
                // Change name
                final String botName = room.getBots().getAvailableName(data);

                FilterResult filterResult = RoomManager.getInstance().getFilter().filter(botName);

                if(botName.length() == 0 || botName.length() > CometSettings.minimumBotNameLength) {
                    client.sendQueue(new NotificationMessageComposer("generic", Locale.getOrDefault("bot.alert.invalidName", "Preencha um nome válido")));
                    return;
                }

                if (filterResult.isBlocked()) {
                    filterResult.sendLogToStaffs(client, "<ModifyBot>");
                    //client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                    client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                    return;
                }

                room.getBots().changeBotName(botEntity.getUsername(), botName);

                botEntity.getData().setUsername(botName);

                room.getEntities().broadcastMessage(new AvatarsMessageComposer(botEntity));
                break;
        }

        botEntity.getData().save();
    }
}
