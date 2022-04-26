package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeArena;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2PlayerData;
import com.cometproject.server.protocol.messages.MessageComposer;

public class EnterArenaComposer  extends MessageComposer {

    private final SnowWarRoom arena;

    public EnterArenaComposer(final SnowWarRoom room) {
        this.arena = room;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
        msg.writeInt(arena.ArenaType.ArenaType);
        msg.writeInt(SnowWar.TEAMS.length);
        msg.writeInt(arena.players.size());
        for (final HumanGameObject Player : arena.players.values()) {
            SerializeGame2PlayerData.parse(msg, Player);
        }
        SerializeArena.parse(msg, arena);
    }

    @Override
    public short getId() {
        return 5021;
    }
}
