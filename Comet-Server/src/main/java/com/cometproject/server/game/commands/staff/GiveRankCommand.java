package com.cometproject.server.game.commands.staff;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class GiveRankCommand extends ChatCommand {
    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2 || !StringUtils.isNumeric(params[1]))
            return;

        final String username = params[0];
        final int rank = Integer.parseInt(params[1]);

        final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            client.getPlayer().sendNotif("error", Locale.getOrDefault("user.not.found", "User is not found or currently offline"));
            return;
        }

        if(username.equals(client.getPlayer().getData().getUsername())) {
            client.getPlayer().sendNotif("error", Locale.getOrDefault("user.himself", "No te puedes dar rango a ti mismo"));
            return;
        }

        if(rank >= client.getPlayer().getData().getRank()) {
            client.getPlayer().sendNotif("error", Locale.getOrDefault("rank.too.high", "You cannot give a rank higher or equal to yours"));
            return;
        }

        session.getPlayer().getData().setRank(rank);
        PlayerDao.updateRank(session.getPlayer().getId(), rank);

        client.getPlayer().sendNotif("success", Locale.getOrDefault("command.giverank.success", "Rank set succesfully!"));
        session.getPlayer().sendNotif("Rank updated", Locale.getOrDefault("command.giverank.received", "Your rank was set to " + rank + ". Please reload client"));

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s has given rank %r to user '%u'"
                .replace("%r", Integer.toString(rank))
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%u", username);
    }

    @Override
    public String getPermission() {
        return "giverank_command";
    }

    @Override
    public String getParameter() {
        return "(usuário) (rank)";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.giverank.description");
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return true;
    }
}
