package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.server.config.Locale;
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
                sendNotif("Quarto aberto para todos.", client);
                break;

            case DOORBELL:
                sendNotif("Quarto protegido por campainha.", client);
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
        return Locale.getOrDefault("command.campainha.description", "Liga/desliga a campainha do quarto.");
    }
}
