package com.cometproject.server.protocol.codec.ws;

import com.cometproject.server.protocol.messages.MessageEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class WebSocketMessageDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame in, List<Object> out) {
        try {
            ByteBuf bf = in.content();
            
            if (bf.readableBytes() < 4) {
        	//System.out.println("bf.readableBytes() < 4");
                return;
            }
            bf.markReaderIndex();
            int length = bf.readInt();
            
            if (bf.readableBytes() < length) {
        	//System.out.println("bf.readableBytes() < length");
                bf.resetReaderIndex();
                return;
            }
            
            if (length < 0) {
        	//System.out.println("length < 0");
                return;
            }
            
            out.add(new MessageEvent(length, bf.readBytes(length)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}