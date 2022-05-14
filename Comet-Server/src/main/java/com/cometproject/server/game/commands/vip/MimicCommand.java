package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.user.details.AvatarAspectUpdateMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class MimicCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Usuário inválido!"), client);
            return;
        }

        final String username = params[0];
        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (username.equals(client.getPlayer().getData().getUsername())) {
            return;
        }

        if (!user.getPlayer().getSettings().getAllowMimic()) {
            sendNotif(Locale.getOrDefault("command.mimic.disabled", "Você não pode copiar o visual desse usuário."), client);
            return;
        }

        final PlayerEntity playerEntity = client.getPlayer().getEntity();

        playerEntity.getPlayer().getData().setFigure(user.getPlayer().getData().getFigure());
        playerEntity.getPlayer().getData().setGender(user.getPlayer().getData().getGender());
        playerEntity.getPlayer().getData().save();

        playerEntity.getPlayer().poof();
        client.send(new AvatarAspectUpdateMessageComposer(user.getPlayer().getData().getFigure(), user.getPlayer().getData().getGender()));
        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "mimic_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.mimic.description", "Copia o visual de um usuário.");
    }
}
