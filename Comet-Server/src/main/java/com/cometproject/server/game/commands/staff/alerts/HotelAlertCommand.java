package com.cometproject.server.game.commands.staff.alerts;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.config.CometSettings;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.boot.DiscordIntegration;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.messenger.InstantChatMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.HashMap;
import java.util.Map;

public class HotelAlertCommand extends ChatCommand {
    private String logDesc;

    public void execute(Session client, String[] message) {
        if (message.length == 0) {
            return;
        }

        final String realMessage = merge(message);

        NetworkManager.getInstance().getSessions().broadcast(new AlertMessageComposer(String.format("Alerta del Equipo Administrativo:<br><br>%s<br><br>- %s", realMessage, client.getPlayer().getData().getUsername())));

        if (!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "El Staff -c ha mandado una alerta a todo el hotel. [-s]"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-s", this.merge(message));

        for (final Session player : ModerationManager.getInstance().getLogChatUsers()) {
            player.send(new InstantChatMessageComposer(this.logDesc, Integer.MAX_VALUE - 1));
        }
    }

    public boolean isAsync() {
        return true;
    }

    public String getPermission() {
        return "hotelalert_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.hotel_alert.parameters", "");
    }

    public String getDescription() {
        return Locale.get("command.hotel_alert.description");
    }
}
