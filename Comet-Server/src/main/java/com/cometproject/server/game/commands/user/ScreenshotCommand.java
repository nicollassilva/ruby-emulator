package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class ScreenshotCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

    }

    @Override
    public String getPermission() {
        return "screenshot_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.screenshot.description", "Toma una captura de pantalla de la sala.");
    }
}
