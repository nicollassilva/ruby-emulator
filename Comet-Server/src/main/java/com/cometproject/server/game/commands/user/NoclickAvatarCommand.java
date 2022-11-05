package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.network.sessions.Session;

public class NoclickAvatarCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {

        if (client.getPlayer().getEntity().getGameTeam() == GameTeam.NONE) {
            final GameTeam gameTeam = GameTeam.RED;
            client.getPlayer().getEntity().setGameTeam(gameTeam);
            client.getPlayer().getEntity().setClickThrough(true);
            sendWhisper("Modo ClickThrough ativado.", client);
            client.getPlayer().getEntity().getRoom().getGame().joinTeam(gameTeam, client.getPlayer().getEntity());
        } else {
            client.getPlayer().getEntity().getRoom().getGame().removeFromTeam(client.getPlayer().getEntity());
            client.getPlayer().getEntity().setClickThrough(false);

            sendWhisper("Modo ClickThrough desativado.", client);
        }
    }

    @Override
    public String getPermission() {
        return "clickthrouse_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.clickthrouse.description");
    }
}
