package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.PlayerLeft;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventPlayerLeft {
    public static void parse(final IComposer msg, final PlayerLeft evt) {
        msg.writeInt(evt.player.objectId);
    }

    public static void parse(final MessageWriter ClientMessage, final PlayerLeft evt) {
        ComposerShit.add(evt.player.objectId, ClientMessage);
    }
}

