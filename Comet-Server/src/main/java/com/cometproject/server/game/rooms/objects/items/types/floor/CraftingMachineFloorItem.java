package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.items.crafting.CraftingMachine;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.crafting.CraftableProductsMessageComposer;

public class CraftingMachineFloorItem  extends RoomItemFloor {

    public CraftingMachineFloorItem (RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (isWiredTrigger) {
            return false;
        }

        final PlayerEntity player = ((PlayerEntity) entity);

        if(player == null) {
            return false;
        }

        final CraftingMachine machine = ItemManager.getInstance().getCraftingMachine(this.getDefinition().getId());

        if(machine == null) {
            return false;
        }

        player.getPlayer().setLastCraftingMachine(machine);
        player.getPlayer().getInventory().send();
        player.getPlayer().getSession().send(new CraftableProductsMessageComposer(machine));

        return true;
    }

    @Override
    public void onPlaced() {
        this.getItemData().setData("1");
        this.sendUpdate();

        this.saveData();
    }

    @Override
    public void onTickComplete() {
    }
}