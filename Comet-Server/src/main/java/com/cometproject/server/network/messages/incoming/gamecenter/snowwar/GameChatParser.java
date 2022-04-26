package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.data.SnowWarPlayerData;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.GameChatFromPlayerComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GameChatParser implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final SnowWarPlayerData snowPlayer = client.snowWarPlayerData;
        if (snowPlayer == null) {
            return;
        }

        final SnowWarRoom room = snowPlayer.currentSnowWar;
        if (room == null) {
            return;
        }

        final String say = msg.readString();

        room.broadcast(new TalkMessageComposer(-1, say, ChatEmotion.NONE, 1));
    }
}
