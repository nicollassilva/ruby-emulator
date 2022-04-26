package com.cometproject.server.network.messages.incoming.landing;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.landing.BonusBagMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.storage.api.StorageContext;
import com.cometproject.storage.api.data.Data;
import com.google.common.collect.Sets;

public class HotelViewRequestBonusRareMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (CometSettings.bonusBagEnabled) {
            int points = CometSettings.bonusHours;
            client.send(new BonusBagMessageComposer(CometSettings.bonusRewardName, CometSettings.bonusRewardItemId, CometSettings.bonusHours, client));
            if (client.getPlayer().getData().getBonusPoints() == points) {
                String extraData = "0";

                int itemId = CometSettings.bonusRewardItemId;

                FurnitureDefinition itemDefinition = ItemManager.getInstance().getDefinition(itemId);

                IPlayer e = client.getPlayer();

                if (itemDefinition != null) {
                    final Data<Long> newItem = Data.createEmpty();
                    StorageContext.getCurrentContext().getRoomItemRepository().createItem(e.getData().getId(), itemId, extraData, newItem::set);

                    PlayerItem playerItem = new InventoryItem(newItem.get(), itemId, extraData);

                    e.getSession().getPlayer().getInventory().addItem(playerItem);

                    e.getSession().send(new UpdateInventoryMessageComposer());
                    e.getSession().send(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
                    e.getSession().send(new NotificationMessageComposer("bonusbag.", "Has conseguido el furni " + CometSettings.bonusRewardName + " por haber completado " + CometSettings.bonusHours + " horas conectado."));
                }
            }
        }
    }
}
