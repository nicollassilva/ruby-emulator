package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.misc.OpenLinkMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class EmojiCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] message) {
        if(message.length >= 1) {
            return;
        }

        client.getPlayer().getData().setEmojiEnabled(!client.getPlayer().getData().isEmojiEnabled());
        //client.getPlayer().sendNotif("Ajustes de emoji", "Los emojis ahora están " + (client.getPlayer().getData().isEmojiEnabled() ? "activados" : "desactivados"));
        client.getPlayer().getSession().send(new NotificationMessageComposer("generic", "Los emojis ahora están " + (client.getPlayer().getData().isEmojiEnabled() ? "activados" : "desactivados")));
    }

    @Override
    public String getPermission() {
        return "emoji_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.emoji.description", "Activa o desactiva los emojis");
    }
}
