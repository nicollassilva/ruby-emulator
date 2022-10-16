package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;

import java.util.Random;


public class DiceFloorItem extends RoomItemFloor {
    private boolean isInUse = false;
    private int rigNumber = -1;

    public DiceFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        PlayerEntity playerEntity = ((PlayerEntity) entity);

        if (!isWiredTrigger) {
            if (!this.getPosition().touching(entity.getPosition())) {
                if(!playerEntity.isFreeze() || !playerEntity.usingTeleportItem()) {
                    entity.moveTo(this.getPosition().squareInFront(this.getRotation()).getX(), this.getPosition().squareBehind(this.getRotation()).getY());
                    return false;
                }
            }
        }

        if (this.isInUse) {
            return false;
        }

        if (requestData >= 0) {
            if (!"-1".equals(this.getItemData().getData())) {
                if (this.getPosition().touching(entity.getPosition())) {
                    this.getItemData().setData("-1");
                    this.sendUpdate();

                    this.isInUse = true;

                    if (playerEntity.hasAttribute("diceRoll")) {
                        this.rigNumber = (int) playerEntity.getAttribute("diceRoll");
                        playerEntity.removeAttribute("diceRoll");
                    }

                    this.setTicks(RoomItemFactory.getProcessTime(1.5));
                }
            }
        } else {
            this.getItemData().setData("0");
            this.sendUpdate();

            this.saveData();
        }

        return true;
    }

    @Override
    public void onPlaced() {
        if (!"0".equals(this.getItemData().getData())) {
            this.getItemData().setData("0");
        }
    }

    @Override
    public void onPickup() {
        this.cancelTicks();
    }

    @Override
    public void onTickComplete() {
        final int num = new Random().nextInt(this.getDefinition().getInteractionCycleCount()) + 1;

        this.getItemData().setData(Integer.toString(this.rigNumber == -1 ? num : this.rigNumber));
        this.sendUpdate();

        this.saveData();

        if (this.rigNumber != -1) this.rigNumber = -1;

        this.isInUse = false;
    }
}
