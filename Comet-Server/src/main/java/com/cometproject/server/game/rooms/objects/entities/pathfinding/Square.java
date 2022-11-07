package com.cometproject.server.game.rooms.objects.entities.pathfinding;

import java.util.HashSet;
import java.util.Set;

public class Square {
    public int x;
    public int y;
    public int xy;
    public float height;

    public static final int HEIGHT_MULT = 256;
    public static final int FLAG_BLOCKED = 16384;
    public static final int MAX_HEIGHT = 64;
    public Set<Square> adjacencies = new HashSet(4, 1.0F);
    public Set<Square> adjacenciesNoDiagonal = new HashSet(4, 1.0F);

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }


}
