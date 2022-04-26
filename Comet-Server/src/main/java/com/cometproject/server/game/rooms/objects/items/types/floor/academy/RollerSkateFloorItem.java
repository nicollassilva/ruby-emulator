package com.cometproject.server.game.rooms.objects.items.types.floor.academy;

import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;


public class RollerSkateFloorItem extends RoomItemFloor {
    public RollerSkateFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        int playerId = ((PlayerEntity) entity).getPlayerId();

        if(PlayerManager.getInstance().isOnline(playerId)) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if(session == null) {
                return;
            }

            if(session.getPlayer().onTheRollerSkate()) {
                return;
            } else {
                session.getPlayer().getQuests().progressQuest(QuestType.WALK_ON_FURNI, this.getDefinition().getSpriteId());
            }

            if (session.getPlayer() != null && session.getPlayer().getAchievements() != null) {
                session.getPlayer().setInRollerSkate(true);
            }

            // Male effect
            if(session.getPlayer().getData().getGender().equals("M")) {
                entity.applyEffect(new PlayerEffect(55, 0));

                return;
            }

            // Female effect
            entity.applyEffect(new PlayerEffect(56, 0));
        }
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

        session.getPlayer().setInRollerSkate(false);
        entity.applyEffect(null);
    }
}