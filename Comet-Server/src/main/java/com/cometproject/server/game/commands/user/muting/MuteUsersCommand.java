package com.cometproject.server.game.commands.user.muting;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class MuteUsersCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final boolean usersMuted = !client.getPlayer().usersMuted();

        client.getPlayer().setUsersMuted(usersMuted);
        sendWhisper(Locale.getOrDefault("command.muteusers." + usersMuted,"Agora " + (usersMuted ? "" : "não") + " está ignorando as mensagens dos users."), client);
    }

    @Override
    public String getPermission() {
        return "muteusers_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.muteusers.description", "Ignorar a fala dos usuários no quarto.");
    }
}
