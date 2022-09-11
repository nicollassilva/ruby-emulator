package com.cometproject.server.protocol.codec.ws;

import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.protocol.messages.Composer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class WebSocketMessageEncoder extends MessageToMessageEncoder<IMessageComposer> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, IMessageComposer msg, List<Object> list) {
        ByteBuf buf = ctx.alloc().buffer();
        
//        // clona a "message" pra quando mudar o id n�o afetar o flash, mas somente aqui (websocket)
//        IMessageComposer msgClone = msg;
//        msgClone.setId(Composers.getWsValueOfId(msgClone.getId()));
        
        try {
            final Composer composer = ((Composer) msg.writeMessage(buf));

            if (!composer.isFinalised()) {
                composer.content().setInt(0, composer.content().writerIndex() - 4);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        list.add(new BinaryWebSocketFrame(buf));
    }
    
}