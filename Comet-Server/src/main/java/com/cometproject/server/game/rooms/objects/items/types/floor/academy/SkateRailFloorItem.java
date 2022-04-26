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

        int playerId = ((PlayerEntity) entity).getPlayerId();

        if(PlayerManager.getInstance().isOnline(playerId)) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session == null || session.getPlayer() == null || session.getPlayer().getAchievements() == null) {
                return;
            }

            if(session.getPlayer().getEntity().getCurrentEffect().getEffectId() != this.effectId) {
                entity.applyEffect(null);
                entity.applyEffect(new PlayerEffect(this.effectId, 0));
            }

            // TODO: Otimizar isso (talvez enviar o progresso ao banco de dados somente quando o usuário sair do mobi)
            session.getPlayer().getAchievements().progressAchievement(AchievementType.SKATEBOARD_JUMP, 1);
        }
    }

    @Override
    public void onEntityPostStepOn(RoomEntity entity) {
        int randomRotation = this.getRandomNumber(0, 7);

        entity.setBodyRotation(randomRotation);
        entity.setHeadRotation(randomRotation);

        // Aumente isso caso queira aumentar a porcentagem de ganho da conquista SKATEBOARD_SLIDE
        if(randomRotation < 2) {
            // TODO: Otimizar isso (talvez enviar o progresso ao banco de dados somente quando o usuário sair do mobi)
            ((PlayerEntity) entity).getPlayer().getAchievements().progressAchievement(AchievementType.SKATEBOARD_SLIDE, 1);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
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

        entity.applyEffect(null);
    }
}