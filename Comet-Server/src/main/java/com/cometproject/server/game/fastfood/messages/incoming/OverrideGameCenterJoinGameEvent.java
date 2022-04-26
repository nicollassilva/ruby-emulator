package com.cometproject.server.game.fastfood.messages.incoming;

import com.cometproject.server.composers.gamecenter.GameStatusMessageComposer;
import com.cometproject.server.game.gamecenter.GameCenterInfo;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.snowwar.Game;
import com.cometproject.server.game.snowwar.GameManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class OverrideGameCenterJoinGameEvent implements Event {
    @Override
    public void handle(final Session cn, MessageEvent msg) throws Exception {
        int gameId = msg.readInt();
        Game game = GameManager.getGameById(gameId);

        if(game != null) {
            game.onPlayButton(gameId, cn.getPlayer());
        }
    }
}
