package com.cometproject.server.network.sessions;

import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.networking.sessions.ISessionManager;
import com.cometproject.api.networking.sessions.ISessionService;
import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public final class SessionManager implements ISessionManager, ISessionService {
    public static final AttributeKey<Session> SESSION_ATTR = AttributeKey.valueOf("Session.attr");
    public static final AttributeKey<Integer> CHANNEL_ID_ATTR = AttributeKey.valueOf("ChannelId.attr");
    public static boolean isLocked = false;
    private final AtomicInteger idGenerator = new AtomicInteger();
    private final Map<Integer, ISession> sessions = new ConcurrentHashMap<>();
    private final Map<String, SessionAccessLog> accessLog = new ConcurrentHashMap<>();
    private final ArrayList<String> pendingConnections = new ArrayList<>();
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public SessionManager() {
        SessionManagerAccessor.getInstance().setSessionManager(this);
    }

    public boolean add(ChannelHandlerContext channel, String ipAddress) {
        Session session = new Session(channel, ipAddress);

        session.initialise();

        channel.channel().attr(SessionManager.SESSION_ATTR).set(session);
        this.channelGroup.add(channel.channel());
        channel.channel().attr(CHANNEL_ID_ATTR).set(this.idGenerator.incrementAndGet());


        if (Comet.isDebugging) {
            System.out.println("\nSessionManager :: session=" + session + "\n");
        }


        return (this.sessions.putIfAbsent(channel.channel().attr(CHANNEL_ID_ATTR).get(), session) == null);
    }

    public Session get(ChannelHandlerContext ctx) {
        for (ISession session : this.sessions.values()) {
            if (session instanceof Session) {
                if (((Session) session).getChannel().channel().id().equals(ctx.channel().id())) {
                    return (Session) session;
                }
            }
        }
        return null;
    }

    public boolean remove(ChannelHandlerContext channel) {

        if (this.sessions.containsKey(channel.channel().attr(CHANNEL_ID_ATTR).get())) {
            this.channelGroup.remove(channel.channel());
            this.sessions.remove(channel.channel().attr(CHANNEL_ID_ATTR).get());

            return true;
        }

        return false;
    }

    public boolean disconnectByPlayerId(int id) {
        if (PlayerManager.getInstance().getSessionIdByPlayerId(id) == -1) {
            return false;
        }

        int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(id);
        Session session = (Session) sessions.get(sessionId);

        if (session != null) {
            session.disconnect();
            return true;
        }

        return false;
    }

    public Session getByPlayerId(int id) {
        if (PlayerManager.getInstance().getSessionIdByPlayerId(id) != -1) {
            int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(id);

            return (Session) sessions.get(sessionId);
        }

        return null;
    }

    public Session fromPlayer(int id) {
        if (PlayerManager.getInstance().getSessionIdByPlayerId(id) != -1) {
            int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(id);

            return (Session) sessions.get(sessionId);
        }

        return null;
    }

    public Session fromPlayer(String username) {
        int playerId = PlayerManager.getInstance().getPlayerIdByUsername(username);

        if (playerId == -1)
            return null;

        int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(playerId);

        if (sessionId == -1)
            return null;

        if (this.sessions.containsKey(sessionId))
            return (Session) this.sessions.get(sessionId);

        return null;
    }

    public Set<ISession> getByPlayerPermission(String permission) {
        // TODO: Optimize this
        Set<ISession> sessions = new HashSet<>();

//        int rank = PermissionsManager.getInstance().getPermissions().get(permission).getRank();
//
//        for (Map.Entry<Integer, ISession> session : this.sessions.entrySet()) {
//            if (session.getValue().getPlayer() != null) {
//                if (((Session) session.getValue()).getPlayer().getData().getRank() >= rank) {
//                    sessions.add(session.getValue());
//                }
//            }
//        }

        return sessions;
    }

    public Session getByPlayerUsername(String username) {
        int playerId = PlayerManager.getInstance().getPlayerIdByUsername(username);

        if (playerId == -1)
            return null;

        int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(playerId);

        if (sessionId == -1)
            return null;

        if (this.sessions.containsKey(sessionId))
            return (Session) this.sessions.get(sessionId);

        return null;
    }

    public int getUsersOnlineCount() {
        return PlayerManager.getInstance().size();
    }

    public Map<Integer, ISession> getSessions() {
        return this.sessions;
    }

    public void broadcast(IMessageComposer msg) {
        this.getChannelGroup().writeAndFlush(msg);
//
//        for (Session client : sessions.values()) {
//            client.getChannel().write(msg);
//        }
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void broadcastToModerators(IMessageComposer messageComposer) {
        for (ISession session : this.sessions.values()) {
            if (session.getPlayer() != null && session.getPlayer().getPermissions() != null && session.getPlayer().getPermissions().getRank().modTool()) {
                session.send(messageComposer);
            }
        }
    }

    @Override
    public void parseCommand(String[] message, ChannelHandlerContext ctx) {
        String password = message[0];

        if (password.equals("cometServer")) {
            String command = message[1];

            switch (command) {
                default: {
                    ctx.channel().writeAndFlush("response||You're connected!");
                    break;
                }

                case "stats": {
                    ctx.channel().writeAndFlush("response||" + JsonUtil.getInstance().toJson(Comet.getStats()));
                    break;
                }

            }
        } else {
            ctx.disconnect();
        }
    }

    public Map<String, SessionAccessLog> getAccessLog() {
        return accessLog;
    }

    public ArrayList<String> getPendingConnections() {
        return pendingConnections;
    }

    public List<ISession> getPlayersIdFromUniqueId(int id) {
        final Session firstSession = NetworkManager.getInstance().getSessions().fromPlayer(id);

        if(firstSession == null) {
            return null;
        }

        return this.getSessions().values().stream().filter(session -> session != null && ((Session) session).getUniqueId().equals(firstSession.getUniqueId())).toList();
    }
}
