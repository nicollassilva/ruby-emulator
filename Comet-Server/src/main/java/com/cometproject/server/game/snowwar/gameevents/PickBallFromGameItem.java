package com.cometproject.server.game.snowwar.gameevents;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.game.snowwar.gameobjects.PickBallsGameItemObject;

public class PickBallFromGameItem extends Event {
    public HumanGameObject player;
    public PickBallsGameItemObject gameItem;

    public PickBallFromGameItem(final HumanGameObject player, final PickBallsGameItemObject gameItem) {
        EventType = Event.PICKBALLFROMGAMEITEM;
        this.player = player;
        this.gameItem = gameItem;
    }

    @Override
    public void apply() {
        int local2;
        final int local1 = player.availableSnowBallSlots();
        if (local1 > 0){
            local2 = gameItem.pickUp(1);
            if (local2 > 0){
                player.addSnowBalls(local2);
            }
        }

        if(gameItem.concurrentUses > 0) {
            gameItem.concurrentUses--;
        }
    }
}
