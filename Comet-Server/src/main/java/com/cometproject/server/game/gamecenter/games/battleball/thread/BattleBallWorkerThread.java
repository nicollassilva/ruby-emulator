package com.cometproject.server.game.gamecenter.games.battleball.thread;

import com.cometproject.server.game.players.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BattleBallWorkerThread {
    private static final Logger log = LogManager.getLogger(PlayerManager.class.getName());

    public static final int SERVER_TINY = 0;
    public static final int SERVER_SMALL = 1;
    public static final int SERVER_NORMAL = 2;
    public static final int SERVER_LARGE = 3;
    public static final int SERVER_EXTRALARGE = 4;
    public static final int SERVER_TURBO = 5;

    public static ScheduledThreadPoolExecutor BattleBallTasks;

    public static void initWorkers() {
        BattleBallTasks = new ScheduledThreadPoolExecutor(1);
        log.info("Battle Ball Tasks started");
    }

    public static void addTask(final BattleBallThread task, final int initDelay, final int repeatRate, final ScheduledThreadPoolExecutor worker) {
        if (repeatRate > 0) {
            try{
                if(task == null || worker == null) {
                    return;
                }

                task.future = worker.scheduleAtFixedRate(task, initDelay, repeatRate, TimeUnit.MILLISECONDS);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            task.future = worker.schedule(task, initDelay, TimeUnit.MILLISECONDS);
        }
    }




}
