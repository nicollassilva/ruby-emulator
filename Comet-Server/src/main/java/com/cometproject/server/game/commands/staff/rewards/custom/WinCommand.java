package com.cometproject.server.game.commands.staff.rewards.custom;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class WinCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(params.length < 1) return;

        final String username = params[0];
        final Session playerSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if(playerSession == null) {
            sendNotif("O usuário ficou offline ou não existe.", client);
            return;
        }

        int updatedPlayerPoints = PlayerDao.incrementUserEventsPoints(playerSession.getPlayer().getId());

        if(updatedPlayerPoints <= CometExternalSettings.currentGameBadgeLimit) {
            playerSession.getPlayer().getInventory().addBadge(CometExternalSettings.currentGameBadgePrefix + updatedPlayerPoints + "", true, false, false);
            playerSession.getPlayer().getInventory().removeBadge(CometExternalSettings.currentGameBadgePrefix + (updatedPlayerPoints - 1) + "", true, false, true);
            playerSession.getPlayer().getData().increaseVipPoints(CometExternalSettings.eventDiamantsReward);
        } else {
            playerSession.getPlayer().getData().increaseVipPoints(CometExternalSettings.eventDiamantsRewardDouble);
        }

        playerSession.getPlayer().getData().save();
        playerSession.getPlayer().sendBalance();

        if(playerSession.getPlayer().getData().getGender().equals("M")) {
            NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer("events", Locale.getOrDefault("command.win.male.alert", "O usuário %user% ganhou um evento.").replace("%user%", username)));
        } else {
            NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer("events", Locale.getOrDefault("command.win.female.alert", "A usuária %user% ganhou um evento.").replace("%user%", username)));
        }

        playerSession.getPlayer().getEntity().kick();
    }

    @Override
    public String getPermission() {
        return "win_command";
    }

    @Override
    public String getParameter() {
        return "(usuário)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.win.description", "Envia ponto no hall, diamante e próximo nível do emblema");
    }
}
