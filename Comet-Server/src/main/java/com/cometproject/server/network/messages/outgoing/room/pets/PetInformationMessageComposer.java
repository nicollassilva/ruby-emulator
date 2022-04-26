package com.cometproject.server.network.messages.outgoing.room.pets;

import com.cometproject.api.game.players.data.PlayerAvatar;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.pets.data.PetMonsterPlantData;
import com.cometproject.server.game.pets.races.plants.PetMonsterPlant;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.Calendar;


public class PetInformationMessageComposer extends MessageComposer {

    private final PetEntity petEntity;
    private final PlayerEntity player;

    public PetInformationMessageComposer(final PetEntity petEntity) {
        this.petEntity = petEntity;
        this.player = null;
    }

    public PetInformationMessageComposer(final PlayerEntity playerEntity) {
        this.petEntity = null;
        this.player = playerEntity;
    }

    @Override
    public short getId() {
        return Composers.PetInformationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (this.petEntity != null && this.petEntity.getData() != null) {
            boolean isMonsterPlan = this.petEntity.getData() instanceof PetMonsterPlantData;

            this.petEntity.composeInformation(msg);
            msg.writeInt(this.petEntity.getData().getId());
            msg.writeString(this.petEntity.getData().getName());

            if(isMonsterPlan) {
                msg.writeInt(((PetMonsterPlantData) this.petEntity.getData()).getGrowthStage());
                msg.writeInt(7);
            } else {
                msg.writeInt(this.petEntity.getData().getLevel());
                msg.writeInt(20); // MAX_LEVEL
            }

            msg.writeInt(this.petEntity.getData().getExperience());
            msg.writeInt(this.petEntity.getData().getExperienceGoal());
            msg.writeInt(this.petEntity.getData().getEnergy());
            msg.writeInt(100); // MAX_ENERGY
            msg.writeInt(this.petEntity.getData().getHappiness()); // NUTRITION
            msg.writeInt(100); // MAX_NUTRITION
            msg.writeInt(this.petEntity.getData().getScratches()); // SCRATCHES
            msg.writeInt(this.petEntity.getData().getOwnerId());
            msg.writeInt(this.daysSinceBirthday(this.petEntity.getData().getBirthday())); // AGE
            msg.writeString(PlayerManager.getInstance().getAvatarByPlayerId(this.petEntity.getData().getOwnerId(), PlayerAvatar.USERNAME_FIGURE).getUsername());
            msg.writeInt(isMonsterPlan ? ((PetMonsterPlantData) this.petEntity.getData()).getRarity() : 0);
            msg.writeBoolean(this.petEntity.getData().isSaddled()); // HAS_SADDLE
            msg.writeBoolean(this.petEntity.hasMount()); // HAS_RIDER
            msg.writeInt(0);
            msg.writeInt(this.petEntity.getData().isAnyRider() ? 1 : 0); // yes = 1 no = 0
            msg.writeBoolean(isMonsterPlan && ((PetMonsterPlantData) this.petEntity.getData()).canBreed()); // State Grown
            msg.writeBoolean(!isMonsterPlan || ((PetMonsterPlantData) this.petEntity.getData()).isFullyGrown()); // unknown 1
            msg.writeBoolean(isMonsterPlan && ((PetMonsterPlantData) this.petEntity.getData()).isDead()); // Dead
            msg.writeInt(isMonsterPlan ? ((PetMonsterPlantData) this.petEntity.getData()).getRarity() : 0);
            msg.writeInt(isMonsterPlan ? PetMonsterPlantData.timeToLive : 0); //Maximum wellbeing
            msg.writeInt(isMonsterPlan ? ((PetMonsterPlantData) this.petEntity.getData()).remainingTimeToLive() : 0);
            msg.writeInt(this.petEntity.getData() instanceof PetMonsterPlant ? ((PetMonsterPlantData) this.petEntity.getData()).remainingGrowTime() : 0);
            msg.writeBoolean(true);
        } else {
            if(this.player == null) {
                return;
            }

            msg.writeInt(this.player.getPlayerId());
            msg.writeString(this.player.getUsername());
            msg.writeInt(20);
            msg.writeInt(20); // MAX_LEVEL
            msg.writeInt(0);
            msg.writeInt(200); // EXPERIENCE_GOAL
            msg.writeInt(0);
            msg.writeInt(100); // MAX_ENERGY
            msg.writeInt(100); // NUTRITION
            msg.writeInt(100); // MAX_NUTRITION
            msg.writeInt(0); // SCRATCHES
            msg.writeInt(this.player.getPlayerId());
            msg.writeInt(0); // AGE
            msg.writeString(this.player.getUsername());
            msg.writeInt(1);
            msg.writeBoolean(this.player.getMotto().toLowerCase().startsWith("rideable")); // HAS_SADDLE
            msg.writeBoolean(false); // HAS_RIDER
            msg.writeInt(0);

            // CAN ANYONE MOUNT?
            msg.writeInt(1); // yes = 1 no = 0

            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(false);
            msg.writeBoolean(true); // all can mount
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(false);
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeBoolean(false);

        }
    }

    private int daysSinceBirthday(long birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday * 1000L);

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(System.currentTimeMillis());

        return newCalendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
    }
}
