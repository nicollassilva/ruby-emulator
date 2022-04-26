package com.cometproject.server.network.messages.incoming.landing;

import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.landing.LTDCountdownMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LTDCountdownMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String text = msg.readString();

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        f.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));

        Date date = f.parse(text);

        int timeSpan = (int)(date.getTime() / 1000L - Comet.getTime());

        client.send(new LTDCountdownMessageComposer(text, timeSpan));
    }
}

