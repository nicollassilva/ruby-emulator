package com.cometproject.server.network.messages.outgoing.notification;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class WiredAlertMessageComposer extends MessageComposer {
    private final String alert;

    public WiredAlertMessageComposer(String alert) {
        this.alert = alert;
    }

    public short getId() {
        return Composers.UpdateFailedMessageComposer;
    }

    public void compose(IComposer msg) {
        msg.writeString(this.alert);
    }
}