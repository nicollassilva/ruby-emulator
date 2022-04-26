package com.cometproject.server.game.snowwar.gameevents;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

public class MakeSnowBall extends Event {
    public HumanGameObject player;

    public MakeSnowBall(final HumanGameObject player) {
        EventType = Event.MAKENOWBALL;
        this.player = player;
    }

    @Override
    public void apply() {
        player.makeSnowBall();
    }
}
