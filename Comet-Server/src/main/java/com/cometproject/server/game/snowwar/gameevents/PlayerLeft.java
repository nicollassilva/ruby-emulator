package com.cometproject.server.game.snowwar.gameevents;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

public class PlayerLeft extends Event {
    public HumanGameObject player;

    public PlayerLeft(final HumanGameObject player) {
        EventType = Event.PLAYERLEFT;
        this.player = player;
    }

    @Override
    public void apply() {
        player.currentSnowWar.queueDeleteObject(player);
        player.cleanTiles();
    }
}
