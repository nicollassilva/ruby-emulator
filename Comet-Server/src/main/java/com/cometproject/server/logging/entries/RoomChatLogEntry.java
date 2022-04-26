package com.cometproject.server.logging.entries;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.logging.AbstractLogEntry;
import com.cometproject.server.logging.LogEntryType;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.utilities.TimeSpan;


public class RoomChatLogEntry extends AbstractLogEntry {
    private final int roomId;
    private final int userId;
    private final String message;
    private final int timestamp;

    public RoomChatLogEntry(int roomId, int userId, String message) {
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.timestamp = (int) Comet.getTime();
    }

    public RoomChatLogEntry(int roomId, int userId, String message, int timestamp) {
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void compose(IComposer msg) {
        msg.writeString(TimeSpan.millisecondsToDate((Comet.getTime() - getTimestamp()) * 1000L));

        msg.writeInt(this.getPlayerId());
        msg.writeString(PlayerDao.getUsernameByPlayerId(this.getPlayerId()));
        msg.writeString(this.getString());
        msg.writeBoolean(false);
    }

    @Override
    public LogEntryType getType() {
        return LogEntryType.ROOM_CHATLOG;
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
        return this.userId;
    }
}
