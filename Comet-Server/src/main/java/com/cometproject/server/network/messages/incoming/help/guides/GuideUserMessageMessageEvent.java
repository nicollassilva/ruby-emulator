package com.cometproject.server.network.messages.incoming.help.guides;

import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.help.guides.GuideSessionMessageMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GuideUserMessageMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String message = msg.readString();
        HelpRequest helpRequest = GuideManager.getInstance().getHelpRequestByParticipant(client.getPlayer().getId());

        if(helpRequest == null) return;

        helpRequest.composeMessage(new GuideSessionMessageMessageComposer(client.getPlayer().getId(), message));
    }
}
