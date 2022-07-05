package com.cometproject.server.game.rooms.objects.items.types.floor.traxmachine;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.items.music.TraxMachineSong;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TraxMachineFloorItem extends RoomItemFloor {
    public TraxMachineFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (isWiredTrigger || !(entity instanceof PlayerEntity)) {
            return false;
        }

        final PlayerEntity playerEntity = (PlayerEntity) entity;

        if(this.getRoom().getData().getOwnerId() != playerEntity.getPlayer().getId()) {
            return false;
        }

        this.sendTraxMachineWindow(entity);
        this.toggleInteract(true);
        this.sendUpdate();

        return true;
    }


    private void sendTraxMachineWindow(RoomEntity entity) {
        try {
            final Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenTraxMachineWindowMessage);
            final OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();
            final TraxMachineSong song = RoomManager.getInstance().getTraxMachineSongFromUserAndSongId(this.getRoom().getData().getOwnerId(), this.getRoom().getData().getSongId());

            message.client = ((PlayerEntity) entity).getPlayer().getData().getWebsocketSession();
            message.data = new JSONObject();

            if(song != null) {
                message.data.put("songData", song.getData());
            }

            message.compose();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
