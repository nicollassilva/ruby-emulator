package com.cometproject.server.boot.webhooks;

import com.cometproject.server.boot.DiscordIntegration;
import com.cometproject.server.config.Locale;

import java.util.HashMap;
import java.util.Map;

public class BadgeWebhook {
    public static void send(String staffUsername, String username, String code) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** enviou o emblema **" + code + "** para **" + username + "**.");

        DiscordIntegration.getInstance().sendWebhookFromNameWithThumbnail(
                Locale.getOrDefault("webhook.badge.send", "Um novo emblema foi enviado"),
                "badge", fields,
                "https://rubyhotel.com.br/apifiles/badges/" + code);
    }

    public static void sendAll(String staffUsername, String code) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** enviou o emblema **" + code + "** para **todos** os usuários onlines.");

        DiscordIntegration.getInstance().sendWebhookFromNameWithThumbnail(
                Locale.getOrDefault("webhook.badge.mass", "Um novo emblema foi enviado para todos"),
                "badge", fields,
                "https://rubyhotel.com.br/apifiles/badges/" + code);
    }

    public static void sendRemoval(String staffUsername, String username, String code) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** retirou o emblema **" + code + "** de **" + username + "**.");

        DiscordIntegration.getInstance().sendWebhookFromNameWithThumbnail(
                Locale.getOrDefault("webhook.badge.remove", "Um emblema foi removido"),
                "badge", fields,
                "https://rubyhotel.com.br/apifiles/badges/" + code);
    }
}
