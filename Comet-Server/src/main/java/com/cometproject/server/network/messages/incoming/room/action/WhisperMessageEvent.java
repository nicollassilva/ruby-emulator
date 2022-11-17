package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.filter.FilterResult;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.logging.LogManager;
import com.cometproject.server.logging.entries.RoomChatLogEntry;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.MutedMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class WhisperMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if(client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1,1));
            return;
        }

        String text = msg.readString();
        int bubbleId = msg.readInt();

        String user = text.split(" ")[0];
        String message = text.substring(user.length() + 1);

        final int timeMutedExpire = client.getPlayer().getData().getTimeMuted() - (int) Comet.getTime();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        if (!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        if (client.getPlayer().getData().getTimeMuted() != 0) {
            if (client.getPlayer().getData().getTimeMuted() > (int) Comet.getTime()) {
                client.getPlayer().getSession().send(new MutedMessageComposer(timeMutedExpire));
                return;
            }
        }

        bubbleId = ShoutMessageEvent.getBubble(client, bubbleId);
        final Room room = client.getPlayer().getEntity().getRoom();
        PlayerEntity userTo = room.getEntities().getPlayerEntityByName(user);
        final PlayerEntity playerEntity = client.getPlayer().getEntity();

        if (user.equalsIgnoreCase("grupo")) {
            for (String username : client.getPlayer().getGroupWhispers()) {
                Player whisperedPlayer = client.getPlayer().getPlayerByGroupChat(username);
                if (whisperedPlayer == null || whisperedPlayer.getEntity() == null || whisperedPlayer.ignores(client.getPlayer().getId())) continue;
                String filteredMessage2 = TalkMessageEvent.filterMessage(message);
                if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
                    FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);
                    if (filterResult.isBlocked()) {
                        filterResult.sendLogToStaffs(client, "Sussurro: " + playerEntity.getRoom().getData().getId() + "");
                        //client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                        client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                        client.getLogger().info(("O filtro detectou uma palavra proibida na mensagem: \"" + message + "\""));
                        continue;
                    }
                    if (filterResult.wasModified()) {
                        filteredMessage2 = filterResult.getMessage();
                    }
                    filteredMessage2 = playerEntity.getRoom().getFilter().filter(playerEntity, filteredMessage2);
                }
                whisperedPlayer.getSession().send(new WhisperMessageComposer(whisperedPlayer.getId(), "<b>" + client.getPlayer().getData().getUsername() + "</b>, disse ao grupo: " + filteredMessage2, 1));
            }
            client.send(new WhisperMessageComposer(playerEntity.getId(), "<b>" + client.getPlayer().getData().getUsername() + "</b>, disse ao grupo: " + message, bubbleId));
            playerEntity.postChat(message);
            return;
        }

        if (userTo == null || user.equals(client.getPlayer().getData().getUsername()))
            return;

        if (!userTo.getPlayer().getEntity().isVisible())
            return;

        if(userTo.getPlayer().getSettings().disableWhisper() && !(client.getPlayer().getData().getRank() > Integer.parseInt(Locale.getOrDefault("whisper.minrank.force", "5")))) {
            client.send(new WhisperMessageComposer(client.getPlayer().getId(), Locale.getOrDefault("whisper.disabled", "Ops! O usuário que você tentou enviar mensagem possui sussurros desativados!")));
            return;
        }

        if (client.getPlayer().getChatMessageColour() != null) {
            message = "@" + client.getPlayer().getChatMessageColour() + "@" + message;

            if (message.toLowerCase().startsWith("@" + client.getPlayer().getChatMessageColour() + "@:")) {
                message = message.toLowerCase().replace("@" + client.getPlayer().getChatMessageColour() + "@:", ":");
            }
        }

        String filteredMessage = TalkMessageEvent.filterMessage(message);

        if (filteredMessage == null) {
            return;
        }

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                filterResult.sendLogToStaffs(client, "<Sussurro: " + playerEntity.getRoom().getData().getId() + ">");
                //client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.getLogger().info("O filtro detectou uma palavra proibida na mensagem: \"" + message + "\"");
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getMessage();
            }

            filteredMessage = playerEntity.getRoom().getFilter().filter(playerEntity, filteredMessage);
        }

        if (playerEntity.onChat(filteredMessage)) {
            try {
                if (LogManager.ENABLED)
                    LogManager.getInstance().getStore().getLogEntryContainer().put(new RoomChatLogEntry(room.getId(), client.getPlayer().getId(), Locale.getOrDefault("game.logging.whisper", "<Sussurro para %username%>").replace("%username%", user) + " " + message));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!userTo.getPlayer().ignores(client.getPlayer().getId()) && !userTo.getPlayer().usersMuted())
                userTo.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), filteredMessage));

            for (PlayerEntity entity : playerEntity.getRoom().getEntities().getWhisperSeers()) {
                if (entity.getPlayer().getId() != client.getPlayer().getId() && !user.equals(entity.getUsername()))
                    entity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), Locale.getOrDefault("game.whispering", "Sussurro para %username%: %message%").replace("%username%", user).replace("%message%", filteredMessage)));
            }
        }

        client.send(new WhisperMessageComposer(playerEntity.getId(), filteredMessage, bubbleId));
        playerEntity.postChat(message);
    }
}