package com.cometproject.server.game.fastfood.messages.incoming;

import com.cometproject.server.composers.gamecenter.GameStatusMessageComposer;
import com.cometproject.server.game.fastfood.messages.outgoing.OverrideGameCenterAccountInfoComposer;
import com.cometproject.server.game.gamecenter.GameCenterInfo;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.snowwar.Game;
import com.cometproject.server.game.snowwar.GameManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class OverrideGameCenterRequestGameStatusEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int gameId = msg.readInt();
        final int status = msg.readInt();

        client.send(new GameStatusMessageComposer(gameId, status));
    }
}
