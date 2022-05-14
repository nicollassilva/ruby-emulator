package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.AvatarAspectUpdateMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class MimicOfflineCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Usuário inválido!"), client);
            return;
        }

        final String username = params[0];

        if(username == null ) {
            client.send(new NotificationMessageComposer("generic", "Por favor, informe o nome do usuário"));
            return;
        }

        final String figure = PlayerDao.getFigureByUsername(username);
        final String gender = PlayerDao.getGenderByUsername(username);

        PlayerEntity playerEntity = client.getPlayer().getEntity();

        playerEntity.getPlayer().getData().setFigure(figure);
        playerEntity.getPlayer().getData().setGender(gender);
        playerEntity.getPlayer().getData().save();

        playerEntity.getPlayer().poof();
        client.send(new AvatarAspectUpdateMessageComposer(figure, gender));

        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "mimicoff_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.mimicoff.description", "Copia o visual de um usuário.");
    }
}
