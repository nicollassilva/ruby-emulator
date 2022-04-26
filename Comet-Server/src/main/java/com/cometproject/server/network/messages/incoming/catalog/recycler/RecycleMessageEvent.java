package com.cometproject.server.network.messages.incoming.catalog.recycler;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.game.achievements.types.Achievement;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.players.components.AchievementComponent;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.catalog.recycler.RecyclerCompleteMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.PurchaseErrorMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.items.ItemDao;
import gnu.trove.set.hash.THashSet;

import java.util.List;
import java.util.Map;

public class RecycleMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if(CatalogManager.getInstance().ecotronItem != null) {
            THashSet<PlayerItem> items = new THashSet<>();

            int count = msg.readInt();
            if(count < CometSettings.RECYCLER_VALUE) return;

            for (int i = 0; i < count; i++) {
                PlayerItem item = (PlayerItem) client.getPlayer().getInventory().getInventoryItems().values();

                if(item == null)
                    return;

                if(item.getDefinition().canRecycle()) {
                    items.add(item);
                }
            }

            if(items.size() == count) {
                for(PlayerItem item : items) {
                    client.getPlayer().getInventory().removeItem(item);

                }
            } else {
                client.getPlayer().getSession().send(new PurchaseErrorMessageComposer(0));
                return;
            }

            PlayerItem reward = ItemDao.handleRecycle(client.getPlayer(), CatalogManager.getInstance().getRandomRecyclerPrize().getId() + "");

            if(reward == null) {
                client.getPlayer().getSession().send(new PurchaseErrorMessageComposer(0));
                return;
            }

            client.getPlayer().getSession().send(new UnseenItemsMessageComposer((Map<Integer, List<Integer>>) reward));
            client.getPlayer().getSession().send(new UpdateInventoryMessageComposer());

            client.getPlayer().getAchievements().progressAchievement(AchievementType.FURNIMATIC_QUEST, 1);
        } else {
            client.getPlayer().getSession().send(new RecyclerCompleteMessageComposer(RecyclerCompleteMessageComposer.RECYCLING_CLOSED));
        }
    }
}
