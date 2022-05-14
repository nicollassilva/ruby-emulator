package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class TagProfileCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final String tag = params[0];

        client.send(new NotificationMessageComposer("tag_profile", Locale.getOrDefault("command.tag_profile.message", "Adicionou uma tag ao perfil corretamente.")));

        PlayerDao.addPlayerTag(client.getPlayer().getId(), tag);
        client.getPlayer().getData().save();

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(client.getPlayer().getEntity()));
        client.send(new UpdateInfoMessageComposer(-1, client.getPlayer().getEntity()));

    }

    @Override
    public String getPermission() {
        return "tagprofile_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.tag_profile.parameters", "(tag)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.tag_profile.description", "");
    }
}
