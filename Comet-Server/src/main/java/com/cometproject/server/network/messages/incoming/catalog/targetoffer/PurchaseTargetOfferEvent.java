package com.cometproject.server.network.messages.incoming.catalog.targetoffer;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.ITargetOffer;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.TargetOffer;
import com.cometproject.server.game.players.PlayerOfferPurchase;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class PurchaseTargetOfferEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int offerId = msg.readInt();
        int amount = msg.readInt();

        if (amount <= 0) return;

        int timeSinceLastUpdate = ((int) Comet.getTime()) - client.getPlayer().getLastPurchase();

        if(timeSinceLastUpdate >= CometSettings.playerPurchaseCooldown) {
            client.getPlayer().setLastPurchase((int) Comet.getTime());

            ITargetOffer offer = CatalogManager.getInstance().getTargetOffer(offerId);
            IPlayerOfferPurchase purchase = PlayerOfferPurchase.getOrCreate(client.getPlayer(), offerId);

            amount = Math.min(offer.getPurchaseLimit() - purchase.getAmount(), amount);
            int now = (int) Comet.getTime();

            if(TargetOffer.ACTIVE_TARGET_OFFER_ID != offer.getId()) {
                client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("commands.cmd_promote_offer.expired", "Essa oferta não está mais disponível!")));
                return;
            }

            if(purchase.getAmount() >= offer.getPurchaseLimit()) {
                client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("commands.cmd_promote_offer.limit_exceeded", "Você excedeu o limite de compras para essa oferta.")));
                return;
            }

            if(offer.getExpirationTime() > now) {
                purchase.update(amount, now);

                ICatalogItem item = CatalogManager.getInstance().getCatalogItem(offer.getCatalogItem());

                if(item.getLimitedTotal() > 0) {
                    amount = 1;
                }

                CatalogManager.getInstance().getPurchaseHandler().purchaseItem(client, item.getPageId(), item.getId(), "", amount, null);
            }
        }

    }
}
