package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.CreateSnowBall;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventCreateSnowBall {
    public static void parse(final IComposer msg, final CreateSnowBall evt) {
        msg.writeInt(evt.ball.objectId);
        msg.writeInt(evt.player.objectId);
        msg.writeInt(evt.x);
        msg.writeInt(evt.y);
        msg.writeInt(evt.type);
    }

    public static void parse(final MessageWriter ClientMessage, final CreateSnowBall evt) {
        ComposerShit.add(evt.ball.objectId, ClientMessage);
        ComposerShit.add(evt.player.objectId, ClientMessage);
        ComposerShit.add(evt.x, ClientMessage);
        ComposerShit.add(evt.y, ClientMessage);
        ComposerShit.add(evt.type, ClientMessage);
    }
}