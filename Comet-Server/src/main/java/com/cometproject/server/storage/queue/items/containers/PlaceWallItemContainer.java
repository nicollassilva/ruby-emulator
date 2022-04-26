package com.cometproject.server.storage.queue.items.containers;

public class PlaceWallItemContainer {

    private int roomId;
    private String wallPos;
    private String data;
    private long itemId;

    public PlaceWallItemContainer(int roomId, String wallPos, String data, long itemId) {
        this.roomId = roomId;
        this.wallPos = wallPos;
        this.data = data;
        this.itemId = itemId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getWallPos() {
        return wallPos;
    }

    public String getData() {
        return data;
    }

    public long getItemId() {
        return itemId;
    }
}
