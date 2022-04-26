package com.cometproject.server.game.commands.user;

import com.cometproject.api.stats.CometStats;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class OnlineCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final CometStats cometStats = Comet.getStats();
        final int OnlineCount = cometStats.getPlayers();
        final int OnlineRoomsCount = cometStats.getRooms();

        if(params.length < 1) {
            client.send(new WhisperMessageComposer(client.getPlayer().getId(), "En estos momentos somos " + OnlineCount + " usuarios conectados con " + OnlineRoomsCount + " salas activas" , 34));
        }

    }

    @Override
    public String getPermission() {
        return "online_command";
    }

    @Override
    public String getParameter() { return ""; }

    @Override
    public String getDescription() {
        { return Locale.getOrDefault("command.about.description", "Revisa las estadísticas del hotel a tiempo real"); }
    }
}
