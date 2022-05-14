package com.cometproject.server.game.commands.staff.fun;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.network.sessions.Session;

public class BubbleCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.params.invalid", "Você deve inserir o número do balão!"), client);
            return;
        }

        int bubble = 0;

        try {
            bubble = Integer.parseInt(params[0]);
        } catch(NumberFormatException ignored) {

        }

        if(bubble != 0) {
            final Integer bubbleMinRank = PermissionsManager.getInstance().getChatBubbles().get(bubble);

            if(bubbleMinRank == null) {
                bubble = 0;
            } else {
                if(client.getPlayer().getData().getRank() < bubbleMinRank) {
                    bubble = 0;
                }
            }
        }
        client.getPlayer().setBubbleId(bubble);
        sendNotif(Locale.getOrDefault("command.bubble.success", "O balão de falas foi trocado com sucesso!"), client);

    }

    @Override
    public String getPermission() {
        return "bubble_command";
    }

    @Override
    public String getParameter() {
        return "(número)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.bubble.description", "Ponte una bubble específica");
    }
}
