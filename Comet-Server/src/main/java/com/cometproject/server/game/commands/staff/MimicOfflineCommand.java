package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.utilities.validator.ClothingValidationManager;
import com.cometproject.server.game.utilities.validator.FigureGender;
import com.cometproject.server.network.messages.outgoing.user.details.AvatarAspectUpdateMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class MimicOfflineCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1 || params[0].length() < 3) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Nome de usuário inválido!"), client);
            return;
        }

        final String username = params[0];
        final String figure = PlayerDao.getFigureByUsername(username);
        final String gender = PlayerDao.getGenderByUsername(username);

        if(figure == null || gender == null) {
            sendNotif("Usuário não encontrado!", client);
            return;
        }

        if(ClothingValidationManager.isInvalidLook(client.getPlayer(), figure, FigureGender.fromString(gender))){
            sendNotif(Locale.getOrDefault("command.mimic.missing_clothing", "Você não pode copiar o visual desse usuário porque não possuí todas as peças de roupa que ele está vestindo."), client);
            return;
        }

        final PlayerEntity playerEntity = client.getPlayer().getEntity();

        playerEntity.getPlayer().getData().setFigure(ClothingValidationManager.validateLook(client.getPlayer(), figure, FigureGender.fromString(gender)));
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
