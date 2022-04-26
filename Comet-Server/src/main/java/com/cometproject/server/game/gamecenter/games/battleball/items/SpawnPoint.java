package com.cometproject.server.game.gamecenter.games.battleball.items;

public class SpawnPoint {

    public int x;
    public int y;
    public int z;
    public int rot;

    public SpawnPoint(int i, int j, int k, int r) {
        x = i;
        y = j;
        z = k;
        rot = r;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getRotation() {
        return rot;
    }

}
