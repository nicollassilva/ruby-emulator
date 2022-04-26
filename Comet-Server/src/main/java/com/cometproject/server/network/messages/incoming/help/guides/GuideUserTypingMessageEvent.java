package com.cometproject.server.network.messages.incoming.help.guides;

import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.help.guides.GuideSessionPartnerIsTypingMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GuideUserTypingMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        HelpRequest helpRequest = GuideManager.getInstance().getHelpRequestByParticipant(client.getPlayer().getId());

        if(helpRequest == null) return;

        boolean typing = msg.readBoolean();

        if(helpRequest.getPlayerSession().getPlayer().getId() == client.getPlayer().getId()) {
            helpRequest.getGuideSession().send(new GuideSessionPartnerIsTypingMessageComposer(typing));
        } else {
            helpRequest.getPlayerSession().send(new GuideSessionPartnerIsTypingMessageComposer(typing));
        }
    }
}
