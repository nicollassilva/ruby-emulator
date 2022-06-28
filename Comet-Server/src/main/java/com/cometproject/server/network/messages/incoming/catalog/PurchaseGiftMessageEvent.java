package com.cometproject.server.network.messages.incoming.catalog;

import com.cometproject.api.game.catalog.types.ICatalogOffer;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.crafting.CraftingDao;


public class PurchaseGiftMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int pageId = msg.readInt();
        int itemId = msg.readInt();

        if(client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1,1));
            return;
        }

        if (pageId <= 0) {
            final ICatalogOffer catalogOffer = CatalogManager.getInstance().getCatalogOffers().get(itemId);

            if (catalogOffer == null) {
                return;
            }

            pageId = catalogOffer.getCatalogPageId();
            itemId = catalogOffer.getCatalogItemId();
        }

        String extraData = msg.readString();

        String sendingUser = msg.readString();
        String message = msg.readString();
        int spriteId = msg.readInt();
        int wrappingPaper = msg.readInt();
        int decorationType = msg.readInt();
        boolean showUsername = msg.readBoolean();

        if (!CatalogManager.getInstance().getGiftBoxesOld().contains(spriteId) && !CatalogManager.getInstance().getGiftBoxesNew().contains(spriteId)) {
            client.disconnect();
            return;
        }

        GiftData data = new GiftData(pageId, itemId, client.getPlayer().getId(), sendingUser, message, spriteId, wrappingPaper, decorationType, showUsername, extraData);

        CatalogManager.getInstance().getPurchaseHandler().purchaseItem(client, pageId, itemId, extraData, 1, data);
    }
}
