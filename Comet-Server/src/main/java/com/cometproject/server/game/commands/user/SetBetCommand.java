package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class SetBetCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Debes colocar un número válido para apostar, :setbet 1-50", ChatEmotion.NONE, 34));
            return;
        }

        try {
            int amount = Integer.parseInt(params[0]);

            if (amount < 0) {
                amount = 5;
            } else if (amount > 50) {
                client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "No puedes apostar más de 50 diamantes", ChatEmotion.NONE, 34));
                amount = 50;
            }

            client.getPlayer().getEntity().setBetAmount(amount);
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Haz colocado tu apuesta en " + amount, ChatEmotion.NONE, 34));
        } catch (Exception e) {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Por favor introduce valores numéricos", ChatEmotion.NONE, 34));
        }
    }


    @Override
    public String getPermission() {
        return "setbet_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.setbet.number", "%amount%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.setbet.description");
    }
}
