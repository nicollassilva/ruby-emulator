package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;

import java.util.Random;

public class RandomStateFloorItem extends DefaultFloorItem {
    private boolean isInUse = false;

    public RandomStateFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onPlaced() {
        if (!"0".equals(this.getItemData().getData())) {
            this.getItemData().setData("");
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            PlayerEntity playerEntity = ((PlayerEntity) entity);

            if (!this.getPosition().touching(entity.getPosition())) {
                if(!playerEntity.isFreeze() || !playerEntity.hasAttribute("interacttpencours") || !playerEntity.hasAttribute("tptpencours")) {
                    entity.moveTo(this.getPosition().squareInFront(this.getRotation()).getX(), this.getPosition().squareBehind(this.getRotation()).getY());
                    return false;
                }
            }
        }

        if (this.isInUse) {
            return false;
        }

        this.isInUse = true;
        this.getItemData().setData("");
        this.sendUpdate();
        this.setTicks(RoomItemFactory.getProcessTime(2.5));
        return true;
    }
    @Override
    public void onPickup() {
        this.cancelTicks();
    }

    @Override
    public void onTickComplete() {
        int num = new Random().nextInt(this.getDefinition().getInteractionCycleCount()) + 1;

        this.getItemData().setData(Integer.toString(num));
        this.sendUpdate();

        this.saveData();

        this.isInUse = false;
    }

    public void onEntityStepOn(RoomEntity entity, boolean instantUpdate) {
        if(this.getDefinition().canSit())
            entity.sit(this.getDefinition().getHeight(), getRotation());

        if (instantUpdate)
            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entity));
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.onEntityStepOn(entity, false);
    }

}
