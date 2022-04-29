package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;


public class WiredActionTeleportPlayer extends WiredActionItem {

    public WiredActionTeleportPlayer(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (event.entity == null) return;

        if (!(event.entity instanceof PlayerEntity) && this.getWiredData() == null || this.getWiredData().getSelectedIds() == null || this.getWiredData().getSelectedIds().isEmpty()) {
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

        final Position position = new Position(item.getPosition().getX(), item.getPosition().getY(), item.getTile().getWalkHeight());

        if (!event.entity.getPosition().equals(position)) {
            event.entity.applyEffect(new PlayerEffect(4, 4));
            event.entity.cancelWalk();
            event.entity.warp(position);
            event.entity = null;
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