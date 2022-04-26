package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.Collection;

public class StageStillLoadingComposer extends MessageComposer {

    private final Collection<HumanGameObject> playersLoaded;

    public StageStillLoadingComposer(Collection<HumanGameObject> playersLoaded) {
        this.playersLoaded = playersLoaded;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0); // notused
        msg.writeInt(playersLoaded.size());
        for (final HumanGameObject player : playersLoaded) {
            msg.writeInt(player.userId);
        }
    }

    @Override
    public short getId() {
        return 5026;
    }
}
