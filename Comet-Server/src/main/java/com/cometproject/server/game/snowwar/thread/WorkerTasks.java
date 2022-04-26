package com.cometproject.server.game.snowwar.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by SpreedBlood on 2017-12-23.
 */
public class WorkerTasks {
    public static int serverType;

    public static final int SERVER_TINY = 0;
    public static final int SERVER_SMALL = 1;
    public static final int SERVER_NORMAL = 2;
    public static final int SERVER_LARGE = 3;
    public static final int SERVER_EXTRALARGE = 4;
    public static final int SERVER_TURBO = 5;

    public static ScheduledThreadPoolExecutor SnowWarTasks;

    public static void initWorkers() {
        SnowWarTasks = new ScheduledThreadPoolExecutor(1);
    }

    public static void addTask(final GameTask task, final int initDelay, final int repeatRate, final ScheduledThreadPoolExecutor worker) {
        if (repeatRate > 0) {
            task.future = worker.scheduleAtFixedRate(task, initDelay, repeatRate, TimeUnit.MILLISECONDS);
        } else {
            task.future = worker.schedule(task, initDelay, TimeUnit.MILLISECONDS);
        }
    }
}
