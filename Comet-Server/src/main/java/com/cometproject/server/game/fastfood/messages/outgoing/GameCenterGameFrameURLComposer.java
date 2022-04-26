package com.cometproject.server.game.fastfood.messages.outgoing;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GameCenterGameFrameURLComposer extends MessageComposer {

    private final String url;
    private final int gameId;

    public GameCenterGameFrameURLComposer(String url, int gameId) {
        this.url = url;
        this.gameId = gameId;
    }

    @Override
    public short getId() {
        return Composers.BaseJumpLoadGameURL;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.gameId);
        msg.writeString("1351418858673"); // not sure what this is, gonna leave it like that
        msg.writeString(this.url); // url of something
    }
}
