package com.cometproject.server.game.rooms.types.components.games;

import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonBlob;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.GameComponent;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.RandomUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class RoomGame implements CometTask {
    protected int timer;
    protected int gameLength;
    protected GameState state = GameState.IDLE;
    protected Room room;
    private final GameType type;
    private ScheduledFuture future;

    private final Logger log;

    public RoomGame(Room room, GameType gameType) {
        this.type = gameType;
        this.log = LogManager.getLogger("RoomGame [" + room.getData().getName() + "][" + room.getData().getId() + "][" + this.type + "]");
        this.room = room;
    }

    @Override
    public void run() {
        try {
            if (timer == 0) {
                this.state = GameState.RUNNING;
                final List<WiredAddonBlob> blobs = room.getItems().getByClass(WiredAddonBlob.class);
                Collections.shuffle(blobs);

                for (final WiredAddonBlob blob : blobs) {
                    blob.onGameStarted();
                }

                onGameStarts();
            }

            try {
                if (this.getGameComponent().getBlobCounter().get() < 2) {
                    if (RandomUtil.getRandomBool(0.1)) {
                        final List<WiredAddonBlob> blobs = room.getItems().getByClass(WiredAddonBlob.class);
                        Collections.shuffle(blobs);

                        for (final WiredAddonBlob blob : blobs) {
                            blob.onGameStarted();
                        }
                    }
                }

                tick();
            } catch (Exception e) {
                log.error("Failed to process game tick", e);
            }

            if (timer >= gameLength || (gameLength - timer) <= 0) {
                onGameEnds();
                room.getGame().stop();
                //this.stop();
            }

            timer++;
        } catch (Exception e) {
            log.error("Error during game process", e);
        }
    }

    public void stop() {
        for (final WiredAddonBlob blob : room.getItems().getByClass(WiredAddonBlob.class)) {
            blob.hideBlob();
        }

        if (this.state.equals(GameState.RUNNING) && this.future != null) {
            this.future.cancel(true);
        }

        this.state = GameState.IDLE;
        this.gameLength = 0;
        this.timer = 0;
    }

    public void pause() {
        if ( this.state.equals(GameState.RUNNING) && this.future != null) {
            this.future.cancel(true);
            this.state = GameState.PAUSED;
        }
    }

    public void startTimer(int amount) {
        if (this.state.equals(GameState.RUNNING) && this.future != null) {
            this.future.cancel(true);
        }

        this.gameLength = amount;
        this.state = GameState.RUNNING;

        this.future = CometThreadManager.getInstance().executePeriodic(this, 0, 1, TimeUnit.SECONDS);
        log.debug("Game active for " + amount + " seconds");
    }

    protected GameComponent getGameComponent() {
        return this.room.getGame();
    }

    public abstract void tick();

    public abstract void onGameEnds();

    public abstract void onGameStarts();

    public GameType getType() {
        return this.type;
    }

    public Logger getLog() {
        return this.log;
    }

    public boolean isActive() {
        return this.state.equals(GameState.RUNNING);
    }

    public GameState getState() {
        return this.state;
    }

    public int getGameLength() {
        return this.gameLength;
    }
}
