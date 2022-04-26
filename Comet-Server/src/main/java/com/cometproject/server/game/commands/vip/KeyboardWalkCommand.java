package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class KeyboardWalkCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(!client.getPlayer().getEntity().isKeyboardWalkEnabled()) {
            client.getPlayer().getEntity().setKeyboardEnabled(true);
            client.getPlayer().getSession().send(new NotificationMessageComposer("command.keyboard_walk.enabled", "Has activado el movimiento con las flechas de tu teclado"));
        } else {
            client.getPlayer().getEntity().setKeyboardEnabled(false);
            client.getPlayer().getSession().send(new NotificationMessageComposer("command.keyboard_walk.disabled", "Has desactivado el movimiento con las flechas de tu teclado"));
        }
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
        return Locale.getOrDefault("command.keyboard_walk.description", "");
    }
}
