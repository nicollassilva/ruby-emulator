package com.cometproject.server.network.sessions.net;

import com.cometproject.networking.api.messages.IMessageHandler;
import com.cometproject.networking.api.sessions.INetSession;
import com.cometproject.networking.api.sessions.INetSessionFactory;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.sessions.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import com.cometproject.server.boot.Comet;


public class NetSessionFactory implements INetSessionFactory {
    private final SessionManager sessionManager;
    private final IMessageHandler messageHandler;

    public NetSessionFactory(SessionManager sessionManager, IMessageHandler messageHandler) {
        this.sessionManager = sessionManager;
        this.messageHandler = messageHandler;

    }

    @Override
    public INetSession createSession(ChannelHandlerContext channel) {
        if (Comet.isDebugging) {
            System.out.println("NetSessionFactory :: vai criar a session...");
        }

        if (!sessionManager.add(channel, null)) {
            if (Comet.isDebugging) {
                System.out.println("NetSessionFactory :: !add");
            }
            return null;
        }

        final Session session = channel.attr(SessionManager.SESSION_ATTR).get();
        final INetSession netSession = new NetSession(session, this.messageHandler);

        return netSession;
    }

    @Override
    public void disposeSession(INetSession session) {
        ((Session) session.getGameSession()).onDisconnect();

        this.sessionManager.remove(session.getChannel());
    }
}
