package com.cometproject.server.game.commands.staff;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class WelcomeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (params.length != 1) {
            return;
        }

        final String username = params[0];

        if(NetworkManager.getInstance().getSessions().getByPlayerUsername(username) == null){
            return;
        }

        room.getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("welcome.message", "¡Hola, %user%, te damos la bienvenida a " + CometSettings.hotelName + " pásalo en grande y si tienes cualquier duda dínoslo.").replace("%user%", username), ChatEmotion.SMILE, 0));
    }

    @Override
    public String getPermission() {
        return "welcome_command";
    }

    @Override
    public String getParameter() {
        return "(usuario)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.welcome.description", "Dale la bienvenida a un usuario");
    }
}
