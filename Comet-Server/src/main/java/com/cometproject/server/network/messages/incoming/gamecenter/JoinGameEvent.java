
package com.cometproject.server.network.messages.incoming.gamecenter;

import com.cometproject.server.composers.gamecenter.GameStatusMessageComposer;
import com.cometproject.server.composers.gamecenter.LoadGameMessageComposer;
import com.cometproject.server.game.fastfood.FastFoodGame;
import com.cometproject.server.game.gamecenter.GameCenterInfo;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.gamecenter.games.battleball.player.BattleBallPlayerQueue;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.types.components.games.battleball.BattleBallGame;
import com.cometproject.server.game.snowwar.SnowPlayerQueue;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.UUID;

public class JoinGameEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int gameId = msg.readInt();
        //client.getPlayer().sendBubble("", "GameID: " + gameId);
        GameCenterInfo gameCenterInfo = GameCenterManager.getInstance().getGameById(gameId);

        switch (gameId) {
            case 1: {
                FastFoodGame.onPlayButton(client.getPlayer());
                break;
            }
            case 2: {
                SnowPlayerQueue.addPlayerInQueue(client);
                break;
            }
            case 3: {
                client.send(new RoomForwardMessageComposer(gameCenterInfo.getGameRoomId()));
                break;
            }
            case 4: {
                client.send(new RoomForwardMessageComposer(gameCenterInfo.getGameRoomId()));
                break;
            }
            case 5: {
                client.send(new RoomForwardMessageComposer(gameCenterInfo.getGameRoomId()));
                break;
            }
            case 6: {
                client.send(new RoomForwardMessageComposer(gameCenterInfo.getGameRoomId()));
                break;
            }
            case 7: {
                client.send(new RoomForwardMessageComposer(gameCenterInfo.getGameRoomId()));
                break;
            }
            case 8: {
                client.send(new RoomForwardMessageComposer(gameCenterInfo.getGameRoomId()));
                break;
            }
        }
        client.send(new GameStatusMessageComposer(gameId, 0));
    }
}