package com.cometproject.server.game.commands.user;

import com.cometproject.api.game.rooms.settings.RoomBanState;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;

public class RoomBanUserCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            sendWhisper("Oops, digite o nick do usuário e o tipo de banimento (hora/dia/semana/sempre).", client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;

        if (client.getPlayer().getId() != room.getData().getOwnerId() && room.getData().getBanState() != RoomBanState.RIGHTS && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;

        final String username = params[0];

        final Session targetSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (targetSession == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        final RoomEntity userEntity = room.getEntities().getEntityByName(username, RoomEntityType.PLAYER);

        if (userEntity == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (username.equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.get("command.pull.playerhimself"), client);
            return;
        }

        int expireTimestamp = 0;
        final String option = params[1];

        switch (option) {
            case "hora":
            case "hour":
                expireTimestamp = 3600;
                break;
            case "dia":
            case "day":
                expireTimestamp = (3600) * 24;
                break;
            case "semana":
            case "week":
                expireTimestamp = (3600) * 24 * 7;
                break;
            case "sempre":
            case "forever":
                expireTimestamp = (3600) * 24 * 365;
                break;
            default:
                sendWhisper("Oops, tem que escolher uma opção válida. (hora/dia/semana/sempre)", client);
                return;
        }

        final int userId = targetSession.getPlayer().getData().getId();

        if (room.getData().getOwnerId() == userId || !targetSession.getPlayer().getPermissions().getRank().roomKickable())
            return;

        room.getRights().addBan(userId, targetSession.getPlayer().getData().getUsername(), (int) Comet.getTime() + expireTimestamp);
        userEntity.leaveRoom(false, true, true);

        sendWhisper("O usuário " + targetSession.getPlayer().getData().getUsername() + " foi banido do quarto com sucesso!", client);
    }

    @Override
    public String getPermission() {
        return "roomban_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.roomban.type", "(usuário) (hora/dia/semana/sempre)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.roomban.description", "Banir um usuário do quarto.");
    }
}