package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.game.rooms.objects.IRoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.WiredTriggerExecutor;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.floor.BetaRollableFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.RollableFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.RollerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerPeriodically;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.TimeSpan;
import com.cometproject.storage.api.StorageContext;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class BallItemProcessComponent implements CometTask {
    private static final int INTERVAL = 400;
    private static final int FLAG = 300;
    private final Room room;
    private final Logger log;

    private ScheduledFuture future;


    private boolean active = false;

    public BallItemProcessComponent(Room room) {
        this.room = room;

        log = LogManager.getLogger("Ball Item Process [" + room.getData().getName() + "]");
    }

    public void start() {
        if (this.future != null && this.active) {
            stop();
        }

        this.active = true;

        this.future = CometThreadManager.getInstance().executePeriodic(this, 0, INTERVAL, TimeUnit.MILLISECONDS);

        if(Comet.isDebugging) {
            log.debug("Processing started");
        }
    }

    public void stop() {
        if (this.future != null) {
            this.active = false;

            this.future.cancel(false);

            if(Comet.isDebugging) {
                log.debug("Processing stopped");
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void processTick() {
        if (!this.active) {
            return;
        }

        if (!this.getRoom().getEntities().hasPlayers()) {
            return;
        }



        for (final RoomItemFloor item : this.getRoom().getItems().getFloorItems().values()) {
            try {
                if ((item == null || !item.requiresTick())) {
                    continue;
                }

                if (!(item instanceof RollableFloorItem) && !(item instanceof BetaRollableFloorItem)) {
                    continue;
                }

                if (item.isStateSwitched()) {
                    item.restoreState();
                }

                item.tick();
            } catch (Exception e) {
                this.handleException(item, e);
            }
        }



        if(Comet.isDebugging) {
            final TimeSpan span = new TimeSpan(System.currentTimeMillis(), System.currentTimeMillis());

            if (span.toMilliseconds() > FLAG) {
                log.warn("ItemProcessComponent process took: " + span.toMilliseconds() + "ms to execute.");
            }
        }
    }

    @Override
    public void run() {
        this.processTick();
    }

    protected void handleException(RoomItem item, Exception e) {
        log.error("Error while processing ball item: " + item.getId() + " (" + item.getClass().getSimpleName() + ")", e);
    }

    public Room getRoom() {
        return this.room;
    }

}
