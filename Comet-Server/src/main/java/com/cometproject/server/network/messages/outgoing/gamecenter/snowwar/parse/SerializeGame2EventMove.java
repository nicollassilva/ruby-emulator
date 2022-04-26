package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.UserMove;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventMove {
    public static void parse(final IComposer msg, final UserMove evt) {
        msg.writeInt(evt.player.objectId);
        msg.writeInt(evt.x);
        msg.writeInt(evt.y);
    }

    public static void parse(final MessageWriter ClientMessage, final UserMove evt) {
        ComposerShit.add(evt.player.objectId, ClientMessage);
        ComposerShit.add(evt.x, ClientMessage);
        ComposerShit.add(evt.y, ClientMessage);
    }
}
