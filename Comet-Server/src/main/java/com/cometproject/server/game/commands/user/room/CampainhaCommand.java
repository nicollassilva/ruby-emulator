package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class CampainhaCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final RoomAccessType roomAccessType = client.getPlayer().getEntity().getRoom().getData().getAccess() == RoomAccessType.DOORBELL
                ? RoomAccessType.OPEN
                : RoomAccessType.DOORBELL;
        client.getPlayer().getEntity().getRoom().getData().setAccess(roomAccessType);

        switch (roomAccessType) {
            case OPEN:
                sendNotif("Campainha desativada", client);
                break;

            case DOORBELL:
                sendNotif("Campainha ativada", client);
                break;
        }

        GameContext.getCurrent().getRoomService().saveRoomData(client.getPlayer().getEntity().getRoom().getData());
    }

    @Override
    public String getPermission() {
        return "campainha_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return "ativa/desativa a campainha do quarto";
    }
}
