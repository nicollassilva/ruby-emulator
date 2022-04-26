package com.cometproject.server.boot.webhooks;

import com.cometproject.server.boot.DiscordIntegration;
import com.cometproject.server.config.Locale;

import java.util.HashMap;
import java.util.Map;

public class PunishmentWebhook {
    public static void sendBan(String staffUsername, String userBannedName, String reason, int hours) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** fez um **Banimento Comum** de **" + hours + " horas** no usuário **" + userBannedName + "** pelo motivo: **" + reason + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.ban", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendIpBan(String staffUsername, String userBannedName, String reason, int hours) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** fez um **Banimento pelo IP** de **" + hours + " horas** no usuário **" + userBannedName + "** pelo motivo: **" + reason + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.ipban", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendMachineBan(String staffUsername, String userBannedName, String reason, int hours) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** fez um **Banimento pelo Dispositivo** de **" + hours + " horas** no usuário **" + userBannedName + "** pelo motivo: **" + reason + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.machineban", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendSuperBan(String staffUsername, String userBannedName) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** fez um **Banimento Permanente** no usuário **" + userBannedName + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.superban", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendTradeBan(String staffUsername, String userBannedName, String reason, int hours) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** fez um **Banimento nas Negociações** do usuário **" + userBannedName + "** por **" + hours + " horas** pelo motivo: **" + reason + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.tradeban", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendUnBan(String staffUsername, String userUnBannedName) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** fez a **remoção do banimento** do usuário **" + userUnBannedName + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.unban", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendDisconnect(String staffUsername, String userUnBannedName) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** acabou de **desconectar** o usuário **" + userUnBannedName + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.disconnect", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendMute(String staffUsername, String username, int minutes) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** acabou de **mutar** por **" + minutes + " minutos** o usuário **" + username + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.mute", "Nova punição registrada"),
                "punishment", fields);
    }

    public static void sendUnMute(String staffUsername, String username) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**" + staffUsername + "** acabou de **desmutar** o usuário **" + username + "**.");

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.punishment.unmute", "Nova punição registrada"),
                "punishment", fields);
    }
}
