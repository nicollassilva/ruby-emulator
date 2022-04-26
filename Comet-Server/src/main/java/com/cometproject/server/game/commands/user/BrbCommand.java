package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class BrbCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        client.getPlayer().getEntity().setAway();

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "* " + client.getPlayer().getData().getUsername() + " voy AFK! *", ChatEmotion.SMILE, 34));
    }

    @Override
    public String getPermission() {
        return "away_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.brb.description", "Pon tu usuario en estado ausente");
    }
}
