package com.cometproject.server.network.messages.incoming.handshake;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.handshake.UniqueIDMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.crypto.utils.HexUtils;
import com.cometproject.server.protocol.messages.MessageEvent;


public class UniqueIdMessageEvent implements Event {
    private static final int HASH_LENGTH = 64;

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String storedMachineId = msg.readString();
        String clientFingerprint = msg.readString();
        String capabilities = msg.readString();

        if (storedMachineId.startsWith("~") || storedMachineId.length() != HASH_LENGTH) {
            storedMachineId = HexUtils.getRandom(HASH_LENGTH);
            client.send(new UniqueIDMessageComposer(storedMachineId));
        }

        client.setUniqueId(storedMachineId);
        //client.getPlayer().getData().setMachineId(storedMachineId);
        //client.getPlayer().getData().save();
    }
}
