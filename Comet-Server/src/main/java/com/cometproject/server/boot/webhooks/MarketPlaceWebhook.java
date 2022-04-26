package com.cometproject.server.boot.webhooks;

import com.cometproject.api.game.players.data.IPlayerData;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.boot.DiscordIntegration;
import com.cometproject.server.config.Locale;

import java.util.HashMap;
import java.util.Map;

public class MarketPlaceWebhook {
    public static void send(PlayerItem item, IPlayerData buyerPlayer, IPlayerData sellerPlayer, int price) {
        final Map<String, String> fields = new HashMap<>();

        fields.put(
                "\u200B",
                "**Item:**\\n"
                + item.getDefinition().getPublicName() +
                "\\n**Valor:**\\n"
                + price + " diamantes" +
                "\\n**Comprador:**\\n"
                + buyerPlayer.getUsername() +
                "\\n**Vendedor:**\\n"
                + sellerPlayer.getUsername()
        );

        DiscordIntegration.getInstance().sendWebhookFromNameWithThumbnail(
                Locale.getOrDefault("webhook.marketplace.title", "Novo item comprado na Feira Livre"),
                "marketplace",
                fields,
                "https://rubyhotel.com.br/ms-swf/dcr/hof_furni/icons/" + item.getDefinition().getItemName().replace("*", "_") + "_icon.png");
    }
}
