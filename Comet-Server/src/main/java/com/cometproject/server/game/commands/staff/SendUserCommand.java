package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class SendUserCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            return;
        }

        final int roomId = Integer.parseInt(params[1]);
        final String player = params[0];
        final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(player);

        if(session == null) {
            client.getPlayer().getSession().send(new TalkMessageComposer(-1, Locale.getOrDefault("command.send_user.user_not_found", "Este usuario está desconectado"), ChatEmotion.NONE, 1));
            return;
        }

        session.getPlayer().bypassRoomAuth(true);
        session.send(new RoomForwardMessageComposer(roomId));
    }

    @Override
    public String getPermission() {
        return "senduser_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.send_user.parameters", "%roomId% %username%");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.send_user.description", "");
    }
}
