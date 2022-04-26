package com.cometproject.server.storage.queue.logging;

import com.cometproject.server.logging.database.queries.LogQueries;
import com.cometproject.server.logging.entries.RoomVisitLogEntry;
import com.cometproject.server.storage.queue.IQueue;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PutRoomVisitQueue implements CometTask, IQueue {

    private ConcurrentLinkedQueue<RoomVisitLogEntry> itemsQueue;
    private ScheduledFuture periodic;

    public void init() {
        this.itemsQueue = new ConcurrentLinkedQueue<>();
        this.periodic = CometThreadManager.getInstance().executePeriodic(this, 1500, 10000, TimeUnit.MILLISECONDS);
    }

    public void saveItem(RoomVisitLogEntry item) {
        this.itemsQueue.remove(item);
        this.itemsQueue.add(item);
    }

    public void stop() {
        this.periodic.cancel(false);
    }

    @Override
    public void run() {
        Set<RoomVisitLogEntry> items = Sets.newHashSet();
        for (int i = 0; i < this.itemsQueue.size(); i++) {
            items.add(this.itemsQueue.poll());
        }
        if (items.size() > 0) {
            LogQueries.putRoomEntryBatch(items);
        }
    }
}
