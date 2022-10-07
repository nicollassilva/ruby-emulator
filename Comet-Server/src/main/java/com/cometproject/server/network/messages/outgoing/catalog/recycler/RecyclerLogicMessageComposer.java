package com.cometproject.server.network.messages.outgoing.catalog.recycler;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public class RecyclerLogicMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.RecyclerLogicMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(CatalogManager.getInstance().prizes.size());
        for(Map.Entry<Integer, THashSet<FurnitureDefinition>> map : CatalogManager.getInstance().prizes.entrySet()) {
            msg.writeInt(map.getKey());
            msg.writeInt(Integer.parseInt(Locale.get("hotel.ecotron.rarity.chance." + map.getKey())));
            msg.writeInt(map.getValue().size());
            for(FurnitureDefinition furnitureDefinition : map.getValue()) {
                msg.writeString(furnitureDefinition.getItemName());
                msg.writeInt(1);
                msg.writeString(furnitureDefinition.getType().toLowerCase());
                msg.writeInt(furnitureDefinition.getSpriteId());
            }
        }
    }
}
