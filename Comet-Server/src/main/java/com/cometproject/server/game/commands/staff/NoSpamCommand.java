package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class NoSpamCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();
        final PlayerEntity entity = client.getPlayer().getEntity();

        int i = 0;
        final int iterator = Integer.parseInt(Locale.getOrDefault("number.iterator.nospam", "25"));

        while (i < iterator) {
            room.getEntities().broadcastMessage(new TalkMessageComposer(entity.getId(), "NO SPAM NO SPAM NO SPAM NO SPAM NO SPAM NO SPAM NO SPAM NO SPAM NO SPAM NO SPAM", ChatEmotion.NONE, 34));
            i++;
        }
    }


    @Override
    public String getPermission() {
        return "nospam_command";
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
