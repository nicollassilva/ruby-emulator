package com.cometproject.server.game.commands.bot;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.user.details.AvatarAspectUpdateMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class MimicBotCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.get("command.mimicbot.none"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null)
            return;

        final String username = this.merge(params);
        final BotEntity botEntity = room.getBots().getBotByName(username);

        if (botEntity == null) {
            sendNotif(Locale.get("command.bot.error"), client);
            return;
        }

        PlayerEntity playerEntity = client.getPlayer().getEntity();

        playerEntity.getPlayer().getData().setFigure(botEntity.getFigure());
        playerEntity.getPlayer().getData().setGender(botEntity.getGender());
        playerEntity.getPlayer().getData().save();

        playerEntity.getPlayer().poof();
        client.send(new AvatarAspectUpdateMessageComposer(botEntity.getFigure(), botEntity.getGender()));
        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "mimicbot_command";
    }

    @Override
    public String getDescription() { return "Copia el look del bot";}

    @Override
    public String getParameter() {
        return Locale.get("command.parameter.username");
    }
}
