package com.cometproject.server.game.commands.user.settings;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.PlayerSettings;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class ToggleEventsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final PlayerSettings playerSettings = client.getPlayer().getSettings();
        if (playerSettings == null)
            return;

        playerSettings.setIgnoreEvents(!playerSettings.ignoreEvents());

        PlayerDao.saveIgnoreEvents(playerSettings.ignoreEvents(), client.getPlayer().getId());

        sendNotif(Locale.getOrDefault("command.toggleevents.msg." + playerSettings.ignoreEvents(), playerSettings.ignoreEvents() ? "Agora você está ignorando as notificações de eventos!" : "As notificações de eventos agora estão ativadas."), client);
    }

    @Override
    public String getPermission() {
        return "ignoreevents_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.toggleevents.description", "Activa o desactiva las invitaciones a eventos en el hotel");
    }
}