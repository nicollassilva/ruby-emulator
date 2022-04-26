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
        msg.writeString("iNicollas");//Username
        msg.writeString("wa-9001123-68.ea-1406-63.ch-265-93.sh-905-62.lg-720-1418.hd-180-2.ha-4201-0.hr-990000418-45-31");//Figure
        msg.writeString("M");//Gender .ToLower()
        msg.writeInt(1);//Rank
        msg.writeInt(5);//Score
    }

    @Override
    public short getId() {
        return Composers.GameCenterFeaturedPlayersMessageComposer;
    }
}
