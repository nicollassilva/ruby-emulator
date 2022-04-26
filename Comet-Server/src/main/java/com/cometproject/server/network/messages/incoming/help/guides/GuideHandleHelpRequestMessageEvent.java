package com.cometproject.server.network.messages.incoming.help.guides;

import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.help.guides.GuideSessionDetachedMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GuideHandleHelpRequestMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final HelpRequest helpRequest = GuideManager.getInstance().getHelpRequestByActiveGuide(client.getPlayer().getId());

        if(helpRequest.getGuideSession() == null) {
            client.send(new GuideSessionDetachedMessageComposer());
        }

        boolean accepted = msg.readBoolean();

        if(accepted) {
            helpRequest.accept();
        } else {
            helpRequest.decline(client.getPlayer().getId());
            client.send(new GuideSessionDetachedMessageComposer());
        }
    }
}
