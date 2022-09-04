package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.players.types.PlayerBanner;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class BannerCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            sendWhisper("Digite o nome do banner!", client);
            return;
        }

        final String banner = params[0];

        if (client.getPlayer().getData().getPlayerBanner().containsKey(banner)) {
            final PlayerBanner playerBanner = client.getPlayer().getData().getPlayerBanner().get(banner);
            if (!playerBanner.isEnabled()) {
                sendWhisper("Este banner não está ativo!", client);
                return;
            }

            client.getPlayer().getData().setBanner(banner);
            client.getPlayer().getData().save();

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(client.getPlayer().getEntity()));
            client.send(new UpdateInfoMessageComposer(-1, client.getPlayer().getEntity()));

            isExecuted(client);
        } else
            sendWhisper("Você não tem este banner!", client);
    }

    @Override
    public String getPermission() {
        return "banner_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.banner.parameters", "(banner)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.banner.description", "Pon un banner al fondo de tu usuario");
    }
}
