package com.cometproject.server.game.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.GameItemObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SynchronizedGameStage {
    public Map<Integer, GameItemObject> gameObjects;
    private final List<GameItemObject> _2xj;
    //public final List<GameItemObject> gameObjects = new ArrayList<GameItemObject>();

    public int objectIdCounter;

    public SynchronizedGameStage(){
        gameObjects = new LinkedHashMap<>();
        _2xj = new ArrayList<>();
    }

    public void addGameObject(GameItemObject obj) {
        if(obj.objectId == 0) {
            obj.objectId = objectIdCounter++;
        }
        gameObjects.put(obj.objectId, obj);
        obj._active = true;
    }

    public void removeGameObject(int _arg1){
        final GameItemObject local1 = gameObjects.remove(_arg1);

        if (local1 != null){
            local1.onRemove();
        }
    }

    public void queueDeleteObject(GameItemObject _arg1){
        if (_arg1 == null){
            //HabboGamesCom.log("Trying to put null in delete list.");
            return;
        }
        _2xj.add(_arg1);
        _arg1._active = false;
        _arg1.GenerateCHECKSUM((SnowWarRoom) this, -1);
    }

    public GameItemObject _3Pl(int _arg1){
        return gameObjects.get(_arg1);
    }

    public void subturn(){
        for(final GameItemObject local0 : gameObjects.values()) {
            local0.subturn(this);
        }

        if (!_2xj.isEmpty()){
            for(final GameItemObject local1 : _2xj) {
                removeGameObject(local1.objectId);
            }

            _2xj.clear();
        }
    }
}
