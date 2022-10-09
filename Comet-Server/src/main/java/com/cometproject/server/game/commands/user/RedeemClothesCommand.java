package com.cometproject.server.game.commands.user;

import com.cometproject.api.game.catalog.types.IClothingItem;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.wardrobe.FigureSetIdsMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerClothingDao;
import com.cometproject.storage.api.StorageContext;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class RedeemClothesCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] message) {
        final Map<Long, PlayerItem> playerInventoryItems = client.getPlayer().getInventory().getInventoryItems();
        if (playerInventoryItems == null || playerInventoryItems.size() == 0) {
            sendWhisper("Oops, não tem nenhum item no inventário!", client);
            return;
        }

        final List<Long> itemsToRemove = Lists.newArrayList();

        for (final PlayerItem inventoryItem : playerInventoryItems.values()) {
            if (inventoryItem == null || inventoryItem.getDefinition() == null)
                continue;

            final IClothingItem clothingItem = CatalogManager.getInstance().getClothingItems().get(inventoryItem.getDefinition().getItemName());
            if (clothingItem == null)
                continue;

            if (client.getPlayer().getWardrobe().getClothing().contains(clothingItem))
                continue;

            itemsToRemove.add(inventoryItem.getId());

            StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(inventoryItem.getId());

            client.getPlayer().getWardrobe().getClothing().add(clothingItem);

            PlayerClothingDao.redeemClothing(client.getPlayer().getId(), clothingItem.getItemName());
        }

        if (itemsToRemove.size() == 0) {
            sendWhisper("Oops, não foi encontrada nenhuma roupa no seu inventário ou você já possui!", client);
            return;
        }

        for (final long itemId : itemsToRemove) {
            client.getPlayer().getInventory().removeItem(itemId);
        }

        itemsToRemove.clear();

        client.send(new UpdateInventoryMessageComposer());

        client.send(new FigureSetIdsMessageComposer(client.getPlayer().getWardrobe().getClothing()));
        client.send(new NotificationMessageComposer("figureset.redeemed.success"));
    }

    @Override
    public String getPermission() {
        return "redeem_clothes_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.redeem_clothes.description", "Gere os seus visuais diretamente do inventário.");
    }
}