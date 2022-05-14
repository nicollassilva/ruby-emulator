package com.cometproject.server.game.commands.bot;

import com.cometproject.api.game.bots.BotMode;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;

public class BotControlCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.get("command.botcontrol.none"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null)
            return;

        if (this.merge(params).equals("stop")) {

            if (client.getPlayer().getEntity().hasAttribute("botcontrol")) {
                final BotEntity botEntity = ((BotEntity) client.getPlayer().getEntity().getAttribute("botcontrol"));

                if (botEntity != null) {
                    botEntity.getData().setMode(BotMode.DEFAULT);
                }

                client.getPlayer().getEntity().removeAttribute("botcontrol");
            }

        } else {

            final String username = this.merge(params);
            final BotEntity botEntity = room.getBots().getBotByName(username);

            if (botEntity == null) {
                sendNotif(Locale.get("command.bot.error"), client);
                return;
            }

            botEntity.getData().setMode(BotMode.RELAXED);
            client.getPlayer().getEntity().setAttribute("botcontrol", botEntity);
            client.getPlayer().getEntity().cancelWalk();
        }

        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "botcontrol_command";
    }

    @Override
    public String getDescription() { return "(nome do bot)"; }

    @Override
    public String getParameter() {
        return "command.botcontrol.description";
    }
}
