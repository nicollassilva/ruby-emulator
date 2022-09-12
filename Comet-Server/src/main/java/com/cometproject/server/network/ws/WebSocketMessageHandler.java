package com.cometproject.server.network.ws;

import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.clients.ClientHandler;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebSocketMessageHandler extends SimpleChannelInboundHandler<MessageEvent> {
    private static Logger log = Logger.getLogger(ClientHandler.class.getName());

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (Comet.isDebugging) {
            System.out.println("WebSocketMessageHandler :: channelInactive()");
        }

        Session session = NetworkManager.getInstance().getSessions().get(ctx);
        if (session != null) {
            session.disconnect();
            NetworkManager.getInstance().getSessions().remove(session.getChannel());
        } else {
            SessionManagerAccessor.getInstance().getSessionManager().getPendingConnections().remove(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (Comet.isDebugging) {
            System.out.println("WebSocketMessageHandler :: userEventTriggered()");
        }

        if (evt instanceof ChannelInputShutdownEvent) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (Comet.isDebugging) {
            System.out.println("WebSocketMessageHandler :: exceptionCaught()");
            cause.printStackTrace();
        }

        if (ctx.channel().isActive()) {
            ctx.close();
        }

        if (cause instanceof IOException) return;

        log.error("Exception caught in WebSocketMessageHandler", cause);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageEvent event) {
        try {
            Session session = NetworkManager.getInstance().getSessions().get(ctx);

            if (session != null) {
                session.handleMessageEvent(event);
            }
        } catch (Exception e) {
            log.error("Error while receiving a message: ", e);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }
}