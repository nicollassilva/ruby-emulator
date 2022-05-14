package com.cometproject.server.game.commands.staff.rewards;

import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class WinnerCommand extends ChatCommand {
    public void execute(Session client, String[] params) {
        if (params.length < 2) return;

        final String username = params[0];
        final String type = params[1];
        int credits = 0;

        switch (type) {
            case "facil":
                credits = Integer.parseInt(Locale.getOrDefault("premio.facil", "3"));
                break;
            case "medio":
                credits = Integer.parseInt(Locale.getOrDefault("premio.facil", "5"));
                break;
            case "dificil":
                credits = Integer.parseInt(Locale.getOrDefault("premio.facil", "7"));
                break;
        }

        final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            final PlayerData playerData = PlayerDao.getDataByUsername(username);
            if (playerData == null)
                return;
            playerData.increaseCredits(credits);
            playerData.save();
            return;
        }

        session.getPlayer().getData().increaseCredits(credits);
        session.getPlayer().getData().increaseGamesWin(1);
        session.getPlayer().getData().save();
        session.send(session.getPlayer().composeCurrenciesBalance());
        session.send(session.getPlayer().composeCreditBalance());
        NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer("winner", Locale.get("winner.info.alert").replace("%user%", session.getPlayer().getData().getUsername())));
    }

    public String getPermission() {
        return "winner_command";
    }

    @Override
    public String getParameter() {
        return null;
    }

    public String getDescription() {
        return Locale.get("command.winner.description");
    }
}
