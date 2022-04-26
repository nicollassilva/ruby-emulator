package com.cometproject.server.storage.queue;

import com.cometproject.server.storage.queue.pets.SavePetsQueue;
import com.cometproject.server.storage.queue.logging.*;
import com.cometproject.server.storage.queue.items.*;

public class StorageQueue {

    private RoomItemQueue itemQueue;
    private RoomItemDataQueue itemDataQueue;
    private RoomRemoveItemQueue removeItemQueue;
    private PlaceWallItemQueue placeWallItemQueue;

    private UpdateRoomEntryQueue updateRoomEntryQueue;
    private PutRoomVisitQueue putRoomVisitQueue;
    private PutEntryQueue putEntryQueue;

    private SavePetsQueue savePetsQueue;

    public StorageQueue() {
        this.itemQueue = new RoomItemQueue();
        this.itemDataQueue = new RoomItemDataQueue();
        this.removeItemQueue = new RoomRemoveItemQueue();
        this.placeWallItemQueue = new PlaceWallItemQueue();

        this.updateRoomEntryQueue = new UpdateRoomEntryQueue();
        this.putRoomVisitQueue = new PutRoomVisitQueue();
        this.putEntryQueue = new PutEntryQueue();

        this.savePetsQueue = new SavePetsQueue();


        this.itemQueue.init();
        this.itemDataQueue.init();
        this.removeItemQueue.init();
        this.placeWallItemQueue.init();
        this.updateRoomEntryQueue.init();
        this.putRoomVisitQueue.init();
        this.putEntryQueue.init();
        this.savePetsQueue.init();
    }

    public void stop() {
        this.itemQueue.stop();
        this.itemDataQueue.stop();
        this.removeItemQueue.stop();
        this.placeWallItemQueue.stop();

        this.updateRoomEntryQueue.stop();
        this.putRoomVisitQueue.stop();
        this.putEntryQueue.stop();

        this.savePetsQueue.stop();
    }

    public void run() {
        this.itemDataQueue.run();
        this.itemQueue.run();
        this.removeItemQueue.run();
        this.placeWallItemQueue.run();

        this.updateRoomEntryQueue.run();
        this.putRoomVisitQueue.run();
        this.putEntryQueue.run();

        this.savePetsQueue.run();
    }

    public PutEntryQueue getPutEntryQueue() {
        return putEntryQueue;
    }

    public PutRoomVisitQueue getPutRoomVisitQueue() {
        return putRoomVisitQueue;
    }

    public UpdateRoomEntryQueue getUpdateRoomEntryQueue() {
        return updateRoomEntryQueue;
    }

    public PlaceWallItemQueue getPlaceWallItemQueue() {
        return placeWallItemQueue;
    }

    public RoomItemQueue getItemQueue() {
        return itemQueue;
    }

    public RoomItemDataQueue getItemDataQueue() {
        return itemDataQueue;
    }

    public RoomRemoveItemQueue getRemoveItemQueue() {
        return removeItemQueue;
    }

    public SavePetsQueue getSavePetsQueue() {
        return savePetsQueue;
    }
}

