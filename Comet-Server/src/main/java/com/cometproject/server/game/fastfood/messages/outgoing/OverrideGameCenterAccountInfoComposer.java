package com.cometproject.server.game.fastfood.messages.outgoing;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class OverrideGameCenterAccountInfoComposer extends MessageComposer {

    private final int gameId;
    private final int gamesLeft;
    private final int other;

    public OverrideGameCenterAccountInfoComposer(int gameId, int gamesLeft, int other){
        this.gameId = gameId;
        this.gamesLeft = gamesLeft;
        this.other = other;
    }
    @Override
    public short getId() {
        return Composers.GameAccountStatusMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.gameId);
        msg.writeInt(this.gamesLeft);
        msg.writeInt(this.other);
    }
}
