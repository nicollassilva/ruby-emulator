package com.cometproject.server.game.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.gameobjects.GameItemObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SnowWarArenaBase {
    public int ArenaHeight;
    public int ArenaType;
    public int ArenaWidth;
    public String HeightMap;
    public List<GamefuseObject> fuseObjects = new ArrayList<GamefuseObject>(200);
    public List<SpawnPoint> spawnsBLUE = new ArrayList<SpawnPoint>(5);
    public List<SpawnPoint> spawnsRED = new ArrayList<SpawnPoint>(5);


    public abstract void gameObjects(Map<Integer, GameItemObject> gameObjects, SnowWarRoom room);
}
