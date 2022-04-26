package com.cometproject.server.network.messages.outgoing.help.guides;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GuideSessionEndedMessageComposer extends MessageComposer {
    public static final int SOMETHING_WRONG = 0;
    public static final int HELP_CASE_CLOSED = 1;

    private final int closeCode;

    public GuideSessionEndedMessageComposer(int closeCode) {
        this.closeCode = closeCode;
    }

    @Override
    public short getId() {
        return Composers.GuideSessionEndedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.closeCode);
    }
}
