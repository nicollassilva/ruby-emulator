package com.cometproject.server.network.messages.outgoing.gamecenter;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class LuckyLosersComposer extends MessageComposer {
    private final int gameId;

    public LuckyLosersComposer(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(gameId);
        msg.writeInt(1);
        msg.writeString("Pietro");//Username
        msg.writeString("lg-275-1408.ca-4115-100-1408.hr-3163-42.ch-3934-1413-110.fa-3993-110.sh-3016-96.hd-180-1370");//Figure
        msg.writeString("M");//Gender .ToLower()
        msg.writeInt(1);//Rank
        msg.writeInt(5);//Score
    }

    @Override
    public short getId() {
        return Composers.GameCenterFeaturedPlayersMessageComposer;
    }
}
