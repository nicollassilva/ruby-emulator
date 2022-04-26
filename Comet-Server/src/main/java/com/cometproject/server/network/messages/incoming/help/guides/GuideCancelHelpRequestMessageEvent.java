package com.cometproject.server.network.messages.incoming.help.guides;

import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.help.guides.GuideSessionDetachedMessageComposer;
import com.cometproject.server.network.messages.outgoing.help.guides.GuideSessionEndedMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GuideCancelHelpRequestMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        HelpRequest helpRequest = GuideManager.getInstance().getHelpRequestByCreator(client.getPlayer().getId());

        if(helpRequest == null) return;


        helpRequest.composeMessage(new GuideSessionEndedMessageComposer(GuideSessionEndedMessageComposer.HELP_CASE_CLOSED));
        helpRequest.composeMessage(new GuideSessionDetachedMessageComposer());
        GuideManager.getInstance().closeHelpRequest(helpRequest.getPlayerId());
    }
}
