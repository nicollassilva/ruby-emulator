package com.cometproject.server.network.messages.outgoing.help.guides;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GuideSessionPartnerIsTypingMessageComposer extends MessageComposer {
    private final boolean isTyping;

    public GuideSessionPartnerIsTypingMessageComposer(boolean isTyping) {
        this.isTyping = isTyping;
    }

    @Override
    public short getId() {
        return Composers.GuideSessionPartnerIsTypingMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.isTyping);
    }
}
