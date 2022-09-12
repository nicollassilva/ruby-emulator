package com.cometproject.server.protocol.codec;

import java.util.List;

import com.cometproject.api.networking.sessions.SessionManagerAccessor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;


public class XMLPolicyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        in.markReaderIndex();
        if (in.readableBytes() < 1) return;

        byte delimiter = in.readByte();

        in.resetReaderIndex();

        if (delimiter == 0x3C) {
            ctx.channel().writeAndFlush(
                    "<?xml version=\"1.0\"?>\r\n"
                            + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\r\n"
                            + "<cross-domain-policy>\r\n"
                            + "<allow-access-from domain=\"*\" to-ports=\"*\" />\r\n"
                            + "</cross-domain-policy>\0"
            ).addListener(ChannelFutureListener.CLOSE);

        } else if (delimiter == 0x3f) {
            try {
                String messageStr = in.toString(CharsetUtil.UTF_8);
                String[] message = messageStr.substring(1).split("\\|\\|");

                SessionManagerAccessor.getInstance().getSessionManager().parseCommand(message, ctx);
            } catch (Exception e) {
                //..
            }
        } else {
            ctx.channel().pipeline().remove(this);

            MessageDecoder decoder = ctx.pipeline().get(MessageDecoder.class);
            // decoder.decode(ctx, in, out);
        }
    }
}