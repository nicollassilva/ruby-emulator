package com.cometproject.server.network.ws;

import com.cometproject.networking.api.sessions.INetSession;
import com.cometproject.networking.api.sessions.INetSessionFactory;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.misc.PingMessageComposer;
import com.cometproject.server.protocol.messages.MessageEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;

import java.io.IOException;

public class WsMessageHandler extends SimpleChannelInboundHandler<MessageEvent> {

    private static final Logger LOGGER = Logger.getLogger(WsMessageHandler.class);

    private static final AttributeKey<INetSession> ATTR_SESSION = AttributeKey.newInstance("WsNetSession");

    private static WsMessageHandler wsMessageHandlerInstance;
    private final INetSessionFactory sessionFactory;
    
    public WsMessageHandler(INetSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static WsMessageHandler getInstance(INetSessionFactory netSessionFactory) {
        if (wsMessageHandlerInstance == null)
            wsMessageHandlerInstance = new WsMessageHandler(netSessionFactory);

        return wsMessageHandlerInstance;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
	if (Comet.isDebugging) {
	    System.out.println("WsMessageHandler :: vai criar a session...");
	}
	
        final INetSession session = this.sessionFactory.createSession(ctx);
        
        ctx.attr(ATTR_SESSION).set(session);
        if (session == null) {
            ctx.disconnect();
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
	if (Comet.isDebugging) {
	    System.out.println("WsMessageHandler :: channelRead()");
	}
	
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            HttpHeaders headers = httpRequest.headers();
            if (headers.get("Connection").equalsIgnoreCase("Upgrade") || headers.get("Upgrade").equalsIgnoreCase("WebSocket")) {
		handleHandshake(ctx, httpRequest);
		
		if (Comet.isDebugging) {
		    System.out.println("WsMessageHandler :: fez o handshake");
		}
            }
        } else {
            System.out.println("WsMessageHandler :: não http");
            System.out.println(msg.getClass());

        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshaker serverHandshake;
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(null, null, true);
        serverHandshake = wsFactory.newHandshaker(req);
        if (serverHandshake == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            serverHandshake.handshake(ctx.channel(), req);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (ctx.attr(ATTR_SESSION).get() == null) {
            return;
        }

        try {
            INetSession session = ctx.attr(ATTR_SESSION).get();

            this.sessionFactory.disposeSession(session);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ctx.attr(ATTR_SESSION).remove();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
	if (Comet.isDebugging) {
	    System.out.println("WsMessageHandler :: userEventTriggered()");
	}
	
        if (NetworkManager.IDLE_TIMER_ENABLED) {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.READER_IDLE) {
                    ctx.close();
                } else if (e.state() == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(new PingMessageComposer(), ctx.voidPromise());
                }
            }
        }

        if (evt instanceof ChannelInputShutdownEvent) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx.channel().isActive()) {
            ctx.close();
        }

        if (cause instanceof IOException) return;

	if (Comet.isDebugging) {
	    LOGGER.error("Exception caught in WsMessageHandler", cause);
	}
    }

//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
//	LOGGER.info("Received: '" + frame.text() + "'");
//	
//	IMessageEvent event = new WsMessageEvent(frame.text());
//	
//	try {
//	    final INetSession netSession = ctx.attr(ATTR_SESSION).get();
//
//	    if (netSession != null) {
//		netSession.getMessageHandler().handleMessage(event, netSession);
//	    }
//	} catch (Exception e) {
//	    LOGGER.error("Error while receiving message", e);
//	}
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageEvent msg) throws Exception {
	if (Comet.isDebugging) {
	    System.out.println("WsMessageHandler :: channelRead0()");
	}
	
        try {
	    final INetSession netSession = ctx.attr(ATTR_SESSION).get();
	    
	    if (Comet.isDebugging) {
		System.out.println("session=" + netSession);
	    }

	    if (netSession != null) {
		netSession.getMessageHandler().handleMessage(msg, netSession);
	    }
	} catch (Exception e) {
	    if (Comet.isDebugging) {
		LOGGER.error("Error while receiving message", e);
	    }
	}
    }
}