package com.cometproject.server.network.messages.outgoing.user.wardrobe;

import com.cometproject.api.game.catalog.types.IClothingItem;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.HashSet;
import java.util.Set;

public class FigureSetIdsMessageComposer extends MessageComposer {

    private final Set<String> clothing;

    public FigureSetIdsMessageComposer(final Set<String> clothing) {
        this.clothing = clothing;
    }

    @Override
    public short getId() {
        return Composers.FigureSetIdsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        final Set<Integer> parts = new HashSet<>();

        for (final String clothing : this.clothing) {
            final IClothingItem clothingItem = CatalogManager.getInstance().getClothingItems().get(clothing);

            if (clothingItem != null) {
                for (final int part : clothingItem.getParts()) {
                    parts.add(part);
                }
            }
        }

        msg.writeInt(parts.size());

        for (final int part : parts) {
            msg.writeInt(part);
        }

        msg.writeInt(this.clothing.size());

        for (final String clothing : this.clothing) {
            msg.writeString(clothing);
        }
    }
}
