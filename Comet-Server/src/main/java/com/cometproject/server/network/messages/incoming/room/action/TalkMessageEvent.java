package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.game.battlepass.types.BattlePassType;
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
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.MutedMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.RandomUtil;

import java.util.concurrent.TimeUnit;


public class TalkMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1, 1));
            return;
        }

        String message = msg.readString();

        int bubble = msg.readInt();

        final int timeMutedExpire = client.getPlayer().getData().getTimeMuted() - (int) Comet.getTime();
        final PlayerEntity playerEntity = client.getPlayer().getEntity();

        if (playerEntity == null || playerEntity.getRoom() == null || playerEntity.getRoom().getEntities() == null)
            return;

        if (!playerEntity.isVisible() && !playerEntity.getPlayer().isInvisible()) {
            return;
        }

        if (client.getPlayer().getData().getTimeMuted() != 0) {
            if (client.getPlayer().getData().getTimeMuted() > (int) Comet.getTime()) {
                client.getPlayer().getSession().send(new MutedMessageComposer(timeMutedExpire));
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
                filterResult.sendLogToStaffs(client, "<Room: " + playerEntity.getRoom().getData().getId() + ">");
                //client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.getPlayer().getEntity().increaseCountFilter(1);

                if (client.getPlayer().getEntity().getCountFilter() >= 3) {
                    final int timeMuted = (int) Comet.getTime() + 5 * 60;
                    PlayerDao.addTimeMute(client.getPlayer().getData().getId(), timeMuted);
                    client.getPlayer().getData().setTimeMuted(timeMuted);

                    client.sendQueue(new NotificationMessageComposer("filter", "Has sido muteado por el sistema de moderación automático por no respetar el filtro " + client.getPlayer().getEntity().getCountFilter() + " veces"));
                }

                // Logs in to client bubble
                for (final ISession player : NetworkManager.getInstance().getSessions().getSessions().values()) {
                    if (!player.getPlayer().getLogsClientStaff()) continue;

                    if (client.getPlayer().getEntity() != null) {
                        player.sendQueue(new NotificationMessageComposer("filter", "El usuario " + client.getPlayer().getData().getUsername() + " ha saltado el filtro diciendo la palabra " + filterResult.getMessage() + " tiene " + client.getPlayer().getEntity().getCountFilter() + " oportunidades de 3"));
                    }
                }
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getMessage();
            }

            filteredMessage = playerEntity.getRoom().getFilter().filter(playerEntity, filteredMessage);
        }

        if (playerEntity.onChat(filteredMessage)) {
            if (!UsingColourCode(message)) {
                if (message.startsWith("@")) {
                    final String[] splittedName = message.replace("@", "").split(" ");
                    final String finalName = splittedName[0];

                    final Session player = NetworkManager.getInstance().getSessions().getByPlayerUsername(finalName);

                    if (player == null)
                        return;

                    if (finalName.equals(client.getPlayer().getEntity().getUsername())) {
                        client.send(new WhisperMessageComposer(client.getPlayer().getData().getId(), Locale.getOrDefault("mention.himself", "No te puedes mencionar a ti mismo"), 34));
                        return;
                    }

                    if (!finalName.equals(client.getPlayer().getEntity().getUsername())) {
                        player.getPlayer().addMention(new PlayerMention(client.getPlayer().getData().getUsername(), message));
                    }

                    if (player.getPlayer().getEntity() == null) {
                        client.send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "No puedes mencionar a este usuario por que no se encuentra en ninguna sala en este momento", ChatEmotion.NONE, 1));
                        return;
                    }

                    final MentionType mentionSetting = player.getPlayer().getSettings().getMentionType();

                    if (mentionSetting == MentionType.FRIENDS && player.getPlayer().getMessenger().getFriendById(client.getPlayer().getEntity().getPlayerId()) != null || mentionSetting == MentionType.ALL) {
                        player.send(new WhisperMessageComposer(player.getPlayer().getEntity().getId(), "El usuario " + client.getPlayer().getData().getUsername() + " dice: " + "<b>" + message + "</b>", 1));
                        //player.send(new JavascriptCallbackMessageComposer(new MentionComposer(client.getPlayer().getData().getUsername(), message, client.getPlayer().getData().getFigure())));
                        final String name = String.format("@%s", finalName);
                        filteredMessage = filteredMessage.replace(String.format("@%s", finalName), String.format("%s", name));
                    } else {
                        if (mentionSetting == MentionType.FRIENDS) {
                            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getPlayerId(), Locale.getOrDefault("mention.notfriend", "You must be friends to mention a player!").replace("%s", finalName), 34));
                        } else if (mentionSetting == MentionType.NONE) {
                            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getPlayerId(), Locale.getOrDefault("mention.disabled", "This player is not acepting mentions!").replace("%s", finalName), 34));
                        } else if (player.getPlayer().getEntity().getRoom() == null) {
                            client.send(new WhisperMessageComposer(client.getPlayer().getData().getId(), Locale.getOrDefault("mention.notexist", "El usuario %s no existe o está desconectado")
                                    .replace("%s", finalName), 34));
                            return;
                        }
                    }
                }
            }

            if (message.startsWith("nb!love")) {
                final int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastNbLove());

                if (timeSinceLastUpdate >= 5) {
                    int love = RandomUtil.getRandomInt(0, 100);

                    final PlayerEntity userOne = client.getPlayer().getEntity();
                    final String[] splittedName = message.replace("nb!love", "").split(" ");
                    final String finalName = splittedName[1];
                    final Session s = NetworkManager.getInstance().getSessions().getByPlayerUsername(finalName);

                    if (s == null) {
                        client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Este usuario está desconectado", ChatEmotion.NONE, 1));
                        return;
                    }

                    if (s.getPlayer().getEntity() == null) {
                        client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Este usuario no está en la sala", ChatEmotion.NONE, 1));
                        return;
                    }

                    if (finalName.equals(userOne.getUsername())) {
                        return;
                    }

                    CometThreadManager.getInstance().executeSchedule(() -> {
                        userOne.getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), client.getPlayer().getEntity().getUsername() + " tiene " + "<b>" + love + "</b>" + "% de compatibilidad con " + finalName, ChatEmotion.NONE, 1));
                    }, 1, TimeUnit.SECONDS);

                    client.getPlayer().setLastNbLove((int) Comet.getTime());

                /*if(love == 100) {
                    CometThreadManager.getInstance().executeSchedule(() -> {
                    NetworkManager.getInstance().getSessions().broadcast(new TalkMessageComposer(client.getPlayer().getEntity().getId(), userOne.getUsername() + " y " + finalName + " han logrado un 100%, armen todos una fiesta ahora mismo por que se viene la boda.", ChatEmotion.NONE, 34));
                    }, 2, TimeUnit.SECONDS);
                } else if (love == 0) {
                    userOne.getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), client.getPlayer().getEntity().getUsername() + " no tiene ninguna chance con " + finalName + ", es mejor que se alejen antes que terminen odiandose", ChatEmotion.NONE, 34));
                }*/
                } else {
                    client.getPlayer().getSession().sendQueue(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Debes esperar 5 segundos para poder volver a utilizar el nb!love", ChatEmotion.NONE, 1));
                    return;
                }
            }

            switch (message) {
                case ":'(":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(660, 10));
                    break;

                case ":@":
                case ">:(":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(659, 10));
                    break;

                case ":(":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(658, 10));
                    break;

                case "8)":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(656, 10));
                    break;

                case "XP":
                case ";p":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(657, 10));
                    break;

                case ">.<":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(661, 10));
                    break;

                case "D:":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(662, 10));
                    break;

                case "D;":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(663, 10));
                    break;

                case "7.7":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(664, 10));
                    break;

                case "._.":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(665, 10));
                    break;

                case ":#":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(666, 10));
                    break;

                case ":$":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(667, 10));
                    break;

                case "poop":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(669, 10));
                    break;

                case "<3":
                case "|":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(670, 10));
                    break;

                case "|_|":
                case "|.|":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(679, 10));
                    break;

                case "0:)":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(674, 10));
                    break;

                case "(y)":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(671, 10));
                    break;

                case "xD":
                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(673, 10));
                    break;

                case "bbjoin":
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

            /*try {
                if (client.getPlayer().getData().getUsername().equals("Dan")) {
                    filteredMessage = TranslationComponent.translate("es", "pt", message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            if (client.getPlayer().getEntity().getPrivateChatItemId() != 0) {
                // broadcast message only to players in the tent.
                final RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(client.getPlayer().getEntity().getPrivateChatItemId());

                if (floorItem != null) {
                    ((PrivateChatFloorItem) floorItem).broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), bubble));
                }
            } else if (message.startsWith("@") && !UsingColourCode(message)) {
                client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "[TU MENSAJE] " + filteredMessage, bubble));
            } else {
                if(UsingColourCode(message)) {
                    filteredMessage = "<font color=\"%clrHr\">" + filteredMessage + "</font>";

                    if (message.contains("@red@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.RED.getColor());
                    } else if (message.contains("@blue@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.BLUE.getColor());
                    } else if (message.contains("@green@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.GREEN.getColor());
                    } else if (message.contains("@yellow@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.YELLOW.getColor());
                    } else if (message.contains("@orange@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.ORANGE.getColor());
                    } else if (message.contains("@pink@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.PINK.getColor());
                    } else if (message.contains("@purple@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.PURPLE.getColor());
                    } else if (message.contains("@gray@")) {
                        filteredMessage = filteredMessage.replace("%clrHr", ChatColors.GRAY.getColor());
                    }

                    filteredMessage = clearMessageColors(filteredMessage);
                }

                client.getPlayer().getEntity().getRoom().getEntities().broadcastChatMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), bubble), client.getPlayer().getEntity());
            }

            playerEntity.postChat(filteredMessage);
        }
    }

    public static String clearMessageColors(String message) {
        return message.replace("@red@", "")
                .replace("@blue@", "")
                .replace("@yellow@", "")
                .replace("@orange@", "")
                .replace("@green@", "")
                .replace("@gray@", "")
                .replace("@purple@", "")
                .replace("@pink@", "");
    }

    public static boolean UsingColourCode(String Message) {
        return Message.contains("@red") ||
                Message.contains("@blue@") ||
                Message.contains("@green@") ||
                Message.contains("@yellow@") ||
                Message.contains("@orange@") ||
                Message.contains("@pink@") ||
                Message.contains("@purple@") ||
                Message.contains("@gray@");
    }

    public static String filterMessage(String message) {
        if (message.contains("&#10º;")) {
            message = message.replace("&#10º;", "");
        }

        return message.replace((char) 13 + "", "").replace("<", "&lt;").replace("&#10º;", "");
    }
}
