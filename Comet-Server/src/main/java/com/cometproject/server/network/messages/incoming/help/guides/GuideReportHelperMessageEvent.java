package com.cometproject.server.network.messages.incoming.help.guides;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.types.components.types.ChatMessage;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.ArrayList;

public class GuideReportHelperMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String message = msg.readString();

        HelpRequest helpRequest = GuideManager.getInstance().getHelpRequestByCreator(client.getPlayer().getId());

        if(helpRequest == null) return;

        ModerationManager.getInstance().createTicket(client.getPlayer().getId(), message, 0, helpRequest.guideId, (int) Comet.getTime(), 0, new ArrayList<>());
    }
}
