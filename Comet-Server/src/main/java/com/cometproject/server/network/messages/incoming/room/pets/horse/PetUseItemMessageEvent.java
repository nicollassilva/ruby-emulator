package com.cometproject.server.network.messages.incoming.room.pets.horse;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.pets.data.PetMonsterPlantData;
import com.cometproject.server.game.rooms.objects.entities.types.MonsterPlantEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.pets.PetUpdateStatusComposer;
import com.cometproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class PetUseItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int itemId = msg.readInt();
        final int petId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        RoomItemFloor effectItem = room.getItems().getFloorItem(ItemManager.getInstance().getItemIdByVirtualId(itemId));

        if (effectItem == null) {
            return;
        }

        PetEntity petEntity = room.getEntities().getEntityByPetId(petId);

        if (petEntity == null || petEntity.getData().getOwnerId() != client.getPlayer().getId() || effectItem.getItemData().getOwnerId() != client.getPlayer().getId()) {
            return;
        }

        if (effectItem.getDefinition().getItemName().contains("saddle")) {
            petEntity.getData().setSaddled(true);
        } else if (effectItem.getDefinition().getItemName().startsWith("horse_dye")) {
            int race = Integer.parseInt(effectItem.getDefinition().getItemName().split("_")[2]);
            int raceType = race * 4 - 2;
            if (race >= 13 && race <= 16) {
                raceType = (2 + race) * 4 + 1;
            }

            petEntity.getData().setRaceId(raceType);
        } else if (effectItem.getDefinition().getItemName().startsWith("horse_hairdye")) {
            petEntity.getData().setHairDye(48 + Integer.parseInt(effectItem.getDefinition().getItemName().split("_")[2]));
        } else if (effectItem.getDefinition().getItemName().startsWith("horse_hairstyle")) {
            petEntity.getData().setHair(100 + Integer.parseInt(effectItem.getDefinition().getItemName().split("_")[2]));
        } else if(effectItem.getDefinition().getItemName().startsWith("mnstr_fert")) {
            if(petEntity instanceof MonsterPlantEntity) {
                PetMonsterPlantData monsterPlantData = ((PetMonsterPlantData) petEntity.getData());

                if(monsterPlantData == null || monsterPlantData.isFullyGrown()) {
                    return;
                }

                monsterPlantData.setBirthday(petEntity.getData().getBirthday() - PetMonsterPlantData.growTime);
                monsterPlantData.save();

                petEntity.clearStatus();
                petEntity.markNeedsUpdate(true);
                petEntity.incrementCycleCount();

                petEntity.addStatus(RoomEntityStatus.GESTURE, "spd");
                petEntity.addStatus(RoomEntityStatus.fromString("grw" + monsterPlantData.getGrowthStage()), "");

                client.getPlayer().getEntity().getRoom().getItems().removeItem(effectItem, client, false, true, true);
                client.send(new RemoveFloorItemMessageComposer(effectItem.getVirtualId(), client.getPlayer().getEntity().getRoom().getData().getOwnerId()));
                client.send(new AvatarUpdateMessageComposer(petEntity));
                client.send(new PetUpdateStatusComposer(petEntity));

                petEntity.removeStatus(RoomEntityStatus.GESTURE);
                petEntity.incrementCycleCount();
            }
        }

        petEntity.getData().savePlantsData();
        petEntity.markNeedsUpdate(true);
        room.getEntities().broadcastMessage(new HorseFigureMessageComposer(petEntity));
        room.getItems().removeItem(effectItem, client, false, true, true);
    }
}
