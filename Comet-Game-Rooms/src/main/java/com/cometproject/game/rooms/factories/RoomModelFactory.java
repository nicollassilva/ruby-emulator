package com.cometproject.game.rooms.factories;

import com.cometproject.api.game.rooms.models.*;
import com.cometproject.api.utilities.ModelUtils;
import com.cometproject.game.rooms.models.RoomModel;
import org.apache.logging.log4j.LogManager;

public class RoomModelFactory implements IRoomModelFactory {
    @Override
    public IRoomModel createModel(RoomModelData roomModelData) throws InvalidModelException {
        final String[] axes = roomModelData.getHeightmap().split("\r");

        if (axes.length == 0) throw new InvalidModelException();

        final int mapSizeX = axes[0].length();
        final int mapSizeY = axes.length;
        final int[][] tileHeights = new int[mapSizeX][mapSizeY];
        final RoomTileState[][] tileStates = new RoomTileState[mapSizeX][mapSizeY];
        StringBuilder map = new StringBuilder(roomModelData.getHeightmap().length());
        int maxTileHeight = 0;

        try {
            for (int y = 0; y < mapSizeY; y++) {
                final char[] line = axes[y].replace("\r", "").replace("\n", "").toCharArray();

                int x = 0;
                for (final char tile : line) {
                    if (x >= mapSizeX) {
                        throw new InvalidModelException();
                    }

                    final boolean isDoor = (x == roomModelData.getDoorX() && y == roomModelData.getDoorY());
                    if (String.valueOf(tile).equals("x")) {
                        tileStates[x][y] =  isDoor ? RoomTileState.VALID : RoomTileState.INVALID;
                    } else {
                        tileStates[x][y] = RoomTileState.VALID;
                        tileHeights[x][y] = ModelUtils.getHeight(tile);
                        if (tileHeights[x][y] > maxTileHeight) {
                            maxTileHeight = (int) Math.ceil(tileHeights[x][y]);
                        }
                    }

                    x++;
                }
            }

            tileHeights[roomModelData.getDoorX()][roomModelData.getDoorY()] = roomModelData.getDoorZ();
            for (final String mapLine : roomModelData.getHeightmap().split("\r\n")) {
                if (mapLine.isEmpty()) {
                    continue;
                }

                map.append(mapLine).append((char) 13);
            }
        } catch (Exception e) {
            if (e instanceof InvalidModelException) {
                throw e;
            }

            LogManager.getLogger(RoomModelFactory.class.getName()).error("Failed to parse heightmap for model: " + roomModelData.getName(), e);
        }

        if (maxTileHeight >= 29) {
            roomModelData.setWallHeight(15);
        }

        return new RoomModel(roomModelData, tileStates, map.toString(), tileHeights);
    }
}
