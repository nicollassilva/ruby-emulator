package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2PlayerStatsData {
    public static void parse(final IComposer msg, final HumanGameObject Player) {
        msg.writeInt(Player.score);
        msg.writeInt(Player.kills);
        msg.writeInt(0); // not used
        msg.writeInt(Player.hits);
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
    }
}
