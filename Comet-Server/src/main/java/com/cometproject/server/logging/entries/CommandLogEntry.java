package com.cometproject.server.logging.entries;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.logging.AbstractLogEntry;
import com.cometproject.server.logging.LogEntryType;

public class CommandLogEntry extends AbstractLogEntry {
    private final int roomId;
    private final int playerId;
    private final String message;
    private final int timestamp;

    public CommandLogEntry(int roomId, int playerId, String message) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.message = message;
        this.timestamp = (int) Comet.getTime();
    }

    @Override
    public LogEntryType getType() {
        return LogEntryType.COMMAND;
    }

    @Override
    public String getString() {
        return this.message;
    }

    @Override
    public int getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int getRoomId() {
        return this.roomId;
    }

    @Override
    public int getPlayerId() {
        return this.playerId;
    }
}


