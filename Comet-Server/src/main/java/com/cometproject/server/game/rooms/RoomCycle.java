package com.cometproject.server.game.rooms;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomPromotion;
import com.cometproject.server.storage.queries.rooms.RoomDao;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.TimeSpan;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class RoomCycle implements CometTask {
    private final static int PERIOD = 500;
    private final static int FLAG = 2000;
    private final static int ROOMCOUNTUPDATEINTERVAL = 60000; // every 10 seconds
    private final Logger log = LogManager.getLogger(RoomCycle.class.getName());
    private ScheduledFuture myFuture;
    private long lastRoomCountUpdate = 0;

    public RoomCycle() {
    }

    public void start() {
        this.myFuture = CometThreadManager.getInstance().executePeriodic(this, PERIOD, PERIOD, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        this.myFuture.cancel(false);
    }

    public boolean isActive() {
        return (!this.myFuture.isCancelled());
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();

            // run this before ticking
            RoomManager.getInstance().unloadIdleRooms();

            final List<Integer> expiredPromotedRooms = Lists.newArrayList();

            for (final RoomPromotion roomPromotion : RoomManager.getInstance().getRoomPromotions().values()) {
                if (roomPromotion.isExpired()) {
                    expiredPromotedRooms.add(roomPromotion.getRoomId());
                }
            }

            if (expiredPromotedRooms.size() != 0) {
                for (final int roomId : expiredPromotedRooms) {
                    RoomManager.getInstance().getRoomPromotions().remove(roomId);
                }

                expiredPromotedRooms.clear();
            }

            if (new TimeSpan(lastRoomCountUpdate, System.currentTimeMillis()).toMilliseconds() > ROOMCOUNTUPDATEINTERVAL) {
                lastRoomCountUpdate = System.currentTimeMillis();
                final Map<Integer, Integer> userCount = Maps.newHashMap();

                for (final Room room : RoomManager.getInstance().getRoomInstances().values()) {
                    final int playerCount = room.getEntities().playerCount();

                    if (playerCount > 0) {
                        userCount.put(room.getId(), playerCount);
                    }
                }

                RoomDao.saveUserCounts(userCount);
                userCount.clear();
            }

            final TimeSpan span = new TimeSpan(start, System.currentTimeMillis());

            if (span.toMilliseconds() > FLAG) {
                log.warn("Global room processing (" + RoomManager.getInstance().getRoomInstances().size() + " rooms) took: " + span.toMilliseconds() + "ms to execute.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while cycling rooms", e);
        }
    }
}
