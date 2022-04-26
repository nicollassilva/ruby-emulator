package com.cometproject.server.network.messages.outgoing.landing;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class BonusBagMessageComposer extends MessageComposer {

    private final ISession s;
    private String reward = "";
    private final int spriteId;
    private final int amount;
    private final int flush;

    public BonusBagMessageComposer(String reward, int spriteId, int amount, ISession s) {
        this.reward = CometSettings.bonusRewardName;
        this.spriteId = CometSettings.bonusRewardItemId;
        this.amount = CometSettings.bonusHours;
        this.flush = s.getPlayer().getData().getBonusPoints() >= amount ? amount : amount - s.getPlayer().getData().getBonusPoints();
        this.s = s;
    }


    @Override
    public short getId() {
        return Composers.BonusRareMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.reward);
        msg.writeInt(this.spriteId);
        msg.writeInt(this.amount);
        msg.writeInt(this.flush);
    }
}
