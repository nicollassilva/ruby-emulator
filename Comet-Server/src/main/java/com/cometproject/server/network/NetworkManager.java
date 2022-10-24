package com.cometproject.server.network;

import com.cometproject.api.config.Configuration;
import com.cometproject.networking.api.INetworkingServer;
import com.cometproject.networking.api.INetworkingServerFactory;
import com.cometproject.networking.api.NetworkingContext;
import com.cometproject.networking.api.config.NetworkingServerConfig;
import com.cometproject.networking.api.sessions.INetSessionFactory;
import com.cometproject.server.network.battleball.gameserver.GameServer;
import com.cometproject.server.network.messages.GameMessageHandler;
import com.cometproject.server.network.messages.MessageHandler;
import com.cometproject.server.network.sessions.SessionManager;
import com.cometproject.server.network.sessions.net.NetSessionFactory;
import com.cometproject.server.network.ws.WebSocketChannelHandler;
import com.cometproject.server.protocol.codec.ws.WebSocketMessageEncoder;
import com.google.common.collect.Sets;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Set;


public class NetworkManager {
    public static boolean IDLE_TIMER_ENABLED = Boolean.parseBoolean(Configuration.currentConfig().get("comet.network.idleTimer.enabled", "true"));
    public static int IDLE_TIMER_READER_TIME = Integer.parseInt(Configuration.currentConfig().get("comet.network.idleTimer.readerIdleTime", "60"));
    public static int IDLE_TIMER_WRITER_TIME = Integer.parseInt(Configuration.currentConfig().get("comet.network.idleTimer.writerIdleTime", "30"));
    public static int IDLE_TIMER_ALL_TIME = Integer.parseInt(Configuration.currentConfig().get("comet.network.idleTimer.allIdleTime", "0"));
    private static NetworkManager networkManagerInstance;
    private static final Logger log = LogManager.getLogger(NetworkManager.class.getName());
    private int serverPort;
    private SessionManager sessions;
    private MessageHandler messageHandler;


    public NetworkManager() {

    }

    public static NetworkManager getInstance() {
        if (networkManagerInstance == null)
            networkManagerInstance = new NetworkManager();

        return networkManagerInstance;
    }

    public void initialize(String ip, String ports) {
        this.sessions = new SessionManager();
        this.messageHandler = new MessageHandler();

        this.serverPort = Integer.parseInt(ports.split(",")[0]);

        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

        System.setProperty("io.netty.leakDetectionLevel", "disabled");
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

//        final NettyNetworkingServerFactory serverFactory = new NettyNetworkingServerFactory(Configuration.currentConfig());

        final INetSessionFactory sessionFactory = new NetSessionFactory(this.sessions, new GameMessageHandler());
        final INetworkingServerFactory serverFactory = new NettyNetworkingServerFactory(Configuration.currentConfig());
        final NetworkingContext networkingContext = new NetworkingContext(serverFactory);

        NetworkingContext.setCurrentContext(networkingContext);


        final ServerBootstrap bootstrapWebSocket = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(final SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpServerCodec())
                                        .addLast(new HttpObjectAggregator(65536))
                                        .addLast(new WebSocketServerCompressionHandler())
                                        .addLast(new WebSocketMessageEncoder())
                                        .addLast(new WebSocketChannelHandler());

                                ch.config().setTrafficClass(24);
                                ch.config().setTcpNoDelay(true);
                            }

                        }
                )
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024))
                .option(ChannelOption.SO_BACKLOG, 500)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0)
                .childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        int wsPort = Integer.parseInt(Configuration.currentConfig().get("comet.network.websocket.port"));

        bootstrapWebSocket.bind(new InetSocketAddress(ip, wsPort)).addListener(objectFuture -> {
            if (!objectFuture.isSuccess()) {
                log.error("Error during server initialization! : " + ip + ":" + wsPort);
            } else {
                log.info("Websocket running on port:" + wsPort);
            }
        });

        final Set<Short> portSet = Sets.newHashSet();

        if (ports.contains(",")) {
            for (String port : ports.split(",")) {
                portSet.add(Short.parseShort(port));
            }
        } else {
            portSet.add(Short.parseShort(ports));
        }

        final INetworkingServer gameServer = serverFactory.createServer(new NetworkingServerConfig(ip, portSet),
                sessionFactory);

        gameServer.start();

        var port = Integer.parseInt(Configuration.currentConfig().get("comet.network.customWebSocket.port"));


        System.out.println("Starting WS on port: " + port);

        var wsServer = new GameServer(port);
        wsServer.start();
    }

    public SessionManager getSessions() {
        return this.sessions;
    }

    public MessageHandler getMessages() {
        return this.messageHandler;
    }

    public int getServerPort() {
        return serverPort;
    }

}
