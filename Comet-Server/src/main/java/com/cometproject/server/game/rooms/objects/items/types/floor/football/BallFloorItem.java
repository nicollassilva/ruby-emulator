package com.cometproject.server.game.rooms.objects.items.types.floor.football;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.BallonFootBall;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.tasks.CometThreadManager;

import java.util.concurrent.TimeUnit;

public class BallFloorItem extends RoomItemFloor {

    private RoomEntity entity;

    public BallFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }


    private Position findPosition() {
        Position position = null;


        return position;
    }
}
