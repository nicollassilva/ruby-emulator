package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;

import java.util.ArrayList;
import java.util.List;

public class WiredCustomTeamLoses extends WiredActionItem {
    private static final int PARAM_TEAM_ID = 0;

    public WiredCustomTeamLoses(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getWiredData().getParams().size() != 1) {
            this.getWiredData().getParams().put(PARAM_TEAM_ID, 1); // team red
        }
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 9;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        this.getRoom().getGame().decreaseScore(getTeam(), 100000);
        for(PlayerEntity player : this.getRoom().getGame().getPlayers()) {
            if(player.getGameTeam() == getTeam()) {
                this.getRoom().getGame().removeFromTeam(getTeam(), player);
                player.applyEffect(new PlayerEffect(0, false));
            }
        }
    }

    private GameTeam getTeam() {
        switch (this.getWiredData().getParams().get(PARAM_TEAM_ID)) {

            case 1:
                return GameTeam.RED;
            case 2:
                return GameTeam.GREEN;
            case 3:
                return GameTeam.BLUE;
            case 4:
                return GameTeam.YELLOW;
        }

        return GameTeam.NONE;
    }
}
