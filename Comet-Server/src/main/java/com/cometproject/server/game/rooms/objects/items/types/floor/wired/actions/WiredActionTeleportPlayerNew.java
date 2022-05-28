package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;


public class WiredActionTeleportPlayerNew extends WiredActionItem {

    public WiredActionTeleportPlayerNew(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public final boolean evaluate(final RoomEntity entity, final Object data) {
        if (this.hasTicks()) {
            return false;
        }

        final WiredItemEvent itemEvent = new WiredItemEvent(entity, data);

        if (this.getWiredData().getDelay() >= 1) {
            itemEvent.setTotalTicks(RoomItemFactory.getProcessTime(this.getWiredData().getDelay() / 2F));
            this.setTicks(RoomItemFactory.getProcessTime(this.getWiredData().getDelay() / 2F));

            this.queueEvent(itemEvent);
        } else {
            itemEvent.onCompletion(this);

            this.onEventComplete(itemEvent);
        }

        return true;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (event.entity == null) {
            return;
        }

        if ((!(event.entity instanceof PlayerEntity) && this.getWiredData() == null) || this.getWiredData().getSelectedIds() == null || this.getWiredData().getSelectedIds().isEmpty()) {
            event.entity = null;
            return;
        }

        final Long itemId = WiredUtil.getRandomElement(this.getWiredData().getSelectedIds());

        if (itemId == null) {
            event.entity = null;
            return;
        }

        final RoomItemFloor item = this.getRoom().getItems().getFloorItem(itemId);

        if (item == null || item.isAtDoor() || item.getPosition() == null || item.getTile() == null) {
            event.entity = null;
            return;
        }

        event.entity.teleportToItemImmediately(item);
        event.entity = null;
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