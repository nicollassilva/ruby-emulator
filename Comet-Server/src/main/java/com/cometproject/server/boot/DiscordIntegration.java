package com.cometproject.server.boot;

import com.cometproject.api.config.Configuration;
import com.cometproject.server.boot.compositions.IDiscordIntegration;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.discord.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Webhooks can be implemented by config file
 */
public class DiscordIntegration implements IDiscordIntegration {
    public static Logger log = LogManager.getLogger(DiscordIntegration.class.getName());

    private String nameLink;
    private String discordName;
    private String avatarUrl;
    public Map<String, String> webhooks = new HashMap<>();

    public static DiscordIntegration discordIntegrationInstance;

    public static DiscordIntegration getInstance() {
        if(discordIntegrationInstance == null) {
            discordIntegrationInstance = new DiscordIntegration();
        }

        return discordIntegrationInstance;
    }

    public void initialize() {
        this.loadWebhooks();
        this.setDefaultWebhookSettings();
    }

    private void loadWebhooks() {
        final String webhooks = Configuration.currentConfig().get("comet.discord.webhooks");

        if(webhooks.isEmpty()) {
            log.info("No webhook found, skipping initialize step.");
            return;
        }

        final String[] webhooksSplitted = webhooks.split("\\|");

        if(webhooksSplitted.length < 1) {
            log.info("The webhook configuration is incorrect.");
            return;
        }

        for (final String webhook : webhooksSplitted) {
            final String[] webhookData = webhook.split(";");

            if(webhookData.length < 2) continue;

            this.registerWebhook(webhookData[0], webhookData[1]);
        }

        log.info("Loaded " + this.webhooks.size() + " discord webhook(s)");
    }

    public void registerWebhook(String name, String endpoint) {
        this.webhooks.put(name, endpoint);
    }

    private void setDefaultWebhookSettings() {
        final String discordName = Configuration.currentConfig().get("comet.discord.username"),
                    discordNameLink = Configuration.currentConfig().get("comet.discord.usernameLink"),
                    discordAvatar = Configuration.currentConfig().get("comet.discord.avatarUrl");

        this.setName(discordName);
        this.setAvatar(discordAvatar);
        this.setNameLink(discordNameLink);
    }

    public void setName(String name) {
        this.discordName = name;
    }

    public void setAvatar(String avatar) {
        this.avatarUrl = avatar;
    }

    public void setNameLink(String nameLink) {
        this.nameLink = nameLink;
    }

    public String getName() {
        return this.discordName;
    }

    public String getAvatar() {
        return this.avatarUrl;
    }

    public String getDiscordNameLink() {
        return this.nameLink;
    }

    public void sendCommandWebhook(String username, String command, String message) {
        final Map<String, String> fields = new HashMap<>();

        fields.put("\u200B", "**Usuário:**\\n" + username + "\\n**Comando:**\\n" + command + "\\n**Dados do Comando:**\\n" + message);

        DiscordIntegration.getInstance().sendWebhookFromName(
                Locale.getOrDefault("webhook.command_send.title", "Um novo comando foi utilizado!"),
                "staffCommands", fields);
    }

    public void sendWebhookFromName(String title, String webhookName, Map<String, String> fields) {
        this.sendWebhook(title, webhookName, fields);
    }

    public void sendWebhookFromNameWithThumbnail(String title, String webhookName, Map<String, String> fields, String thumbnailUrl) {
        this.sendWebhookWithThumbnail(title, webhookName, fields, thumbnailUrl);
    }

    private void sendWebhook(String title, String webhookName, Map<String, String> fields) {
        if(!this.webhooks.containsKey(webhookName)) {
            log.warn("Webhook not found: " + webhookName);
            return;
        }

        final Webhook webhook = new Webhook(this.webhooks.get(webhookName));

        webhook.setUsername(this.getName());
        webhook.setAvatarUrl(this.getAvatar());

        webhook.addEmbed(new Webhook.EmbedObject()
                    .setTitle(title)
                    .addMultipleFields(fields)
                    .setColor(new Color(171, 43, 43))
                    .setFooter("Data: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime()), "")
                    .setAuthor(this.getName(), this.getDiscordNameLink(), this.getAvatar())
        );

        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendWebhookWithThumbnail(String title, String webhookName, Map<String, String> fields, String thumbnailUrl) {
        if(!this.webhooks.containsKey(webhookName)) {
            log.warn("Webhook not found: " + webhookName);
            return;
        }

        final Webhook webhook = new Webhook(this.webhooks.get(webhookName));

        webhook.setUsername(this.getName());
        webhook.setAvatarUrl(this.getAvatar());

        webhook.addEmbed(new Webhook.EmbedObject()
                .setTitle(title)
                .addMultipleFields(fields)
                .setColor(new Color(171, 43, 43))
                .setThumbnail(thumbnailUrl)
                .setFooter("Data: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime()), "")
                .setAuthor(this.getName(), this.getDiscordNameLink(), this.getAvatar())
        );

        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
