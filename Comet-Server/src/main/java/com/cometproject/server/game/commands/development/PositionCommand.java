package com.cometproject.server.game.commands.development;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;


public class PositionCommand extends ChatCommand {
    private final boolean debug;

    public PositionCommand() {
        this.debug = false;
    }

    public PositionCommand(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void execute(Session client, String[] params) {
        sendNotif(("Posição X: " + client.getPlayer().getEntity().getPosition().getX() + "\r\n") +
                        "Posição Y: " + client.getPlayer().getEntity().getPosition().getY() + "\r\n" +
                        "Posição Z: " + client.getPlayer().getEntity().getPosition().getZ() + "\r\n" +
                        "Rotação: " + client.getPlayer().getEntity().getBodyRotation() + "\r\n",
                client);
    }

    @Override
    public String getPermission() {
        return this.debug ? "dev" : "position_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.position.description", "Revisa las coordenadas de tu posición en la sala");
    }
}
