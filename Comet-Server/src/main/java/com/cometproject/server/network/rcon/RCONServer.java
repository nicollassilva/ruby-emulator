package com.cometproject.server.network.rcon;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.messages.rcon.ForwardUser;
import com.cometproject.server.network.messages.rcon.ReloadCredits;
import com.cometproject.server.network.rcon.utils.RCONMessage;
import com.cometproject.server.network.rcon.utils.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gnu.trove.map.hash.THashMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RCONServer extends Server {
    private static final Logger log = LogManager.getLogger(RCONServer.class.getName());

    private final THashMap<String, Class<? extends RCONMessage>> messages;
    private final GsonBuilder gsonBuilder;
    List<String> allowedAdresses = new ArrayList<>();

    public RCONServer(String host, int port) throws Exception {
        super("Comet RCON", host, port, 1, 2);

        this.messages = new THashMap<>();

        this.gsonBuilder = new GsonBuilder();
        this.gsonBuilder.registerTypeAdapter(RCONMessage.class, new RCONMessage.RCONMessageSerializer());

        this.addRCONMessage("forwarduser", ForwardUser.class);
        this.addRCONMessage("reloadcredits", ReloadCredits.class);

        Collections.addAll(this.allowedAdresses, "127.0.0.1");
    }

    @Override
    public void initializePipeline() {
        super.initializePipeline();

        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new RCONServerHandler());
            }
        });
    }


    public void addRCONMessage(String key, Class<? extends RCONMessage> clazz) {
        this.messages.put(key, clazz);
    }

    public String handle(ChannelHandlerContext ctx, String key, String body) throws Exception {
        Class<? extends RCONMessage> message = this.messages.get(key.replace("_", "").toLowerCase());

        String result;
        if (message != null) {
            try {
                RCONMessage rcon = message.getDeclaredConstructor().newInstance();
                Gson gson = this.gsonBuilder.create();
                rcon.handle(gson, gson.fromJson(body, rcon.type));
                log.info("Handled RCON Message: {}", message.getSimpleName());
                result = gson.toJson(rcon, RCONMessage.class);

                if (Comet.isDebugging) {
                    log.debug("RCON Data {} RCON Result {}", body, result);
                }

                return result;
            } catch (Exception ex) {
                log.error("Failed to handle RCONMessage", ex);
            }
        } else {
            log.error("Couldn't find: {}", key);
        }

        throw new ArrayIndexOutOfBoundsException("Unhandled RCON Message");
    }

    public List<String> getCommands() {
        return new ArrayList<>(this.messages.keySet());
    }
}

