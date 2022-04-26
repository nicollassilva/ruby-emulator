package com.cometproject.server.game.snowwar.gameevents;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.MachineGameObject;

public class AddBallToMachine extends Event {
    public MachineGameObject gameItem;

    public AddBallToMachine(final MachineGameObject gameItem) {
        EventType = Event.ADDBALLTOMACHINE;
        this.gameItem = gameItem;
    }

    @Override
    public void apply() {
        gameItem.addSnowBall();
    }
}
