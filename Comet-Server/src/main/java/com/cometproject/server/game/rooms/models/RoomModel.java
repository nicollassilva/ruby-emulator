package com.cometproject.server.game.rooms.models;

import com.cometproject.api.game.rooms.models.InvalidModelException;
import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.utilities.ModelUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class RoomModel {
    private final String name;
    private String map = "";
    private int doorX;
    private final int doorY;
    private int doorZ;
    private final int doorRotation;
    private final int mapSizeX;
    private final int mapSizeY;
    private final int[][] squareHeight;
    private final RoomTileState[][] squareState;
    private int wallHeight;

    public RoomModel(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation, int wallHeight) throws InvalidModelException {
        this.name = name;
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.doorRotation = doorRotation;
        this.wallHeight = wallHeight;

        final String[] axes = heightmap.split("\r");
        if (axes.length == 0) throw new InvalidModelException();

        this.mapSizeX = axes[0].length();
        this.mapSizeY = axes.length;
        this.squareHeight = new int[mapSizeX][mapSizeY];
        this.squareState = new RoomTileState[mapSizeX][mapSizeY];

        StringBuilder mapBuilder = new StringBuilder(mapSizeX * mapSizeY);
        int maxTileHeight = 0;
        try {
            for (int y = 0; y < mapSizeY; y++) {
                final char[] line = axes[y].replace("\r", "").replace("\n", "").toCharArray();
                int x = 0;

                for (char tile : line) {
                    if (x >= mapSizeX) {
                        throw new InvalidModelException();
                    }

                    final String tileVal = String.valueOf(tile);
                    final boolean isDoor = (x == doorX && y == doorY);
                    if (tileVal.equals("x")) {
                        squareState[x][y] = isDoor ? RoomTileState.VALID : RoomTileState.INVALID;
                    } else {
                        squareState[x][y] = RoomTileState.VALID;
                        squareHeight[x][y] = ModelUtils.getHeight(tile);
                        if (squareHeight[x][y] > maxTileHeight) {
                            maxTileHeight = (int) Math.ceil(squareHeight[x][y]);
                        }

                    }

                    x++;
                }
            }

            squareHeight[doorX][doorY] = doorZ;
            for (final String mapLine : heightmap.split("\r\n")) {
                if (mapLine.isEmpty()) continue;

                mapBuilder.append(mapLine).append((char) 13);
            }

            map = mapBuilder.toString();
        } catch (Exception e) {
            if (e instanceof InvalidModelException) {
                throw e;
            }

            LogManager.getLogger(RoomModel.class.getName()).error("Failed to parse heightmap for model: " + this.name, e);
        }

        if (maxTileHeight >= 29) {
            this.wallHeight = 15;
        }
    }

    public String getId() {
        return this.name;
    }

    public String getMap() {
        return this.map;
    }

    public int getDoorX() {
        return this.doorX;
    }

    public void setDoorX(int doorX) {
        this.doorX = doorX;
    }

    public int getDoorY() {
        return this.doorY;
    }

    public int getDoorZ() {
        return this.doorZ;
    }

    public void setDoorZ(int doorZ) {
        this.doorZ = doorZ;
    }

    public int getDoorRotation() {
        return this.doorRotation;
    }

    public int getSizeX() {
        return this.mapSizeX;
    }

    public int getSizeY() {
        return this.mapSizeY;
    }

    public RoomTileState[][] getSquareState() {
        return this.squareState;
    }

    public int[][] getSquareHeight() {
        return this.squareHeight;
    }

    public int getWallHeight() {
        return wallHeight;
    }
}
