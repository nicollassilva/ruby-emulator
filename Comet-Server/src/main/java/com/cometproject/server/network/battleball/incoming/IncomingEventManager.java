package com.cometproject.server.network.battleball.incoming;

import com.cometproject.server.network.battleball.incoming.battleball.BattleBallLeaveEvent;
import com.cometproject.server.network.battleball.incoming.battlepass.OpenBattlePassEvent;
import com.cometproject.server.network.battleball.incoming.events.JoinRoomEventEvent;
import com.cometproject.server.network.battleball.incoming.handshake.SSOLoginEvent;
import com.cometproject.server.network.battleball.incoming.queues.JoinQueueSnowstormEvent;
import com.cometproject.server.network.battleball.incoming.room.CloseBuildToolEvent;
import com.cometproject.server.network.battleball.incoming.room.SetBuildToolEvent;
import com.cometproject.server.network.battleball.incoming.traxmachine.SaveSongEvent;

import java.util.HashMap;

public class IncomingEventManager {

    private final HashMap<Integer, Class<? extends IncomingEvent>> events;

    public IncomingEventManager() {
        this.events = new HashMap<>();
        this.registerUserEvent();
    }

    public HashMap<Integer, Class<? extends IncomingEvent>> getEvents() {
        return this.events;
    }

    public void registerUserEvent() {

        events.put(Incoming.SSOLoginEvent, SSOLoginEvent.class);
        events.put(Incoming.SetBuildToolEvent, SetBuildToolEvent.class);
        events.put(Incoming.CloseBuildToolEvent, CloseBuildToolEvent.class);
        events.put(Incoming.JoinQueueSnowstormEvent, JoinQueueSnowstormEvent.class);
        events.put(Incoming.OpenBattlePassEvent, OpenBattlePassEvent.class);
        events.put(Incoming.BattleBallLeaveEvent, BattleBallLeaveEvent.class);
        events.put(Incoming.JoinRoomEventEvent, JoinRoomEventEvent.class);
        events.put(Incoming.SaveSongEvent, SaveSongEvent.class);

    }

}
