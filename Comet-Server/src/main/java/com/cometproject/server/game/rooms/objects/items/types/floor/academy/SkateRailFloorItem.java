package com.cometproject.server.game.rooms.objects.items.types.floor.academy;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.utilities.RandomUtil;

public class SkateRailFloorItem extends RoomItemFloor {
    private final int effectId;

    public SkateRailFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        this.effectId = this.getDefinition().getEffectId();
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        final int playerId = ((PlayerEntity) entity).getPlayerId();

        if(PlayerManager.getInstance().isOnline(playerId)) {
            final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session == null || session.getPlayer() == null || session.getPlayer().getAchievements() == null) {
                return;
            }

            if(session.getPlayer().getEntity().getCurrentEffect().getEffectId() != this.effectId) {
                entity.applyEffect(null);
                entity.applyEffect(new PlayerEffect(this.effectId, 0));
            }

            session.getPlayer().getAchievements().progressAchievement(AchievementType.SKATEBOARD_JUMP, 1);
        }
    }

    @Override
    public void onEntityPostStepOn(RoomEntity entity) {
        final int randomRotation = RandomUtil.getRandomInt(0, 7);

        entity.setBodyRotation(randomRotation);
        entity.setHeadRotation(randomRotation);

        if(randomRotation < 2) {
            ((PlayerEntity) entity).getPlayer().getAchievements().progressAchievement(AchievementType.SKATEBOARD_SLIDE, 1);
        }
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        int playerId = ((PlayerEntity) entity).getPlayerId();
        final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if(session == null || session.getPlayer() == null) {
            return;
        }

        entity.applyEffect(null);
    }
}