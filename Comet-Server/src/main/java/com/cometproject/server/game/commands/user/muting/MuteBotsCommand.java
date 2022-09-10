package com.cometproject.server.game.commands.user.muting;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class MuteBotsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final boolean botsMuted = !client.getPlayer().botsMuted();

        client.getPlayer().setBotsMuted(botsMuted);
        sendWhisper(Locale.get("command.mutebots." + botsMuted), client);
    }

    @Override
    public String getPermission() {
        return "mutebots_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.mutebots.description", "Mutea los bots que estén en tu sala");
    }
}
