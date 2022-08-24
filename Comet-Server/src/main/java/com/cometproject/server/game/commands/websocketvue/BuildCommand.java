package com.cometproject.server.game.commands.websocketvue;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BuildCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

        if (client.getPlayer().getEntity().hasAttribute("build.activated")) {
            return;
        }

        client.getPlayer().getEntity().setAttribute("build.activated", true);
        sendWhisper(Locale.getOrDefault("command.build_websocket.enabled", "Modo de construção ativado."), client);

        final JSONObject output = new JSONObject();

        final Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenBuildToolMessage);

        try {
            final OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();
            message.client = client.getPlayer().getData().getWebsocketSession();
            message.data = output;

            message.compose();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPermission() {
        return "build_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.build.description", "Ferramenta com intuito de facilitar construções.");
    }
}
