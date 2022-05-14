package com.cometproject.server.game.commands.user.settings;

import com.cometproject.api.game.players.data.types.MentionType;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.PlayerSettings;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class MentionSettingsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        final String mentionType = params[0].toLowerCase();

        final PlayerSettings playerSettings = client.getPlayer().getSettings();
        final String mentionAll = Locale.getOrDefault("mention.all", "todos");
        final String mentionFriends = Locale.getOrDefault("mention.friends", "amigos");

        if (mentionType.equals(mentionAll)) {
            playerSettings.setMentionType(MentionType.ALL);
            sendNotif(Locale.getOrDefault("mention.all.set", "As menções serão recebidas de todos!"), client);
        } else if (mentionType.equals(mentionFriends)) {
            playerSettings.setMentionType(MentionType.FRIENDS);
            sendNotif(Locale.getOrDefault("mention.friends.set", "As menções serão recebidas apenas de amigos."), client);
        } else {
            playerSettings.setMentionType(MentionType.NONE);
            sendNotif(Locale.getOrDefault("mention.friends.set", "As menções estão desativadas."), client);
        }

        PlayerDao.saveMentionType(client.getPlayer().getId(), playerSettings.getMentionType());
    }

    @Override
    public String getPermission() {
        return "mentionsettings_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.mentionsettings.param", "tipo (todos, amigos, nadie)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.mentionsettings.description", "Establece quienes pueden mencionarte");
    }
}