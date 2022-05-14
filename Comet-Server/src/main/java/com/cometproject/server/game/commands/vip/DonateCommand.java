package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;
import org.apache.commons.lang.StringUtils;

public class DonateCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 3) {
            sendWhisper(Locale.getOrDefault("command.donate.error.params", "Você deve fornecer o nome de usuário, valor e tipo de moeda."), client);
            return;
        }

        String playerName = params[0];
        String strAmount = params[1];
        String currency = params[2];
        PlayerData playerData = PlayerDao.getDataByUsername(playerName);

        if (playerData == null) {
            sendWhisper(Locale.getOrDefault("command.donate.error.playernotfound", "Usuário '%username%' não encontrado.")
                    .replace("%player%", playerName), client);
            return;
        }

        if(playerName.equals(client.getPlayer().getData().getUsername())) {
            sendWhisper(Locale.getOrDefault("command.donate.himself", "Não é possível enviar moedas para si mesmo."), client);
            return;
        }

        if (!StringUtils.isNumeric(strAmount)) {
            sendWhisper(Locale.getOrDefault("command.donate.error.amount", "O valor deve ser um número."), client);
            return;
        }

        int amount = Integer.parseInt(strAmount);
        Session targetSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(playerName);

        switch (currency) {
            case "moedas":
                if (client.getPlayer().getData().getCredits() < amount) {
                    sendWhisper(Locale.get("command.donate.error.notenough"), client);
                    return;
                }

                client.getPlayer().getData().decreaseCredits(amount);
                client.getPlayer().getData().save();

                client.send(client.getPlayer().composeCreditBalance());

                if (targetSession != null) {
                    targetSession.getPlayer().getData().increaseCredits(amount);
                    targetSession.getPlayer().getData().save();

                    targetSession.send(targetSession.getPlayer().composeCreditBalance());
                } else {
                    playerData.increaseCredits(amount);
                    playerData.save();
                }
                break;
            case "diamantes":
                if (client.getPlayer().getData().getVipPoints() < amount) {
                    sendWhisper(Locale.get("command.donate.error.notenough"), client);
                    return;
                }

                client.getPlayer().getData().decreaseVipPoints(amount);
                client.getPlayer().getData().save();

                client.send(client.getPlayer().composeCurrenciesBalance());

                if (targetSession != null) {
                    targetSession.getPlayer().getData().increaseVipPoints(amount);
                    targetSession.getPlayer().getData().save();

                    targetSession.send(targetSession.getPlayer().composeCurrenciesBalance());
                } else {
                    playerData.increaseVipPoints(amount);
                    playerData.save();
                }
                break;
            case "rubis":
                if (client.getPlayer().getData().getSeasonalPoints() < amount) {
                    sendWhisper(Locale.get("command.donate.error.notenough"), client);
                    return;
                }

                client.getPlayer().getData().decreaseSeasonalPoints(amount);
                client.getPlayer().getData().save();

                client.send(client.getPlayer().composeCurrenciesBalance());

                if (targetSession != null) {
                    targetSession.getPlayer().getData().increaseSeasonalPoints(amount);
                    targetSession.getPlayer().getData().save();

                    targetSession.send(targetSession.getPlayer().composeCurrenciesBalance());
                } else {
                    playerData.increaseSeasonalPoints(amount);
                    playerData.save();
                }
                break;
            default:
                sendWhisper(Locale.get("command.donate.error.currency"), client);
                return;
        }

        sendWhisper(Locale.get("command.donate.success")
                .replace("%amount%", "" + amount)
                .replace("%currency%", currency)
                .replace("%username%", playerName), client);

        if (targetSession != null) {
            sendWhisper(Locale.get("command.donate.received")
                    .replace("%sender%", client.getPlayer().getData().getUsername())
                    .replace("%amount%", "" + amount)
                    .replace("%currency%", currency), targetSession);
        }
    }

    @Override
    public String getPermission() {
        return "donate_command";
    }

    @Override
    public String getParameter() {
        return "(usuário) (quantidade) (câmbio)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.donate.description", "Envía alguna moneda a un usuario");
    }
}