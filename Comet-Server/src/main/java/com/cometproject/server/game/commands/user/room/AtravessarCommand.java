package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.game.GameContext;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class AtravessarCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        client.getPlayer().getEntity().getRoom().getData().setAllowWalkthrough(!client.getPlayer().getEntity().getRoom().getData().isAllowWalkthrough());

        if (client.getPlayer().getEntity().getRoom().getData().isAllowWalkthrough()) {
            sendNotif("Agora você pode atravessar outros usuários.", client);
        } else {
            sendNotif("Atravessar usuários desativado.", client);
        }

        GameContext.getCurrent().getRoomService().saveRoomData(client.getPlayer().getEntity().getRoom().getData());
    }

    @Override
    public String getPermission() {
        return "atravessar_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.atravessar.description", "Liga/desliga o atravessar de usuários no quarto.");
    }
}
