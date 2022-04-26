package com.cometproject.server.protocol.codec.websockets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;

import java.util.List;

public class MessageInterceptorHandler extends ByteToMessageDecoder {
    private static final int MAX_FRAME_SIZE = 500000;

    private final SslContext context;
    private final boolean isSSL;
    private final WebSocketServerProtocolConfig config;

    public MessageInterceptorHandler() {
        context = SSLCertificateLoader.getContext();
        isSSL = context != null;
        config = WebSocketServerProtocolConfig.newBuilder()
                .websocketPath("/")
                .checkStartsWith(true)
                .maxFramePayloadLength(MAX_FRAME_SIZE)
                .build();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.toString(CharsetUtil.UTF_8).startsWith("GET")) {

            if(isSSL) {
                ctx.pipeline().addLast(context.newHandler(ctx.alloc()));
            }

            // this is a websocket upgrade request, so add the appropriate decoders/encoders
            ctx.pipeline().addAfter("messageInterceptor", "websocketHandler", new WebSocketFrameCodec());
            ctx.pipeline().addAfter("messageInterceptor", "protocolHandler", new WebSocketServerProtocolHandler(config));
            ctx.pipeline().addAfter("messageInterceptor", "customHttpHandler", new HttpCustomHandler());
            ctx.pipeline().addAfter("messageInterceptor", "objectAggregator", new HttpObjectAggregator(MAX_FRAME_SIZE));
            ctx.pipeline().addAfter("messageInterceptor", "httpCodec", new HttpServerCodec());
        }
        // Remove ourselves
        ctx.pipeline().remove(this);
    }
}

