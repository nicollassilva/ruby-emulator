package com.cometproject.storage.mysql.models.factories.rooms;

import com.cometproject.api.game.rooms.models.CustomFloorMapData;
import com.cometproject.api.game.rooms.models.RoomModelData;

public class RoomModelDataFactory {

    public  static final RoomModelDataFactory instance = new RoomModelDataFactory();

    public RoomModelData createData(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation, int wallHeight) {
        return new RoomModelData(name, heightmap, doorX, doorY, doorZ, doorRotation, wallHeight);
    }

    public RoomModelData createData(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation){
        return createData(name, heightmap, doorX, doorY, doorZ, doorRotation, -1);
    }

    public RoomModelData createData(CustomFloorMapData customFloorData) {
        return createData("dynamic_heightmap",
                customFloorData.getModelData(),
                customFloorData.getDoorX(),
                customFloorData.getDoorY(),
                customFloorData.getDoorZ(),
                customFloorData.getDoorRotation(),
                customFloorData.getWallHeight()
        );
    }

}
