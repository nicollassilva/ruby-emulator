package com.cometproject.server.game.players.login.queue;

import com.cometproject.server.network.sessions.Session;


public class PlayerLoginQueueEntry {
    private final Session connectingClient;

    private final int playerId;
    private final String ssoTicket;

    public PlayerLoginQueueEntry(Session client, int id, String sso) {
        this.connectingClient = client;

        this.playerId = id;
        this.ssoTicket = sso;
    }

    public Session getClient() {
        return this.connectingClient;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getSsoTicket() {
        return ssoTicket;
    }
}
