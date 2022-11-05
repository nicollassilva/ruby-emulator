package com.cometproject.server.storage.queue.items;

import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import com.cometproject.server.storage.queue.IQueue;
import com.cometproject.server.storage.queue.items.containers.PlaceWallItemContainer;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlaceWallItemQueue implements CometTask,   IQueue {

    private ConcurrentLinkedQueue<PlaceWallItemContainer> itemsQueue;
    private ScheduledFuture periodic;

    public void init() {
        this.itemsQueue = new ConcurrentLinkedQueue<>();
        this.periodic = CometThreadManager.getInstance().executePeriodic(this, 2500, 3000, TimeUnit.MILLISECONDS);
    }

    public void saveItem(PlaceWallItemContainer item) {
        this.itemsQueue.remove(item);
        this.itemsQueue.add(item);
    }

    public void stop() {
        this.periodic.cancel(false);
    }

    @Override
    public void run() {
        Set<PlaceWallItemContainer> items = Sets.newHashSet();
        for (int i = 0; i < this.itemsQueue.size(); i++) {
            items.add(this.itemsQueue.poll());
        }
        if (items.size() > 0) {
            RoomItemDao.placeWallItemBatch(items);
        }
    }
}
