package com.cometproject.server.network.messages.incoming.user.talenttrack;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.talenttrack.TalentTrackComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestTalentTrackEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if(!CometSettings.talentTrackEnabled) return;

        client.send(new TalentTrackComposer(client, TalentTrackType.valueOf(msg.readString().toUpperCase())));
    }
}
