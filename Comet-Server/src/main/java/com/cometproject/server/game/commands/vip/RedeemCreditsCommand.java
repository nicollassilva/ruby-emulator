package com.cometproject.server.game.commands.vip;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.storage.api.StorageContext;
import com.google.common.collect.Lists;

import java.util.List;


public class RedeemCreditsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        int coinsToGive = 0;
        int diamondsToGive = 0;
        int rubisToGive = 0;

        final List<Long> itemsToRemove = Lists.newArrayList();

        if (!client.getPlayer().getInventory().itemsLoaded()) {
            sendNotif(Locale.getOrDefault("command.redeemcredits.inventory", "Abra seu inventário antes de executar o comando!"), client);
            return;
        }

        for (final PlayerItem playerItem : client.getPlayer().getInventory().getInventoryItems().values()) {
            if (playerItem == null || playerItem.getDefinition() == null) continue;

            final String itemName = playerItem.getDefinition().getItemName();

            if (itemName.startsWith("CF_") || itemName.startsWith("CFC_")) {
                try {
                    if (itemName.contains("_diamond_")) {
                        diamondsToGive += Integer.parseInt(itemName.split("_diamond_")[1]);
                    } else if(itemName.contains("_ruby_")) {
                        rubisToGive += Integer.parseInt(itemName.split("_ruby_")[1]);
                    } else {
                        coinsToGive += Integer.parseInt(itemName.split("_")[1]);
                    }

                    itemsToRemove.add(playerItem.getId());

                    StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(playerItem.getId());
                } catch (Exception ignored) {

                }
            }
        }

        if (itemsToRemove.size() == 0) {
            return;
        }

        for (final long itemId : itemsToRemove) {
            client.getPlayer().getInventory().removeItem(itemId);
        }

        itemsToRemove.clear();

        client.send(new UpdateInventoryMessageComposer());

        if (diamondsToGive > 0) {
            client.getPlayer().getData().increaseVipPoints(diamondsToGive);
        }

        if(rubisToGive > 0) {
            client.getPlayer().getData().increaseSeasonalPoints(rubisToGive);
        }

        if (coinsToGive > 0) {
            client.getPlayer().getData().increaseCredits(coinsToGive);
        }

        if (diamondsToGive > 0 || coinsToGive > 0 || rubisToGive > 0) {
            client.getPlayer().sendBalance();
            client.getPlayer().getData().save();
        }
    }


    @Override
    public String getPermission() {
        return "redeemcredits_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.redeemcredits.description", "Canjea los furnis créditos, rubis, diamantes o moneda de temporada a tu monedero");
    }
}
