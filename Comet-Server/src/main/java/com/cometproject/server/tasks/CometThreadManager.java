package com.cometproject.server.tasks;

import com.cometproject.api.config.Configuration;
import com.cometproject.api.utilities.Initialisable;
import com.cometproject.server.game.rooms.types.components.ItemProcessComponent;
import com.cometproject.server.game.rooms.types.components.ProcessComponent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.*;


public class CometThreadManager implements Initialisable {
    public static int POOL_SIZE = 0;
    private static CometThreadManager cometThreadManagerInstance;
    private ScheduledExecutorService coreExecutor;
    private ScheduledExecutorService ballExecutor;
    private ScheduledExecutorService roomProcessingExecutor;

    public CometThreadManager() {

    }

    public static CometThreadManager getInstance() {
        if (cometThreadManagerInstance == null)
            cometThreadManagerInstance = new CometThreadManager();

        return cometThreadManagerInstance;
    }

    @Override
    public void initialize() {
        int poolSize = Integer.parseInt(Configuration.currentConfig().get("comet.system.taskThreads"));

        this.coreExecutor = Executors.newScheduledThreadPool(poolSize, r -> {
            POOL_SIZE++;

            Thread scheduledThread = new Thread(r);
            scheduledThread.setName("Comet-Scheduler-Thread-" + POOL_SIZE);

            final Logger log = LogManager.getLogger("Comet-Scheduler-Thread-" + POOL_SIZE);
            scheduledThread.setUncaughtExceptionHandler((t, e) -> log.error("Exception in worker thread", e));

            return scheduledThread;
        });

        final int roomProcessingPool = Integer.parseInt(Configuration.currentConfig().get("comet.system.taskRoomThreads"));

        this.roomProcessingExecutor = Executors.newScheduledThreadPool(roomProcessingPool, r -> {
            final Thread scheduledThread = new Thread(r);
            scheduledThread.setName("Room-Processor-" + POOL_SIZE);

            final Logger log = LogManager.getLogger("Comet-Room-Scheduler-Thread-" + POOL_SIZE);
            scheduledThread.setUncaughtExceptionHandler((t, e) -> log.error("Exception in room worker thread", e));

            return scheduledThread;
        });

        this.ballExecutor = new ScheduledThreadPoolExecutor(8);

    }

    public void executeOnce(CometTask task) {
        this.coreExecutor.submit(task);
    }

    public ScheduledFuture executePeriodic(CometTask task, long initialDelay, long period, TimeUnit unit) {
        if (task instanceof ProcessComponent || task instanceof ItemProcessComponent) {
            // Handle room processing in a different pool, this should help against
            return this.roomProcessingExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
        }

        return this.coreExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public ScheduledFuture executeSchedule(CometTask task, long delay, TimeUnit unit) {
        if (task instanceof ProcessComponent) {
            return this.roomProcessingExecutor.schedule(task, delay, unit);
        }

        return this.coreExecutor.schedule(task, delay, unit);
    }
    public ScheduledFuture executeScheduleBall(CometTask task, long delay, TimeUnit unit) {
        return this.ballExecutor.schedule(task, delay, unit);

    }

    public ScheduledExecutorService getCoreExecutor() {
        return coreExecutor;
    }
}