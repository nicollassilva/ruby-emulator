package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;

public class GenericSmallScoreFloorItem extends RoomItemFloor {
    public final int SCORE_LIMIT = 99;

    public GenericSmallScoreFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public void sendUpdate() {
        this.getItemData().setData(this.getItemData().getData());

        super.sendUpdate();
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTriggered) {
        if (isWiredTriggered) {
            requestData = 2;
        } else {
            if(!(entity instanceof PlayerEntity))
                return false;

            final PlayerEntity pEntity = (PlayerEntity) entity;

            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId())
                    && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        final int currentScore = Integer.parseInt(this.getItemData().getData());

        switch(requestData) {
            case 1:
                this.decrement(currentScore);
                break;
            case 2:
                this.increment(currentScore);
                break;
        }

        this.sendUpdate();
        this.saveData();

        return true;
    }

    public void increment(int currentScore) {
        this.getItemData().setData(
                currentScore >= this.getScoreLimit() ? 0 : currentScore + 1
        );
    }

    public void decrement(int currentScore) {
        this.getItemData().setData(
                currentScore > 0 ? currentScore - 1 : 0
        );
    }

    public void reset() {
        this.getItemData().setData(0 + "");
        this.sendUpdate();
    }

    public int getScoreLimit() {
        return SCORE_LIMIT;
    }
}
