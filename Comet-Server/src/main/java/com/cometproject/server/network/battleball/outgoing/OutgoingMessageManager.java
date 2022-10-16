package com.cometproject.server.network.battleball.outgoing;

import com.cometproject.server.network.battleball.outgoing.battleball.BattleBallCounterMessage;
import com.cometproject.server.network.battleball.outgoing.battleball.BattleBallEndMessage;
import com.cometproject.server.network.battleball.outgoing.battleball.BattleBallJoinQueueMessage;
import com.cometproject.server.network.battleball.outgoing.battleball.BattleBallStartedMessage;
import com.cometproject.server.network.battleball.outgoing.events.OpenEventAlertMessage;
import com.cometproject.server.network.battleball.outgoing.handshake.SSOVerifiedMessage;
import com.cometproject.server.network.battleball.outgoing.room.OpenBuildToolMessage;
import com.cometproject.server.network.battleball.outgoing.traxmachine.CloseTraxMachineWindowMessage;
import com.cometproject.server.network.battleball.outgoing.traxmachine.OpenTraxMachineWindowMessage;
import com.cometproject.server.network.battleball.outgoing.youtube.CloseYoutubeWindowMessage;
import com.cometproject.server.network.battleball.outgoing.youtube.OpenYoutubeWindowMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class OutgoingMessageManager {
    private final HashMap<Integer, Class<? extends OutgoingMessage>> messages;
    public static OutgoingMessageManager outgoingMessageManagerInstance;

    public OutgoingMessageManager() {
        this.messages = new HashMap<>();

        this.registerMessages();
    }

    public static OutgoingMessageManager getInstance() {
        if(outgoingMessageManagerInstance == null) {
            outgoingMessageManagerInstance = new OutgoingMessageManager();
        }

        return outgoingMessageManagerInstance;
    }

    public HashMap<Integer, Class<? extends OutgoingMessage>> getMessages() {
        return this.messages;
    }

    public void registerMessages() {
        this.messages.put(Outgoing.SSOVerifiedMessage, SSOVerifiedMessage.class);
        this.messages.put(Outgoing.OpenBuildToolMessage, OpenBuildToolMessage.class);
        this.messages.put(Outgoing.BattleBallCounterMessage, BattleBallCounterMessage.class);
        this.messages.put(Outgoing.BattleBallStartedMessage, BattleBallStartedMessage.class);
        this.messages.put(Outgoing.BattleBallEndMessage, BattleBallEndMessage.class);
        this.messages.put(Outgoing.BattleBallJoinQueueMessage, BattleBallJoinQueueMessage.class);
        this.messages.put(Outgoing.OpenEventAlertMessage, OpenEventAlertMessage.class);
        this.messages.put(Outgoing.OpenYoutubeWindowMessage, OpenYoutubeWindowMessage.class);
        this.messages.put(Outgoing.CloseYoutubeWindowMessage, CloseYoutubeWindowMessage.class);
        this.messages.put(Outgoing.OpenTraxMachineWindowMessage, OpenTraxMachineWindowMessage.class);
        this.messages.put(Outgoing.CloseTraxMachineWindowMessage, CloseTraxMachineWindowMessage.class);
    }

    public OutgoingMessage getMessageInstance(int outgoingId) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(!this.getMessages().containsKey(outgoingId))
            return null;

        return this.getMessages().get(outgoingId).getDeclaredConstructor().newInstance();
    }

}
