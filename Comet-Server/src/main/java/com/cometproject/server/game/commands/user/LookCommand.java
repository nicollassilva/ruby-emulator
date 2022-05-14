package com.cometproject.server.game.commands.user;

import com.cometproject.api.game.players.data.types.IWardrobeItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.user.details.AvatarAspectUpdateMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class LookCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final int look = Integer.parseInt(params[0]);

        for(final IWardrobeItem item : client.getPlayer().getSettings().getWardrobe()) {
            final String figure = item.getFigure();
            final String gender = item.getGender();

            if (item.getSlot() == look) {
                        client.getPlayer().getData().setFigure(figure);
                        client.getPlayer().getData().setGender(gender);
                        client.getPlayer().getData().save();
                        client.getPlayer().poof();
                        client.send(new AvatarAspectUpdateMessageComposer(figure, gender));
            }
        }
    }

    @Override
    public String getPermission() {
        return "look_command";
    }

    @Override
    public String getParameter() {
        return "(número do slot)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.lookcommand.description", "Cambia a un look guardado sin necesidad de abrir el armario.");
    }
}
