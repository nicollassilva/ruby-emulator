package com.cometproject.server.game.commands.vip;

import com.cometproject.api.game.rooms.RoomDiagonalType;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;

import javax.annotation.Nullable;

public class DiagonalCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getEntity().getRoom().getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            sendNotif(Locale.getOrDefault("command.togglediagonal.nopermission", "You don't have permission to use this command!"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        @Nullable RoomDiagonalType type = params.length > 0 ? RoomDiagonalType.parse(params[0]) : null;
        if (params.length > 0) {
            if (params[0].equals("?") || params[0].equals("lista")) {
                sendAlert("Tipos de diagonal disponíveis: \n\n- on/enable/ativar: habilita a diagonal permitindo qualquer movimento\n- off/disable/desativar: desabilita a diagonal\n- strict: habilita a diagonal como no habbo original. Só poderá andar na diagonal se o usuário também puder andar até a posição.", client);
                return;
            }
            type = RoomDiagonalType.parse(params[0]);
        }

        if (type == null) {
            type = RoomDiagonalType.toggle(room.getData().getRoomDiagonalType());
        }
        room.getData().setRoomDiagonalType(type);

        switch (type) {
            case STRICT: {
                sendNotif(Locale.getOrDefault("command.togglediagonal.strict", "Diagonal strict ativada!"), client);
                return;
            }

            case DISABLED: {
                sendNotif(Locale.getOrDefault("command.togglediagonal.disabled", "Diagonal desativada!"), client);
                return;
            }

            case ENABLED: {
                sendNotif(Locale.getOrDefault("command.togglediagonal.enabled", "Diagonal ativada!"), client);
            }
        }
    }

    @Override
    public String getPermission() {
        return "togglediagonal_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.togglediagonal.description");
    }
}
