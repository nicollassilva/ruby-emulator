package com.cometproject.server.game.commands.user;

import com.cometproject.api.commands.CommandInfo;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.commands.CommandManager;
import com.cometproject.server.modules.ModuleManager;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CommandsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final List<String> list = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();

        for (final Map.Entry<String, CommandInfo> commandInfoEntry : ModuleManager.getInstance().getEventHandler().getCommands().entrySet()) {
            if (client.getPlayer().getPermissions().hasCommand(commandInfoEntry.getValue().getPermission()) || commandInfoEntry.getValue().getPermission().isEmpty()) {
                list.add(commandInfoEntry.getKey() + " - " + commandInfoEntry.getValue().getDescription() + "\n\n");
            }
        }

        for (final Map.Entry<String, ChatCommand> command : CommandManager.getInstance().getChatCommands().entrySet()) {
            if (command.getValue().isHidden()) continue;

            if (client.getPlayer().getPermissions().hasCommand(command.getValue().getPermission())) {
                list.add(command.getKey().split(",")[0] + " " + command.getValue().getParameter() + " " + command.getValue().getDescription() + "\n\n");
            }
        }

        list.sort(String::compareToIgnoreCase);

        for (final String value : list) {
            builder.append(value);
        }

        client.send(new MotdNotificationMessageComposer(Locale.get("command.commands.title") + "\n\n" + builder));
    }

    @Override
    public String getPermission() {
        return "commands_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.commands.description");
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
