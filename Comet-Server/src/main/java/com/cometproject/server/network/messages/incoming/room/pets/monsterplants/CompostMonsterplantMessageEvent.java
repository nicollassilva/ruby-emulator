package com.cometproject.server.network.messages.incoming.room.pets.monsterplants;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.items.types.ItemDefinition;
import com.cometproject.server.game.pets.data.PetMonsterPlantData;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.game.rooms.objects.entities.types.MonsterPlantEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.storage.api.StorageContext;
import com.cometproject.storage.api.data.Data;

public class CompostMonsterplantMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int petId = msg.readInt();

        final Room room = client.getPlayer().getEntity().getRoom();
        RoomItemFloor effectItem = room.getItems().getFloorItem(ItemManager.getInstance().getItemIdByVirtualId(886734987));
        final MonsterPlantEntity monsterPlant = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlantId(petId);

        if(monsterPlant != null) {
            if(monsterPlant instanceof MonsterPlantEntity) {
                if(((PetMonsterPlantData) monsterPlant.getData()).isDead()) {
                    if(effectItem != null) {

                    }
                }
            }
        }

    }
}
