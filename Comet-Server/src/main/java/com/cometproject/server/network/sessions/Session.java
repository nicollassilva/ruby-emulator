package com.cometproject.server.network.sessions;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.players.data.components.messenger.IMessengerFriend;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.discord.Webhook;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.snowwar.data.SnowWarPlayerData;
import com.cometproject.server.network.messages.outgoing.notification.LogoutMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.protocol.codec.websockets.HttpCustomHandler;
import com.cometproject.server.protocol.crypto.HabboEncryption;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.UUID;


public class Session implements ISession {
    public static int CLIENT_VERSION = 0;
    private final ChannelHandlerContext channel;
    private final UUID uuid = UUID.randomUUID();
    private Logger logger = LogManager.getLogger(Session.class.getName());
    private SessionEventHandler eventHandler;
    private final boolean isClone = false;
    private String uniqueId = "";
    private Player player;
    public SnowWarPlayerData snowWarPlayerData;
    private boolean disconnectCalled = false;

    private ChannelHandlerContext wsChannel;
    private final HabboEncryption encryption;
    private boolean handshakeFinished;

    private long lastPing = Comet.getTime();

    private String ipAddress = null;
    public Session(ChannelHandlerContext channel, String ipAddress ) {
        this.channel = channel;
        this.ipAddress = ipAddress;
        this.encryption = CometSettings.cryptoEnabled ? new HabboEncryption(CometSettings.crypto_e, CometSettings.crypto_n, CometSettings.crypto_d) : null;
    }

    public void initialise() {
        this.eventHandler = new SessionEventHandler(this);
    }

    public void onDisconnect() {
        if (this.disconnectCalled) {
            return;
        }

        this.disconnectCalled = true;

        PlayerManager.getInstance().getPlayerLoadExecutionService().submit(() -> {
            try {
                if (player != null && player.getData() != null)
                    PlayerManager.getInstance().remove(player.getId(), player.getData().getUsername(), this.channel.channel().attr(SessionManager.CHANNEL_ID_ATTR).get(), this.getIpAddress());

                this.eventHandler.dispose();

                if(this.player != null) {
                    for (IMessengerFriend friend : this.getPlayer().getMessenger().getFriends().values()) {
                        if (!friend.isOnline() || friend.getUserId() == this.getPlayer().getId()) {
                            continue;
                        }

                        if(CometSettings.userDisconnectNotification) {
                            friend.getSession().send(new NotificationMessageComposer("looks/figure/" + player.getData().getUsername(),
                                    Locale.getOrDefault("player.online", "%username% se ha desconectado!")
                                            .replace("%username%", player.getData().getUsername())));
                        }
                    }
                }

                if (this.player != null) {
                    if (this.getPlayer().getPermissions().getRank().modTool()) {
                        ModerationManager.getInstance().removeModerator(this);
                    }

                    if (this.getPlayer().getPermissions().getRank().messengerLogChat()) {
                        ModerationManager.getInstance().removeLogChatUser(this);
                    }

                    if (this.getPlayer().getPermissions().getRank().messengerAlfaChat()) {
                        ModerationManager.getInstance().removeAlfaChatUser(this);
                    }

                    try {
                        this.getPlayer().dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.setPlayer(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnect() {
        this.onDisconnect();

        this.getChannel().disconnect();
    }

    public String getIpAddress() {
        String ipAddress = "0.0.0.0";

        if (this.ipAddress != null && !this.ipAddress.isEmpty()){
            return this.ipAddress;
        }

        if(this.getChannel().channel().hasAttr(HttpCustomHandler.WS_IP)) {
            return this.getChannel().channel().attr(HttpCustomHandler.WS_IP).get();
        } else if (this.player == null || !CometSettings.useDatabaseIp) {
            return ((InetSocketAddress) this.getChannel().channel().remoteAddress()).getAddress().getHostAddress();
        } else {
            if (this.getPlayer() != null) {
                ipAddress = PlayerDao.getIpAddress(this.getPlayer().getId());
            }
        }

        return ipAddress;
    }

    public void disconnect(String reason) {
        this.send(new LogoutMessageComposer(reason));
        this.disconnect();
    }

    public void handleMessageEvent(MessageEvent msg) {
        this.eventHandler.handle(msg);
    }

    public Session sendQueue(final IMessageComposer msg) {
        return this.send(msg, true);
    }

    public Session send(IMessageComposer msg) {
        return this.send(msg, false);
    }

    public Session send(IMessageComposer msg, boolean queue) {
        if (msg == null) {
            return this;
        }

        if (msg.getId() == 0) {
            logger.debug("Unknown header ID for message: " + msg.getClass().getSimpleName());
        }

        if (!(msg instanceof AvatarUpdateMessageComposer) && !(msg instanceof UpdateFloorItemMessageComposer) && Comet.isDebugging)
            logger.debug("Sent message: " + msg.getClass().getSimpleName() + " / " + msg.getId());

        if (!queue) {
            this.channel.writeAndFlush(msg, channel.voidPromise());
        } else {
            this.channel.write(msg);
        }
        return this;
    }

    public void flush() {
        this.channel.flush();
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        if (player == null || player.getData() == null) {
            return;
        }

        final String username = player.getData().getUsername();

        this.logger = LogManager.getLogger("[" + username + "][" + player.getId() + "]");
        this.player = player;
        this.snowWarPlayerData = new SnowWarPlayerData(player);

        int channelId = this.channel.channel().attr(SessionManager.CHANNEL_ID_ATTR).get();

        PlayerManager.getInstance().put(player.getId(), channelId, username, this.getIpAddress());

        if (player.getPermissions().getRank().modTool()) {
            ModerationManager.getInstance().addModerator(player.getSession());
        }

        if (player.getPermissions().getRank().messengerAlfaChat()) {
            ModerationManager.getInstance().addAlfa(player.getSession());
        }

        if (player.getPermissions().getRank().messengerLogChat()) {
            ModerationManager.getInstance().addLogChatUser(player.getSession());
        }
    }

    public ChannelHandlerContext getChannel() {
        return this.channel;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getSessionId() {
        return uuid;
    }

    public HabboEncryption getEncryption() {
        return this.encryption;
    }

    public boolean isHandshakeFinished() {
        return handshakeFinished;
    }

    public void setHandshakeFinished(boolean handshakeFinished) {
        this.handshakeFinished = handshakeFinished;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    public ChannelHandlerContext getWsChannel() {
        return wsChannel;
    }

    public void setWsChannel(ChannelHandlerContext wsChannel) {
        this.wsChannel = wsChannel;
    }
}