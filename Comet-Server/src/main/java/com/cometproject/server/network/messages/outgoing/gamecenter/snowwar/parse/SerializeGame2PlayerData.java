package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2PlayerData {
    public static void parse(final IComposer msg, final HumanGameObject Player) {
        msg.writeInt(Player.userId);
        msg.writeString(Player.userName);
        msg.writeString(Player.look);
        msg.writeString(Player.sex);
        msg.writeInt(Player.team);
    }
}
