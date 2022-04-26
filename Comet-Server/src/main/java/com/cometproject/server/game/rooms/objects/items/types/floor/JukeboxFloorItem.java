package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.utilities.attributes.Stateable;

public class JukeboxFloorItem extends RoomItemFloor implements Stateable {


    public JukeboxFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);


    }
    private RoomEntity vendingEntity;
    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {

        PlayerEntity playerEntity = (PlayerEntity) entity;
        //playerEntity.getPlayer().getSession().sendWs(new JukeboxMessage("caixa"));

        this.vendingEntity = entity;
        this.sendUpdate();
        return true;
    }

    @Override
    public void onPickup() {
        for (RoomEntity entity : this.getRoom().getEntities().getPlayerEntities()) {
            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                PlayerEntity playerEntity = (PlayerEntity) entity;
                final Session target = NetworkManager.getInstance().getSessions().getByPlayerId(playerEntity.getPlayerId());
                //target.sendWs(new JukeboxMessage("fechar"));
            }
        }

        Room room = RoomManager.getInstance().get(this.getRoom().getData().getId());
        room.PlayNext(false);
    }

    @Override
    public void onPlaced() {

        for (RoomEntity entity : this.getRoom().getEntities().getPlayerEntities()) {
            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                PlayerEntity playerEntity = (PlayerEntity) entity;
                final Session target = NetworkManager.getInstance().getSessions().getByPlayerId(playerEntity.getPlayerId());
                //target.sendWs(new JukeboxMessage("abrir"));
            }
        }

        Room room = RoomManager.getInstance().get(this.getRoom().getData().getId());
        room.PlayNext(true);
    }

    @java.lang.Override
    public boolean getState() {
        return false;
    }
}
