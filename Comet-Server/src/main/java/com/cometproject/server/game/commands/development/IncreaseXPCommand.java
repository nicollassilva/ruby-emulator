package com.cometproject.server.game.commands.development;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class IncreaseXPCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        int cantity = Integer.parseInt(params[0]);

        client.getPlayer().getData().increaseXp(cantity);
        client.getPlayer().getData().save();
        client.send(new MotdNotificationMessageComposer(
                Locale.getOrDefault("command.increase_xp.message", "Tienes %quantity% XP con un nivel de: %level%")
                        .replace("%quantity%", client.getPlayer().getData().getXp() + "")
                        .replace("%level%", client.getPlayer().getData().getLevel() + "")
        ));
    }

    @Override
    public String getPermission() {
        return "hotelalert_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.increase_xp.parameters", "%quantity%");
    }

    @Override
    public String getDescription() {
        return null;
    }
}
