package com.cometproject.server.game.snowwar.thread;

import com.cometproject.server.game.snowwar.SnowPlayerQueue;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.tasks.SnowArenaEnd;
import com.cometproject.server.game.snowwar.tasks.SnowArenaRun;
import com.cometproject.server.game.snowwar.tasks.SnowStageRun;
import com.cometproject.server.game.snowwar.tasks.SnowStageStarting;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.SnowStageLoading;

import java.util.concurrent.ScheduledFuture;

public class SnowWarTask extends GameTask {
    public SnowWarRoom room;

    public static void addTask(GameTask task, int initDelay, int repeatDelay) {
        WorkerTasks.addTask(task, initDelay, repeatDelay, WorkerTasks.SnowWarTasks);
    }

    public SnowWarTask(SnowWarRoom snowRoom) {
        this.room = snowRoom;
    }

    @Override
    public void run() {
        try {
            if (this.room.STATUS == 6) {
                this.future.cancel(false);
                SnowArenaEnd.exec(this.room);
                return;
            }
            if (this.room.STATUS == 5) {
                SnowArenaRun.exec(this.room);
                return;
            }
            if (this.room.STATUS == 4) {
                SnowStageRun.exec(this.room);
                this.room.STATUS = 5;
                return;
            }
            if (this.room.STATUS == 3) {
                SnowStageStarting.exec(this.room);
                this.room.STATUS = 4;
                SnowWarTask.addTask(this, 6000, 150);
                return;
            }
            if (this.room.STATUS == 2) {
                SnowStageLoading.exec(this.room);
                if (this.room.STATUS == 3) {
                    this.future.cancel(false);
                    SnowWarTask.addTask(this, 6000, 0);
                }
                return;
            }
            if (this.room.STATUS == 1 && this.room.TimeToStart-- == 0) {
                this.future.cancel(false);
                SnowPlayerQueue.roomLoaded(this.room);
                this.room.STATUS = 2;
                SnowWarTask.addTask(this, 100, 200);
            }
        }
        catch (Exception ex) {
            this.future.cancel(false);
            ex.printStackTrace();
            System.out.println("SnowEngine " + ex);
        }
    }
}
abstract class GameTask extends Thread {
    public ScheduledFuture<?> future;
}