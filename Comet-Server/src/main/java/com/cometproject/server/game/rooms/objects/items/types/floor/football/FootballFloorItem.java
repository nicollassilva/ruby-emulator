package com.cometproject.server.game.rooms.objects.items.types.floor.football;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.RollableFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.BallonFootBall;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.tasks.CometThreadManager;

import java.util.concurrent.TimeUnit;


public class FootballFloorItem extends RoomItemFloor {
    public FootballFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public RoomEntity getPusher() {
        return this.pusher;
    }

    private RoomEntity pusher;
    @Override
    public void onEntityPostStepOn(RoomEntity entity) {

        this.pusher = entity;
        CometThreadManager.getInstance().executeScheduleBall(new BallonFootBall(this, (PlayerEntity) entity, entity.getProcessingPath().size() == 0), 50, TimeUnit.MILLISECONDS);

    }



}
