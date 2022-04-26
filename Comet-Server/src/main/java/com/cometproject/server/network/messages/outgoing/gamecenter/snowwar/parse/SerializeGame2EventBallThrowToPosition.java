package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.BallThrowToPosition;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventBallThrowToPosition {
    public static void parse(final IComposer msg, final BallThrowToPosition evt) {
        msg.writeInt(evt.attacker.objectId);
        msg.writeInt(evt.x);
        msg.writeInt(evt.y);
        msg.writeInt(evt.type);
    }

    public static void parse(final MessageWriter ClientMessage, final BallThrowToPosition evt) {
        ComposerShit.add(evt.attacker.objectId, ClientMessage);
        ComposerShit.add(evt.x, ClientMessage);
        ComposerShit.add(evt.y, ClientMessage);
        ComposerShit.add(evt.type, ClientMessage);
    }
}
