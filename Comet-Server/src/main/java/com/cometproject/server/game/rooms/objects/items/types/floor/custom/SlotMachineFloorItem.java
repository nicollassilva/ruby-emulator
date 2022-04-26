package com.cometproject.server.game.rooms.objects.items.types.floor.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.common.SlotMachineComposer;

public class SlotMachineFloorItem extends DefaultFloorItem {
    public SlotMachineFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        SlotMachineComposer msg = new SlotMachineComposer(this.getVirtualId(), playerEntity.getPlayer().getData().getCredits());
        playerEntity.getPlayer().getSession().send(new JavascriptCallbackMessageComposer(msg));
        return true;
    }
}
