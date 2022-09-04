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
        if (params.length == 0) {
            sendWhisper("Digite o nick do usuário!", client);
            return;
        }

        if (params.length == 1) {
            sendWhisper("Digite o nome do banner!", client);
            return;
        }

        String target = params[0];
        String bannerName = params[1];

        int targetPlayerId = PlayerDao.getIdByUsername(target);
        if (targetPlayerId == 0) {
            sendWhisper("Este usuário não existe!", client);
            return;
        }

        final Session targetSession = NetworkManager.getInstance().getSessions().getByPlayerId(targetPlayerId);

        if (PlayerDao.userHasBanner(bannerName, targetPlayerId))
            PlayerDao.bannerForPlayer(bannerName, targetPlayerId);

        //Check if the user is online to refresh the banners
        if (targetSession != null) {
            if (!targetSession.getPlayer().getData().getPlayerBanner().containsKey(bannerName)) {
                targetSession.getPlayer().getData().loadPlayerBanners();
            } else
                sendWhisper("Este usuário já tem este banner!", client);
        }

        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "givebanner_command";
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