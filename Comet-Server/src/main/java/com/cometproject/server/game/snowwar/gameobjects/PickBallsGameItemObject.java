package com.cometproject.server.game.snowwar.gameobjects;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.Direction360;
import com.cometproject.server.game.snowwar.PlayerTile;
import com.cometproject.server.game.snowwar.Tile;

public abstract class PickBallsGameItemObject extends GameItemObject {
    protected int parentFuseId;
    protected int snowBalls;
    protected Tile location;

    public int concurrentUses;

    public PickBallsGameItemObject(int _arg1, Tile _arg2, int _arg3, int _arg4){
        super(_arg1);
        location = _arg2;
        snowBalls = _arg3;
        parentFuseId = _arg4;
    }

    @Override
    public Direction360 direction360(){
        return (null);
    }

    @Override
    public PlayerTile location3D(){
        return (location.location());
    }

    public int _4rk(){
        return (parentFuseId);
    }

    public boolean canPickUpFromHere(){
        return snowBalls > concurrentUses;
    }

    public int pickUp(int ammount){
        if (snowBalls < ammount) {
            ammount = snowBalls;
        }
        onSnowballPickup(ammount);
        return (ammount);
    }

    public abstract void onSnowballPickup(int ammount);
}
