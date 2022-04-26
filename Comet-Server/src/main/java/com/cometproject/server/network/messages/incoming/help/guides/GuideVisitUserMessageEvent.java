package com.cometproject.server.network.messages.incoming.help.guides;

import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.help.guides.GuideSessionRequesterRoomMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GuideVisitUserMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final HelpRequest helpRequest = GuideManager.getInstance().getHelpRequestByActiveGuide(client.getPlayer().getId());

        if(helpRequest == null) return;

        Room room = helpRequest.getPlayerSession().getPlayer().getEntity().getRoom();

        if(room == null) return;

        client.send(new GuideSessionRequesterRoomMessageComposer(room.getId()));
    }
}
