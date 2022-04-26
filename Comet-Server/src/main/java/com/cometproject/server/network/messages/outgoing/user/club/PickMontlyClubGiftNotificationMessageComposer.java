package com.cometproject.server.network.messages.outgoing.user.club;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class PickMontlyClubGiftNotificationMessageComposer extends MessageComposer {
    private final int count;

    public PickMontlyClubGiftNotificationMessageComposer(int count) {
        this.count = count;
    }

    @Override
    public short getId() {
        return Composers.PickMonthlyClubGiftNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.count);
    }
}
