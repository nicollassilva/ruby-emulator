package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;

public class WiredCustomTeleportFront extends WiredActionItem {

    public WiredCustomTeleportFront(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (this.getWiredData() == null || this.getWiredData().getSelectedIds() == null || this.getWiredData().getSelectedIds().isEmpty()) {
            return;
        }

        Long itemId = WiredUtil.getRandomElement(this.getWiredData().getSelectedIds());

        if (itemId == null) {
            return;
        }

        RoomItemFloor item = this.getRoom().getItems().getFloorItem(itemId);

        if (item == null || item.isAtDoor() || item.getPosition() == null || item.getTile() == null) {
            return;
        }

        //Position positionFront = nearestPlayerEntity().getPosition().squareInFront(nearestPlayerEntity().getBodyRotation());

        if (event.entity == null) {
            return;
        }

        PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        //event.entity.moveTo(positionFront);

        if (this.getRoom().getItems().moveFloorItem(item.getId(), playerEntity.getPosition().squareInFront(playerEntity.getBodyRotation()), item.getRotation(), true)) {
            if(this.useItemsAnimation()) {
                this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
            } else {
                this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
                this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(item.getPosition(), playerEntity.getPosition().squareInFront(playerEntity.getBodyRotation()), this.getVirtualId(), 0, item.getVirtualId()));
            }
        }
    }


    @Override
    public int getInterface() {
        return 0;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }
}
