package com.cometproject.server.storage.queue.items;

import com.cometproject.api.game.rooms.objects.IRoomItemData;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import com.cometproject.server.storage.queue.IQueue;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RoomItemQueue implements CometTask, IQueue {

    private ConcurrentLinkedQueue<IRoomItemData> itemsQueue;
    private ScheduledFuture periodic;

    public void init() {
        this.itemsQueue = new ConcurrentLinkedQueue<>();
        this.periodic = CometThreadManager.getInstance().executePeriodic(this, 1500, 1000, TimeUnit.MILLISECONDS);
    }

    public void saveItem(IRoomItemData item) {
        this.itemsQueue.remove(item);
        this.itemsQueue.add(item);
    }

    public void stop() {
        this.periodic.cancel(false);
    }

    @Override
    public void run() {
        Set<IRoomItemData> items = Sets.newHashSet();
        for (int i = 0; i < this.itemsQueue.size(); i++) {
            items.add(this.itemsQueue.poll());
        }
        if (items.size() > 0) {
            RoomItemDao.saveItemBatch(items);
        }
    }
}
