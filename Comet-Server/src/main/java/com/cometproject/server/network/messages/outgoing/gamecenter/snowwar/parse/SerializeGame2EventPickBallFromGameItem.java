package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.gameevents.PickBallFromGameItem;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2EventPickBallFromGameItem {
    public static void parse(final IComposer msg, final PickBallFromGameItem evt) {
        msg.writeInt(evt.player.objectId);
        msg.writeInt(evt.gameItem.objectId);
    }

    public static void parse(final MessageWriter ClientMessage, final PickBallFromGameItem evt) {
        ComposerShit.add(evt.player.objectId, ClientMessage);
        ComposerShit.add(evt.gameItem.objectId, ClientMessage);
    }
}
