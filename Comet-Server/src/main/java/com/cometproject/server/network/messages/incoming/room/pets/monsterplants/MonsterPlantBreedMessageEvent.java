package com.cometproject.server.network.messages.incoming.room.pets.monsterplants;

import com.cometproject.server.game.rooms.objects.entities.types.MonsterPlantEntity;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class MonsterPlantBreedMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int unknownInt = msg.readInt(); // Something state. 2 = accept

        if(unknownInt == 0) {
            final MonsterPlantEntity petOne = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlantId(msg.readInt());
            final MonsterPlantEntity petTwo = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlantId(msg.readInt());


            if(petOne == null || petTwo == null || petOne == petTwo) {
                //TODO Add error
                return;
            }

            if(petOne instanceof MonsterPlantEntity && petTwo instanceof MonsterPlantEntity) {
                petOne.breed(petTwo);
            }

        }
    }
}

