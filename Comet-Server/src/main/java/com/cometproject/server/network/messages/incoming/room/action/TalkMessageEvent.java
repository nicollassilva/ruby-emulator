package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.game.players.data.types.MentionType;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.gamecenter.games.battleball.player.BattleBallPlayerQueue;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.players.types.PlayerMention;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.filter.FilterResult;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.PrivateChatFloorItem;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.logging.LogManager;
import com.cometproject.server.logging.entries.RoomChatLogEntry;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.incoming.room.action.utilities.ChatColors;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.MutedMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.MessageColorUtil;
import com.cometproject.server.utilities.RandomUtil;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TalkMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Você deve inserir o PIN antes de fazer qualquer ação."));
            client.send(new EmailVerificationWindowMessageComposer(1, 1));
            return;
        }

        String message = msg.readString();

        int bubble = msg.readInt();

        final PlayerEntity playerEntity = client.getPlayer().getEntity();

        if (playerEntity == null || playerEntity.getRoom() == null || playerEntity.getRoom().getEntities() == null)
            return;

        if (!playerEntity.isVisible() && !playerEntity.getPlayer().isInvisible()) {
            return;
        }

        if (client.getPlayer().getData().getTimeMuted() != 0) {
            if (client.getPlayer().getData().getTimeMuted() > (int) Comet.getTime()) {
                client.getPlayer().getSession().send(new MutedMessageComposer(client.getPlayer().getData().getTimeMuted() - (int) Comet.getTime()));
                return;
            }
        }

        bubble = ShoutMessageEvent.getBubble(client, bubble);

        if (client.getPlayer().getBubbleId() > 0) bubble = client.getPlayer().getBubbleId();

        if (bubble != 0) {
            final Integer bubbleMinRank = PermissionsManager.getInstance().getChatBubbles().get(bubble);

            if (bubbleMinRank == null) {
                bubble = 0;
            } else {
                if (client.getPlayer().getData().getRank() < bubbleMinRank) {
                    bubble = 0;
                }
            }
        }

        String filteredMessage = filterMessage(message);

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            final FilterResult filterResult = RoomManager.getInstance().getFilter().filter(filteredMessage);

            if (filterResult.isBlocked()) {
                filterResult.sendLogToStaffs(client, "<Quarto: " + playerEntity.getRoom().getData().getId() + ">");
                client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.getPlayer().getEntity().increaseCountFilter(1);

                if (client.getPlayer().getEntity().getCountFilter() >= 3) {
                    final int timeMuted = (int) Comet.getTime() + 5 * 60;
                    PlayerDao.addTimeMute(client.getPlayer().getData().getId(), timeMuted);
                    client.getPlayer().getData().setTimeMuted(timeMuted);

                    client.sendQueue(new NotificationMessageComposer("filter", "Foi silenciado pela moderação automática por não respeitar o filtro " + client.getPlayer().getEntity().getCountFilter() + " vezes"));
                }

                // Logs in to client bubble
                for (final ISession player : NetworkManager.getInstance().getSessions().getSessions().values()) {
                    if (!player.getPlayer().getLogsClientStaff()) continue;

                    if (client.getPlayer().getEntity() != null) {
                        player.sendQueue(new NotificationMessageComposer("filter", "O usuário " + client.getPlayer().getData().getUsername() + " ativou o filtro dizendo " + filterResult.getMessage() + ", tem " + client.getPlayer().getEntity().getCountFilter() + " oportunidades de 3"));
                    }
                }
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getMessage();
            }

            filteredMessage = playerEntity.getRoom().getFilter().filter(playerEntity, filteredMessage);
        }

        if (playerEntity.onChat(filteredMessage)) {
            if (messageHasNoColorsCode(message)) {
                if (message.startsWith("@")) {
                    final String[] splittedName = message.replace("@", "").split(" ");
                    final String finalName = splittedName[0];

                    final Session player = NetworkManager.getInstance().getSessions().getByPlayerUsername(finalName);

                    if (player == null)
                        return;

                    if (finalName.equals(client.getPlayer().getEntity().getUsername())) {
                        client.send(new WhisperMessageComposer(client.getPlayer().getData().getId(), Locale.getOrDefault("mention.himself", "Você não pode mencionar a si mesmo."), 34));
                        return;
                    }

                    if (!finalName.equals(client.getPlayer().getEntity().getUsername())) {
                        player.getPlayer().addMention(new PlayerMention(client.getPlayer().getData().getUsername(), message));
                    }

                    if (player.getPlayer().getEntity() == null) {
                        client.send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Você não pode mencionar este usuário porque ele não está em nenhum quarto no momento.", ChatEmotion.NONE, 1));
                        return;
                    }

                    final MentionType mentionSetting = player.getPlayer().getSettings().getMentionType();

                    if (mentionSetting == MentionType.FRIENDS && player.getPlayer().getMessenger().getFriendById(client.getPlayer().getEntity().getPlayerId()) != null || mentionSetting == MentionType.ALL) {
                        final Map<String, String> notificationParams = Maps.newHashMap();
                        notificationParams.put("display", "BUBBLE");
                        notificationParams.put("linkUrl", "event:navigator/goto/" + client.getPlayer().getEntity().getRoom().getId());
                        notificationParams.put("message", client.getPlayer().getData().getUsername() + " disse: " + message);

                        player.send(new NotificationMessageComposer("mention", notificationParams));
                        //player.send(new JavascriptCallbackMessageComposer(new MentionComposer(client.getPlayer().getData().getUsername(), message, client.getPlayer().getData().getFigure())));
                        final String name = String.format("@%s", finalName);
                        filteredMessage = filteredMessage.replace(String.format("@%s", finalName), String.format("%s", name));
                    } else {
                        if (mentionSetting == MentionType.FRIENDS) {
                            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getPlayerId(), Locale.getOrDefault("mention.notfriend", "Você deve ser amigo para enviar a menção!").replace("%s", finalName), 34));
                        } else if (mentionSetting == MentionType.NONE) {
                            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getPlayerId(), Locale.getOrDefault("mention.disabled", "Este usuário não aceita menções!").replace("%s", finalName), 34));
                        } else if (player.getPlayer().getEntity().getRoom() == null) {
                            client.send(new WhisperMessageComposer(client.getPlayer().getData().getId(), Locale.getOrDefault("mention.notexist", "O usuário %s não existe ou está offline.")
                                    .replace("%s", finalName), 34));
                            return;
                        }
                    }
                }
            }

            switch (message) {
                case "bbjoin":
                    if(!Comet.isDebugging) return;

                    BattleBallPlayerQueue.addPlayerInQueue(client);
                    break;
            }

            if (client.getPlayer().getChatMessageColour() != null) {
                filteredMessage = "@" + client.getPlayer().getChatMessageColour() + "@" + filteredMessage;

                if (filteredMessage.toLowerCase().startsWith("@" + client.getPlayer().getChatMessageColour() + "@:")) {
                    filteredMessage = filteredMessage.toLowerCase().replace("@" + client.getPlayer().getChatMessageColour() + "@:", ":");
                }
            }

            try {
                if (LogManager.ENABLED && !message.replace(" ", "").isEmpty())
                    LogManager.getInstance().getStore().getLogEntryContainer().put(new RoomChatLogEntry(playerEntity.getRoom().getId(), client.getPlayer().getId(), message));
            } catch (Exception ignored) {

            }

            if (client.getPlayer().getEntity().getPrivateChatItemId() != 0) {
                // broadcast message only to players in the tent.
                final RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(client.getPlayer().getEntity().getPrivateChatItemId());

                if (floorItem != null) {
                    ((PrivateChatFloorItem) floorItem).broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), bubble));
                }
            } else if (message.startsWith("@") && messageHasNoColorsCode(message)) {
                client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, bubble));
            } else {
                filteredMessage = MessageColorUtil.getInstance().getFilteredString(filteredMessage);

                client.getPlayer().getEntity().getRoom().getEntities().broadcastChatMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), bubble), client.getPlayer().getEntity());
            }

            playerEntity.postChat(filteredMessage);
        }
    }

    public static boolean messageHasNoColorsCode(String string) {
        return MessageColorUtil.getInstance().getFirstColorCodeOnString(string).isEmpty();
    }

    public static String filterMessage(String message) {
        if (message.contains("&#10º;")) {
            message = message.replace("&#10º;", "");
        }

        return message.replace((char) 13 + "", "").replace("<", "&lt;").replace("&#10º;", "");
    }
}
