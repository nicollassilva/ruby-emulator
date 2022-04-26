package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class KissesMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int playerId = msg.readInt();

        final Session user = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);
        final Room room = client.getPlayer().getEntity().getRoom();
        final Position actorPosition = client.getPlayer().getEntity().getPosition();

        if(room == null) {
            return;
        }

        if(actorPosition == null) {
            return;
        }

        if(user == null || user.getPlayer() == null) return;

        if(actorPosition.distanceTo(user.getPlayer().getEntity().getPosition()) > 2.0) {
            client.send(new NotificationMessageComposer("action_distance_error", Locale.getOrDefault("kisses.far", "Debes estar delante de la persona para realizar la acci\u00f3n.")));
            return;
        }

        if (!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        if (client.getPlayer() == null || client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        if (playerId == client.getPlayer().getId()) {
            return;
        }

        if(CometSettings.useNewKissSystem) {
            if (client.getPlayer().antiSpam("SentKissesToUser", 0.5)) {
                client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("kisses.sent.too.fast", "Você está enviando beijos rápido demais.")));
                return;
            }

            if(client.getPlayer().getStats().getTotalKisses() < 1) return;

            client.getPlayer().getStats().decrementTotalKisses(1);
            client.getPlayer().getStats().incrementTotalKissesSent(1);
            client.getPlayer().getStats().save();

            user.getPlayer().getStats().incrementKissesReceived(1);
            user.getPlayer().getStats().save();

            client.getPlayer().getAchievements().progressAchievement(AchievementType.KISS_SENT, 1);
            user.getPlayer().getAchievements().progressAchievement(AchievementType.KISS_RECEIVED, 1);

            room.getEntities().broadcastMessage(
                    new TalkMessageComposer(
                            client.getPlayer().getEntity().getId(),
                            Locale.getOrDefault("user.kiss.action", "%user_one% beijou %user_two%.").replace("%user_one%", client.getPlayer().getData().getUsername()).replace("%user_two%", user.getPlayer().getData().getUsername()),
                            ChatEmotion.NONE, 42)
            );
        } else {
            if (client.getPlayer().getData().getKisses() < 1) return;

            client.getPlayer().getData().decreaseKisses(1);
            client.getPlayer().getData().save();
        }

        room.getEntities().broadcastMessage(new ActionMessageComposer(client.getPlayer().getEntity().getId(), 2));
        room.getEntities().broadcastMessage(new ActionMessageComposer(user.getPlayer().getEntity().getId(), 2));

        user.getPlayer().getEntity().lookTo(client.getPlayer().getEntity().getPosition().getX(), client.getPlayer().getEntity().getPosition().getY());
    }
}
