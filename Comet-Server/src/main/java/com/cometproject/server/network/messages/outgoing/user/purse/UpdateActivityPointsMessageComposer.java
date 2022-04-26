package com.cometproject.server.network.messages.outgoing.user.purse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class UpdateActivityPointsMessageComposer extends MessageComposer {

    private final int activityPoints;
    private final int change;

    public UpdateActivityPointsMessageComposer(int activityPoints, int change) {
        this.activityPoints = activityPoints;
        this.change = change;
    }

    @Override
    public short getId() {
        return Composers.HabboActivityPointNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.activityPoints);
        msg.writeInt(this.change);
        msg.writeInt(0);
    }
}
