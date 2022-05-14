package com.cometproject.server.game.commands.staff.rewards;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.webhooks.CurrencyWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class CoinsCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2)
            return;

        final String username = params[0];
        final String credits_ = params[1];

        try {
            final int credits = Integer.parseInt(credits_);
            final Session player = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

            if (player == null) {
                final PlayerData playerData = PlayerDao.getDataByUsername(username);

                if (playerData == null) return;

                playerData.increaseCredits(credits);
                playerData.save();
                return;
            }

            player.getPlayer().getData().increaseCredits(credits);
            player.getPlayer().getData().save();
            player.getPlayer().sendBalance();

            CurrencyWebhook.send(client.getPlayer().getData().getUsername(), player.getPlayer().getData().getUsername(), credits, "moedas");

            player.send(new AdvancedAlertMessageComposer(Locale.get("command.coins.title"), Locale.get("command.coins.received").replace("%amount%", String.valueOf(credits))));
        } catch (Exception e) {
            client.send(new AdvancedAlertMessageComposer(Locale.get("command.coins.errortitle"), Locale.get("command.coins.formaterror")));
        }

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "-c enviou -e moedas para o usuário -d"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-e", credits_)
                .replace("-d", username);
    }

    @Override
    public String getPermission() {
        return "coins_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username" + " " + "command.parameter.amount", "(usuário) (quantia)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.coins.description");
    }

    @Override
    public String getLoggableDescription() {
        return this.logDesc;
    }

    @Override
    public boolean isLoggable() {
        return true;
    }
}
