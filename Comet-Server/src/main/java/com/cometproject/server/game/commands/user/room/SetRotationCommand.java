package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class SetRotationCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper("Você digitou errado o comando. :rotation [valor] ~ :rotation stop.", client);
            return;
        }

        if (params[0].equals("stop")) {
            if(client.getPlayer().getEntity().hasAttribute("rotation.height")) {
                client.getPlayer().getEntity().removeAttribute("rotation.height");
                sendWhisper("Rotação desativada.", client);
                return;
            }

            sendWhisper("Rotação desativada.", client);
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            try {
                final int rotation = Integer.parseInt(params[0]);

                if (rotation < 0 || rotation > 7) {
                    sendWhisper("A rotação deve estar entre 0 e 7.", client);
                    return;
                }

                client.getPlayer().getEntity().setAttribute("rotation.height", rotation);

                sendWhisper("A rotação estabelecida foi " + rotation + ".", client);
            } catch (Exception e) {
                sendWhisper("Você digitou errado o comando. :rotation [valor] ~ :rotation stop.", client);
            }
        }
    }

    @Override
    public String getPermission() {
        return "setrot_command";
    }

    @Override
    public String getParameter() {
        return "(número)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.setrot.description", "Use este comando para girar mobílias, rotações disponíveis em um intervalo entre 0 e 7.");
    }
}