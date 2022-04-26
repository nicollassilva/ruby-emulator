package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class FastWalkCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room != null) {
            if (room.hasAttribute(("fastwalkcmd"))) {
                client.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.room.error")));
                return;
            }
        }

        if (client.getPlayer().getEntity().isFastWalkEnabled()) {
            client.getPlayer().getEntity().toggleFastWalk();
            sendWhisper(Locale.get("command.fastwalk.disabled"), client);

        } else {
            client.getPlayer().getEntity().toggleFastWalk();
            sendWhisper(Locale.get("command.fastwalk.enabled"), client);
        }
    }

    @Override
    public String getPermission() {
        return "fastwalk_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.fastwalk.description", "Activa el caminar rápido");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
