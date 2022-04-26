package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.server.game.players.types.PlayerAvatarActions;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.GiveRespectMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class RespectUserMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int respect = msg.readInt();

        if (respect == client.getPlayer().getId()) {
            return;
        }

        if (!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        Session user = NetworkManager.getInstance().getSessions().getByPlayerId(respect);

        if (client.getPlayer() == null || client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        if (user == null || user.getPlayer() == null) {
            return;
        }

        if (client.getPlayer().getStats().getDailyRespects() < 1) {
            return;
        }

        user.getPlayer().getStats().incrementRespectPoints(1);
        user.getPlayer().getAchievements().progressAchievement(AchievementType.RESPECT_EARNED, 1);

        client.getPlayer().getStats().decrementDailyRespects(1);

        room.getEntities().broadcastMessage(new ActionMessageComposer(client.getPlayer().getEntity().getId(), PlayerAvatarActions.EXPRESSION_RESPECT.getValue()));
        room.getEntities().broadcastMessage(new GiveRespectMessageComposer(user.getPlayer().getId(), user.getPlayer().getStats().getRespectPoints()));

        client.getPlayer().getQuests().progressQuest(QuestType.SOCIAL_RESPECT);
        client.getPlayer().getAchievements().progressAchievement(AchievementType.RESPECT_GIVEN, 1);
    }
}
