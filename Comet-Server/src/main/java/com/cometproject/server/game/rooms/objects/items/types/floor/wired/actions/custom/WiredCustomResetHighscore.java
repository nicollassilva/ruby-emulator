package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.tasks.CometThreadManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WiredCustomResetHighscore extends WiredActionItem {

    public WiredCustomResetHighscore(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 1;
    }

    @Override
    public boolean usesDelay() {
        return false;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        final PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        resetHighscore(playerEntity);

        CometThreadManager.getInstance().executeSchedule(() -> resetHighscore(playerEntity), this.getWiredData().getDelay() * 500L, TimeUnit.MILLISECONDS);
    }

    private void resetHighscore(PlayerEntity playerEntity) {
        final List<HighscoreFloorItem> scoreboards = playerEntity.getRoom().getItems().getByClass(HighscoreFloorItem.class);

        if (scoreboards.size() != 0) {
            for (final HighscoreFloorItem scoreboard : scoreboards) {
                scoreboard.resetScoreboard();
            }
        }

        playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("highscore", "Has reiniciado correctamente la clasificación de la sala."));
    }
}
