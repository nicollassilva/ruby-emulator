package com.cometproject.server.game.commands.development;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class GiveBannerCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        String target = params[0];
        String bannerName = params[1];

        int targetPlayerId = PlayerDao.getIdByUsername(target);

        PlayerDao.bannerForPlayer(bannerName, targetPlayerId);

        PermissionsManager.getInstance().loadPlayerBanner();
        isExecuted(client);

    }

    @Override
    public String getPermission() {
        return "banner_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.give_banner.parameters", "%usuário% %banner%");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.give_banner.description", "Envia um banner para um usuário");
    }
}
