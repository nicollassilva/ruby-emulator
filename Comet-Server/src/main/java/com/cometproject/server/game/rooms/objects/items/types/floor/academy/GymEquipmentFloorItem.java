package com.cometproject.server.game.rooms.objects.items.types.floor.academy;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;

import java.util.List;

public class GymEquipmentFloorItem extends RoomItemFloor {
    /**
     * Achievements every 60 seconds
     */
    public final double processTime = 60.0;

    public GymEquipmentFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity))
            return;

        final PlayerEntity playerEntity = ((PlayerEntity) entity);

        playerEntity.setBodyRotation(this.getRotation());
        playerEntity.setHeadRotation(this.getRotation());

        this.setTicks(RoomItemFactory.getProcessTime(this.processTime));
    }

    @Override
    public void onTickComplete() {
        final List<RoomEntity> entities = this.getEntitiesOnItem();

        for(final RoomEntity entity : entities) {
            if (!(entity instanceof PlayerEntity))
                return;

            final PlayerEntity playerEntity = ((PlayerEntity) entity);

            switch (this.getDefinition().getItemName()) {
                case "olympics_c16_trampoline":
                    playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.TRAMPOLINIST, 1);
                    break;
                case "olympics_c16_crosstrainer":
                    playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.CROSS_TRAINER, 1);
                    break;
                case "olympics_c16_treadmill":
                    playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.JOGGER, 1);
                    break;
            }
        }

        this.setTicks(RoomItemFactory.getProcessTime(this.processTime));
    }
}
