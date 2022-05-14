package com.cometproject.server.game.commands.staff;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class InvisibleCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        final boolean isVisible = !client.getPlayer().getEntity().isVisible();

        client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.get("command.invisible." + (isVisible ? "disabled" : "enabled"))));

        client.getPlayer().setInvisible(!isVisible);
        client.getPlayer().getEntity().updateVisibility(isVisible);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "-c -d"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-d", (isVisible) ? "ficou visível" : "ficou invisível");
    }

    @Override
    public String getPermission() {
        return "invisible_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.invisible.description");
    }

    @Override
    public String getLoggableDescription() {
        return this.logDesc;
    }

    @Override
    public boolean isLoggable() {
        return true;
    }
}
