package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class OverrideCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final boolean isOverriden = !client.getPlayer().getEntity().isOverriden();

        client.getPlayer().getEntity().setOverrideA(isOverriden);
        client.getPlayer().getEntity().setOverriden(isOverriden);

        if(isOverriden)
            sendNotif(Locale.getOrDefault("command.override.enable", "El Override está activado !"), client);
        else
            sendNotif(Locale.getOrDefault("command.override.disable", "El Override está desactivado !"), client);
    }

    @Override
    public String getPermission() {
        return "override_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.override.description", "Camina por sobre furnis que tengan prohibido el caminar sobre ellos");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
