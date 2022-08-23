package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class SetStateCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper("Você digitou errado o comando. :state [valor] ~ :state stop.", client);
            return;
        }

        if (params[0].equals("stop")) {
            if(client.getPlayer().getEntity().hasAttribute("state.height")) {
                client.getPlayer().getEntity().removeAttribute("state.height");
                sendWhisper("Estado desativado.", client);
                return;
            }

            sendWhisper("Estado desativado.", client);
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            try {
                final int state = Integer.parseInt(params[0]);

                if (state < 0 || state > 100) {
                    sendWhisper("O estado deve estar entre 0 e 100.", client);
                    return;
                }

                client.getPlayer().getEntity().setAttribute("state.height", state);

                sendWhisper("O estado estabelecido foi " + state + ".", client);
            } catch (Exception e) {
                sendWhisper("Você digitou errado o comando. :state [valor] ~ :state stop.", client);
            }
        }
    }

    @Override
    public String getPermission() {
        return "setstate_command";
    }

    @Override
    public String getParameter() {
        return "(número)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.setstate.description", "Use este comando para mudar o estado das mobílias, estados disponíveis em um intervalo entre 0 e 100.");
    }
}