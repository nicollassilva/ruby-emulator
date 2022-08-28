package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.misc.NameColor;
import com.cometproject.server.network.messages.outgoing.misc.OpenLinkMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.regex.Pattern;

public class NameColourCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        final String colour = params[0];
        if (!this.isValidHexColor(colour.replace("#", ""))) {
            sendWhisper("A cor digitada é inválida! Tente novamente. Exemplo: FF00FF", client);
            return;
        }

        sendWhisper("Cor definida com sucesso!", client);

        client.getPlayer().getData().setNameColour(colour);
        client.getPlayer().getData().save();
        isExecuted(client);
    }

    private boolean isValidHexColor(String color) {
        return Pattern.matches("([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$", color);
    }

    @Override
    public String getPermission() {
        return "namecolour_command";
    }

    @Override
    public String getParameter() {
        return Locale.get("command.namecolour.param");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.namecolour.description", "Ponle color a tu nombre");
    }
}