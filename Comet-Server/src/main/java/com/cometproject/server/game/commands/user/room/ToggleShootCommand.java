package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.config.Locale;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.game.commands.ChatCommand;

public class ToggleShootCommand extends ChatCommand
{
    @Override
    public void execute(final Session client, final String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room.getData().getOwnerId() != client.getPlayer().getData().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        room.getGame().setShootEnabled(!room.getGame().shootEnabled());
        ChatCommand.sendNotif(Locale.get("command.toggleshoot." + (room.getGame().shootEnabled() ? "enabled" : "disabled")), client);
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
        return Locale.get("command.toggleshoot.description");
    }
}