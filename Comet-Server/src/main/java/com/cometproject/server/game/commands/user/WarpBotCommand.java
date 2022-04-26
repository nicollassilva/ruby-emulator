package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.network.sessions.Session;

public class WarpBotCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final String botName = params[0];

        for (final BotEntity botEntity : client.getPlayer().getEntity().getRoom().getEntities().getBotEntities()) {
            if(botEntity.getUsername().equals(botName)) {
                botEntity.teleportToEntity(client.getPlayer().getEntity());
            }
        }
    }

    @Override
    public String getPermission() {
        return "warpbot_command";
    }

    @Override
    public String getParameter() {
        return "(nombre del bot)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.warpbot.description", "Atrae o teletransporta un bot a tu posición");
    }
}
