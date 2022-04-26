package com.cometproject.server.game.items.music;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TraxMachineSong {
    private final int id;
    private final int roomId;
    private final int userId;
    private String data;
    private final int timestamp;

    public TraxMachineSong(int id, int roomId, int userId, String data, int timestamp) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.data = data;
        this.timestamp = timestamp;
    }

    public TraxMachineSong(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.roomId = resultSet.getInt("room_id");
        this.userId = resultSet.getInt("user_id");
        this.data = resultSet.getString("data");
        this.timestamp = resultSet.getInt("timestamp");
    }

    public int getId() {
        return id;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getData() {
        return data;
    }

    public int getUserId() {
        return userId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setData(String data) {
        this.data = data;
    }
}
