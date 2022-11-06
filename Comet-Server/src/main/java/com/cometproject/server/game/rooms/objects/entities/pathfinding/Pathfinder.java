package com.cometproject.server.game.rooms.objects.entities.pathfinding;

import com.cometproject.api.game.rooms.RoomDiagonalType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.RoomObject;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class Pathfinder {
    private final Position[] diagonalMovePoints = {
            new Position(-1, -1),
            new Position(0, -1),
            new Position(1, 1),
            new Position(0, 1),
            new Position(1, -1),
            new Position(1, 0),
            new Position(-1, 1),
            new Position(-1, 0)
    };
    public static final Position[] movePoints = new Position[]{
            new Position(0, -1),
            new Position(1, 0),
            new Position(0, 1),
            new Position(-1, 0)
    };

    public List<Square> makePath(RoomObject roomFloorObject, Position end) {
        return this.makePath(roomFloorObject, end, RoomDiagonalType.STRICT.getKey(), false);
    }

    public List<Square> makePath(RoomObject roomFloorObject, Position end, byte pathfinderMode, boolean isRetry) {
      //  final long startTime = System.currentTimeMillis();

        final List<Square> squares = new ArrayList<>();
        PathfinderNode nodes = makePathReversed(roomFloorObject, end, pathfinderMode, isRetry);

        if (nodes != null) {
            while (nodes.getNextNode() != null) {
                squares.add(new Square(nodes.getPosition().getX(), nodes.getPosition().getY()));
                nodes = nodes.getNextNode();
            }
        }
   //
        return Lists.reverse(squares);
    }

    private PathfinderNode makePathReversed(RoomObject roomFloorObject, Position end, byte pathfinderMode, boolean isRetry) {
        final MinMaxPriorityQueue<PathfinderNode> openList = MinMaxPriorityQueue.maximumSize(25).create();

        final PathfinderNode[][] map = new PathfinderNode[roomFloorObject.getRoom().getMapping().getModel().getSizeX()][roomFloorObject.getRoom().getMapping().getModel().getSizeY()];
        PathfinderNode node;
        Position tmp;

        int cost;

        PathfinderNode current = new PathfinderNode(roomFloorObject.getPosition());
        current.setCost(0);

        final PathfinderNode finish = new PathfinderNode(end);

        map[current.getPosition().getX()][current.getPosition().getY()] = current;
        openList.add(current);

        while (openList.size() > 0) {
            current = openList.pollFirst();
            current.setInClosed(true);

            for (int i = 0; i < (RoomDiagonalType.isAllowed(pathfinderMode) ? diagonalMovePoints.length : movePoints.length); i++) {
                tmp = current.getPosition().add((RoomDiagonalType.isAllowed(pathfinderMode) ? diagonalMovePoints : movePoints)[i]);
                final boolean isFinalMove = (tmp.getX() == end.getX() && tmp.getY() == end.getY());

                if (this.isValidStep(roomFloorObject, new Position(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), tmp, isFinalMove, isRetry)) {
                    try {
                        if (map[tmp.getX()][tmp.getY()] == null) {
                            node = new PathfinderNode(tmp);
                            map[tmp.getX()][tmp.getY()] = node;
                        } else {
                            node = map[tmp.getX()][tmp.getY()];
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }

                    if (!node.isInClosed())
                    {

                        cost =  node.getPosition().getDistanceSquared(end);

                        if (cost < node.getCost()) {
                            node.setCost(cost);
                            node.setNextNode(current);
                        }

                        if (!node.isInOpen()) {
                            if (node.getPosition().getX() == finish.getPosition().getX() && node.getPosition().getY() == finish.getPosition().getY()) {
                                node.setNextNode(current);
                                return node;
                            }

                            node.setInOpen(true);
                            openList.add(node);
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean isValidStep(RoomObject object, Position from, Position to, boolean lastStep, boolean isRetry) {

        if (object instanceof RoomEntity)
            return object.getRoom().getMapping().isValidEntityStep((RoomEntity) object, from, to, lastStep, isRetry);
        else
            return object.getRoom().getMapping().isValidStep(0, from, to, lastStep, object instanceof RoomItemFloor, isRetry);
    }
}
