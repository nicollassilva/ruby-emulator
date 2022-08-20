package com.cometproject.server.game.rooms.objects.entities.pathfinding;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.api.utilities.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AffectedTile  {
    public int x;
    public int y;

    public AffectedTile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * NOTE: posX and posY are included into affectedTile coords
     *
     * @param length
     * @param width
     * @param posX
     * @param posY
     * @param rotation
     * @return
     */
    public static List<AffectedTile> getAffectedBothTilesAt(int length, int width, int posX, int posY, int rotation) {
       return makeSquare(length, width, posX, posY, rotation);

/*
        pointList.add(new AffectedTile(posX, posY)); // TODO: this shouldn't be here. make duplicate coordinate.
        // maybe fix: if len and wid == 1 return only posX and posY

        if (length > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            }
        }

        if (width > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            }
        }

        return pointList;*/
    }

    public static List<AffectedTile> makeSquare(int length, int width, int posX, int posY, int rotation) {
        assert length >= 1 : "Length must be >= 1";
        assert width >= 1 : "Width must be >= 1";
        assert rotation >=0 : "Rotation must be >= 0";

        final List<AffectedTile> pointList = new ArrayList<>(length * width);
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if(rotation == 0 || rotation == 4){
                    pointList.add(new AffectedTile(posX + j, posY + i));
                }
                else if (rotation == 2 || rotation == 6){
                    pointList.add(new AffectedTile(posX + i, posY + j));
                }
            }
        }
        return pointList;
    }

    public static List<AffectedTile> getAffectedTilesAt(int length, int width, int posX, int posY, int rotation) {
        final List<AffectedTile> pointList = makeSquare(length, width, posX, posY, rotation);
        pointList.remove(new AffectedTile(posX, posY));
        return pointList;
        /*
        final List<AffectedTile> pointList = new ArrayList<>(length * width);

        if (length > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            }
        }

        if (width > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            }
        }

        return pointList;*/
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof AffectedTile))
            return false;

        final AffectedTile other = (AffectedTile) o;
        return Objects.equals(other.x , this.x) && Objects.equals(other.y, this.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return JsonUtil.getInstance().toJson(this);
    }
}
