package com.cometproject.server.network.messages.incoming.user.details;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementsListMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.PlayerSettingsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.UserObjectMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.BadgeInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.permissions.AllowancesMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class InfoRetrieveMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.sendQueue(new UserObjectMessageComposer(client.getPlayer()))
                .sendQueue(new AllowancesMessageComposer(client.getPlayer().getData().getRank()))
                .sendQueue(new BadgeInventoryMessageComposer(client.getPlayer().getInventory().getBadges()))
                .sendQueue(new AchievementsListMessageComposer(client.getPlayer().getAchievements()))
                .sendQueue(new PlayerSettingsMessageComposer(client.getPlayer().getSettings()));

        client.getPlayer().getMessenger().sendStatus(!client.getPlayer().getSettings().getHideOnline(), client.getPlayer().getSettings().allowedFollowToRoom());

//        if(client.getPlayer().getPermissions().getRank().alfaTool()){
//            final HelperSession helperSession = new HelperSession(client.getPlayer().getId(), true, true, true);
//            client.getPlayer().setHelperSession(helperSession);
//            GuideManager.getInstance().startPlayerDuty(client.getPlayer().getHelperSession());
//            client.send(new GuideToolsMessageComposer(true));
//        }

        client.flush();
    }
}
