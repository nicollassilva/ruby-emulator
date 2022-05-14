package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.List;

public class ClearHighscoreCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] message) {
        if(client == null)
            return;

        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) &&
                !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            sendNotif(Locale.getOrDefault("command.need.rights", "You need rights to use this command in this room!"), client);
            return;
        }

        final List<HighscoreClassicFloorItem> scoreboards = client.getPlayer().getEntity().getRoom().getItems().getByClass(HighscoreClassicFloorItem.class);

        if (scoreboards.size() != 0) {
            for (final HighscoreClassicFloorItem scoreboard : scoreboards) {
                scoreboard.resetScoreboard();
            }
        }

        client.send(new NotificationMessageComposer("highscore", "Você redefiniu com sucesso os classificadores do quarto."));
    }

    @Override
    public String getPermission() {
        return "highschore_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.highscore.description", "Limpia las tablas de puntuación que tengas en tu sala");
    }
}
