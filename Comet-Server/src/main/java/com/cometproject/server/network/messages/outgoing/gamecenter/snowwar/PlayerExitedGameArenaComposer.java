package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.protocol.messages.MessageComposer;

public class PlayerExitedGameArenaComposer extends MessageComposer {

    private final HumanGameObject player;

    public PlayerExitedGameArenaComposer(HumanGameObject player) {
        this.player = player;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(player.userId);
        msg.writeInt(player.objectId);
    }

    @Override
    public short getId() {
        return 0;
    }
}
