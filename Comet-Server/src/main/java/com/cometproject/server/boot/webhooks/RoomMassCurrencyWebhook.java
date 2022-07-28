package com.cometproject.server.boot.webhooks;

import com.cometproject.server.boot.DiscordIntegration;
import com.cometproject.server.config.Locale;

import java.util.HashMap;
import java.util.Map;

public class RoomMassCurrencyWebhook {
    public static void send(String staffUsername, int quantity, String coinName, int roomId) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** enviou **" + quantity + " " + coinName + "** para **todos** os usuários do quarto [ID: " + roomId + "].");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.masscurrency.title", "Uma nova quantia foi enviada para todos do quarto"),
                "currency", fields);
    }
}
