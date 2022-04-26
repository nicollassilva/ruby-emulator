package com.cometproject.server.network.messages.outgoing.user.details;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class AvailabilityStatusMessageComposer extends MessageComposer {
    private final boolean isOpen;
    private final boolean isShuttingDown;
    private final boolean isAuthenticHabbo;

    public AvailabilityStatusMessageComposer(boolean isOpen, boolean isShuttingDown, boolean isAuthenticHabbo) {
        this.isOpen = isOpen;
        this.isShuttingDown = isShuttingDown;
        this.isAuthenticHabbo = isAuthenticHabbo;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.isOpen);//isOpen
        msg.writeBoolean(this.isShuttingDown);//onShutdown
        msg.writeBoolean(this.isAuthenticHabbo);//isAuthenticHabbo
    }
        @Override
        public short getId() {
            return Composers.AvailabilityStatusMessageComposer;
        }
}
