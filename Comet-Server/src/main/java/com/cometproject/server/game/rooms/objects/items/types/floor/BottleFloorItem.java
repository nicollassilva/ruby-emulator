package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.utilities.RandomInteger;


public class BottleFloorItem extends RoomItemFloor
{
    private static final int TIMEOUT = 4;

    public BottleFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger && entity != null) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity) entity;

            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId())
                    && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        this.getItemData().setData("8");
        this.sendUpdate();

        this.setTicks(RoomItemFactory.getProcessTime(TIMEOUT / 2));
        return true;
    }

    @Override
    public void onTickComplete() {
        final int randomInteger = RandomInteger.getRandom(0, 8);

        this.getItemData().setData(randomInteger + "");
        this.sendUpdate();
    }

    @Override
    public void onPlaced() {
        this.getItemData().setData("0");
    }
}
