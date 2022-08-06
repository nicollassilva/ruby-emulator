package com.cometproject.server.network.messages.incoming.catalog;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.PurchaseErrorMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class PurchaseItemMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if(client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1,1));
            client.send(new PurchaseErrorMessageComposer(2));
            return;
        }

        final int pageId = msg.readInt();
        final int itemId = msg.readInt();
        final String data = msg.readString();
        final int amount = Math.min(Math.max(msg.readInt(), 1), 100);

        final int timeSinceLastUpdate = ((int) Comet.getTime()) - client.getPlayer().getLastPurchase();
        if(timeSinceLastUpdate >= CometSettings.playerPurchaseCooldown) {
            client.getPlayer().setLastPurchase((int) Comet.getTime());

            if (!client.getPlayer().getSettings().getAllowTrade()) {
                client.getPlayer().sendPopup(Locale.get("user.troc.disabled.title"), Locale.get("user.troc.disabled.action.message"));
                return;
            }

            if (client.getPlayer().antiSpam("PurchaseItemMessageEvent", 1.0)) {
                client.getPlayer().sendPopup(Locale.get("game.catalog.furni.buytoofast.title"), Locale.get("game.catalog.furni.buytoofast.message"));
                return;
            }

            CatalogManager.getInstance().getPurchaseHandler().purchaseItem(client, pageId, itemId, data, amount, null);
        }
        else{
            client.getPlayer().sendPopup(Locale.get("game.catalog.furni.buytoofast.title"), Locale.get("game.catalog.furni.buytoofast.message"));
        }
    }
}
