package com.cometproject.server.network.messages.incoming.crafting;

import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.items.crafting.CraftingMachine;
import com.cometproject.server.game.items.crafting.CraftingRecipe;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.crafting.CraftingFinalResultMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import com.cometproject.storage.api.StorageContext;
import com.cometproject.storage.api.data.Data;
import com.google.common.collect.Sets;

public class ExecuteCraftingRecipeMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final CraftingMachine machine = client.getPlayer().getLastCraftingMachine();

        if (machine == null) {
            return;
        }

        final int machineId = msg.readInt();
        final String result = msg.readString();
        final CraftingRecipe recipe = machine.getRecipeByProductData(result);

        if (recipe == null) {
            return;
        }

        for (final Integer elementId : recipe.getComponents()) {
            for (final PlayerItem item : client.getPlayer().getInventory().getInventoryItems().values()) {
                if (item.getBaseId() == elementId) {
                    client.getPlayer().getInventory().removeItem(item.getId());
                    RoomItemDao.deleteItem(item.getId());
                    break;
                }
            }
        }

        final int nIb = recipe.getResultBaseId();
        final FurnitureDefinition itemDefinition = ItemManager.getInstance().getDefinition(nIb);
        final IPlayer crafterPlayer = client.getPlayer();

        if (itemDefinition != null) {
            final Data<Long> newItem = Data.createEmpty();
            StorageContext.getCurrentContext().getRoomItemRepository().createItem(crafterPlayer.getData().getId(), nIb, "", newItem::set);

            PlayerItem playerItem = new InventoryItem(newItem.get(), nIb, "");

            client.getPlayer().getInventory().addItem(playerItem);

            crafterPlayer.getSession().send(new UpdateInventoryMessageComposer());
            crafterPlayer.getSession().send(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
        }

        if (recipe.getAchievement() != null) {
            client.getPlayer().getAchievements().progressAchievement(recipe.getAchievement(), 1);
        }
        if (recipe.getBadge() != null && !recipe.getBadge().isEmpty()) {
            client.getPlayer().getInventory().addBadge(recipe.getBadge(), true);
        }

        client.send(new CraftingFinalResultMessageComposer(true, recipe));
    }
}
