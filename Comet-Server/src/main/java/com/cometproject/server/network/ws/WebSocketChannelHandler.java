package com.cometproject.server.network.ws;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.protocol.codec.ws.WebSocketMessageDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.Map;

public class WebSocketChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (Comet.isDebugging) {
            System.out.println("WebSocketChannelHandler :: channelRead()");
        }



        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            HttpHeaders headers = httpRequest.headers();
            System.out.println(headers.get("Connection"));
            System.out.println(headers.get("Upgrade"));
            for (Map.Entry<String, String> entry : headers.entries()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            if ("Upgrade".equalsIgnoreCase(headers.get("Connection")) || "WebSocket".equalsIgnoreCase(headers.get("Upgrade"))) {
                if (Comet.isDebugging) {
                    System.out.println("WebSocketChannelHandler :: vai criar a session...");
                }

                String ipAddress = "0.0.0.0";
                if (headers.contains("X-Forwarded-For")) {
                    ipAddress = headers.get("X-Forwarded-For");
                }
                if (headers.contains("CF-Connecting-IP")) {
                    ipAddress = headers.get("CF-Connecting-IP");
                }

                //todo: set ip to session!

                if (!NetworkManager.getInstance().getSessions().add(ctx)) {
                    System.out.println("falha ao setar");

                    ctx.disconnect();
                } else {
                    ctx.pipeline().remove(this);
                    ctx.pipeline().addLast(new WebSocketMessageDecoder());
                    ctx.pipeline().addLast(new WebSocketMessageHandler());
                    handleHandshake(ctx, httpRequest);

                    if (Comet.isDebugging) {
                        System.out.println("WebSocketChannelHandler :: fez o handshake");
                    }
                }
            } else {

            }
        } else {
            System.out.println("WebSocketChannelHandler :: não fez o handshake");

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

}
