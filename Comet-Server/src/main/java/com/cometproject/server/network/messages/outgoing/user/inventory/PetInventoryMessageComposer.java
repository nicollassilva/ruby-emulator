package com.cometproject.server.network.messages.outgoing.user.inventory;

import com.cometproject.api.game.pets.IPetData;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.pets.data.PetMonsterPlantData;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.Map;


public class PetInventoryMessageComposer extends MessageComposer {
    private final Map<Integer, IPetData> pets;

    public PetInventoryMessageComposer(final Map<Integer, IPetData> pets) {
        this.pets = pets;
    }

    @Override
    public short getId() {
        return Composers.PetInventoryMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
        msg.writeInt(1);

        msg.writeInt(pets.size());

        for (IPetData data : pets.values()) {
            msg.writeInt(data.getId());
            msg.writeString(data.getName());
            msg.writeInt(data.getTypeId());
            msg.writeInt(data.getRaceId());
            msg.writeString(data.getColour());
            if (data instanceof PetMonsterPlantData) {
                msg.writeInt(0);
                msg.writeInt(5);
                msg.writeInt(4);
                msg.writeInt(((PetMonsterPlantData)data).getEyes());
                msg.writeInt(((PetMonsterPlantData)data).getEyesColor());
                msg.writeInt(3);
                msg.writeInt(((PetMonsterPlantData)data).getNose());
                msg.writeInt(((PetMonsterPlantData)data).getNoseColor());
                msg.writeInt(2);
                msg.writeInt(((PetMonsterPlantData)data).getMouth());
                msg.writeInt(((PetMonsterPlantData)data).getMouthColor());
                msg.writeInt(1);
                msg.writeInt(((PetMonsterPlantData)data).getBody().getId());
                msg.writeInt(((PetMonsterPlantData)data).getPlantColor());
                msg.writeInt(0);
                msg.writeInt(-1);
                msg.writeInt(2);
                msg.writeInt(((PetMonsterPlantData)data).getGrowthStage());
                continue;
            }
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
        }
    }
}
