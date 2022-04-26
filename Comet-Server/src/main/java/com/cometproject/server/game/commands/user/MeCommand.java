package com.cometproject.server.game.commands.user;

import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class MeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "*" + this.merge(params) + "*", ChatEmotion.SMILE, 34));
    }

    @Override
    public String getPermission() {
        return "me_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return "null";
    }
}
