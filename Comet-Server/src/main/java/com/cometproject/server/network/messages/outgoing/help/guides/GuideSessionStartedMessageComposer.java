package com.cometproject.server.network.messages.outgoing.help.guides;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GuideSessionStartedMessageComposer extends MessageComposer {
    private final int requesterId;
    private final String requesterUsername;
    private final String requesterLook;

    private final int guideId;
    private final String guideUsername;
    private final String guideLook;

    public GuideSessionStartedMessageComposer(int requesterId, String requesterUsername, String requesterLook, int guideId, String guideUsername, String guideLook) {
        this.requesterId = requesterId;
        this.guideId = guideId;
        this.requesterUsername = requesterUsername;
        this.requesterLook = requesterLook;
        this.guideUsername = guideUsername;
        this.guideLook = guideLook;
    }

    @Override
    public short getId() {
        return Composers.GuideSessionStartedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.requesterId);
        msg.writeString(this.requesterUsername);
        msg.writeString(this.requesterLook);

        msg.writeInt(this.guideId);
        msg.writeString(this.guideUsername);
        msg.writeString(this.guideLook);
    }
}
