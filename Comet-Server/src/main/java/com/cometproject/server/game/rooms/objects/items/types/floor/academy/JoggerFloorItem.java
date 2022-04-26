package com.cometproject.server.game.rooms.objects.items.types.floor.academy;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;


public class JoggerFloorItem extends RoomItemFloor {
    private final int effectId;

    public JoggerFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        this.effectId = this.getDefinition().getEffectId();
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        int playerId = ((PlayerEntity) entity).getPlayerId();

        if(PlayerManager.getInstance().isOnline(playerId)) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session != null && session.getPlayer() != null && session.getPlayer().getAchievements() != null) {
                session.getPlayer().setInJogger(true);
            }
        }

        entity.setBodyRotation(getRotation());
        entity.setHeadRotation(getRotation());
        entity.applyEffect(new PlayerEffect(this.effectId, 0));

        this.toggleInteract(true);
        this.sendUpdate();
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        int playerId = ((PlayerEntity) entity).getPlayerId();
        Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if(session == null || session.getPlayer() == null) {
            return;
        }

        session.getPlayer().setInJogger(false);
        entity.applyEffect(null);

        this.toggleInteract(false);
        this.sendUpdate();
    }
}