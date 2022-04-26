package com.cometproject.server.network.flash_external_interface_protocol.incoming.common;

import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.common.UpdateCreditsComposer;

public class RequestCreditsEvent extends IncomingExternalInterfaceMessage<RequestCreditsEvent.JSONRequestCreditsEvent> {

    public RequestCreditsEvent() {
        super(JSONRequestCreditsEvent.class);
    }

    @Override
    public void handle(Session client, JSONRequestCreditsEvent message) {
        client.send(client.getPlayer().composeCreditBalance());
        UpdateCreditsComposer creditsComposer = new UpdateCreditsComposer(client.getPlayer().getData().getCredits());
        client.send(new JavascriptCallbackMessageComposer(creditsComposer));
    }
    static class JSONRequestCreditsEvent {
        boolean idk;
    }
}
