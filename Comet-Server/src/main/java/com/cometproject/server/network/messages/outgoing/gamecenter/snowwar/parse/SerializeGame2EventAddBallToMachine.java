package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.AddBallToMachine;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventAddBallToMachine {
    public static void parse(final IComposer msg, final AddBallToMachine evt) {
        msg.writeInt(evt.gameItem.objectId);
    }

    public static void parse(final MessageWriter ClientMessage, final AddBallToMachine evt) {
        ComposerShit.add(evt.gameItem.objectId, ClientMessage);
    }
}
