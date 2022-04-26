package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

/**
 * Created by SpreedBlood on 2017-12-23.
 */
public class YouArePlayingGameComposer extends MessageComposer {

    private final boolean isPlaying;

    public YouArePlayingGameComposer(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.isPlaying);
    }

    @Override
    public short getId() {
        return 545;
    }
}
