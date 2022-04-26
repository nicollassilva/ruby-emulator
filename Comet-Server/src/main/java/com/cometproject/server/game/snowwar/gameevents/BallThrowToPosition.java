package com.cometproject.server.game.snowwar.gameevents;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

public class BallThrowToPosition extends Event {
    public HumanGameObject attacker;
    public int x;
    public int y;
    public int type;

    public BallThrowToPosition(final HumanGameObject attacker, final int x, final int y, final int type) {
        EventType = Event.BALLTHROWPOSITION;
        this.attacker = attacker;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public void apply() {
        attacker._vs(x, y);
        attacker.increaseFireLimiter();
    }
}
