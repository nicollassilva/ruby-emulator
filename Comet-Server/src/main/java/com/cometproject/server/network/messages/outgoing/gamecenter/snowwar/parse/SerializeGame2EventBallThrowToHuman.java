package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.BallThrowToHuman;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventBallThrowToHuman {
    public static void parse(final IComposer msg, final BallThrowToHuman evt) {
        msg.writeInt(evt.attacker.objectId);
        msg.writeInt(evt.victim.objectId);
        msg.writeInt(evt.type);
    }

    public static void parse(final MessageWriter ClientMessage, final BallThrowToHuman evt) {
        ComposerShit.add(evt.attacker.objectId, ClientMessage);
        ComposerShit.add(evt.victim.objectId, ClientMessage);
        ComposerShit.add(evt.type, ClientMessage);
    }
}
