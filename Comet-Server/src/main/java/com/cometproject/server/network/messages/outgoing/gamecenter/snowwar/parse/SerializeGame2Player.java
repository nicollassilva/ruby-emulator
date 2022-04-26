package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.network.sessions.Session;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2Player {
    public static void parse(final IComposer msg, final Session cn) {
        msg.writeInt(cn.getPlayer().getData().getId());
        msg.writeString(cn.getPlayer().getData().getUsername());
        msg.writeString(cn.getPlayer().getData().getFigure());
        msg.writeString(cn.getPlayer().getData().getGender().toUpperCase());
        msg.writeInt(cn.snowWarPlayerData.humanObject.team);
        msg.writeInt(cn.getPlayer().getStats().getLevel());
        msg.writeInt(cn.snowWarPlayerData.getScore());
        msg.writeInt(cn.snowWarPlayerData.PointsNeed);
    }
}
