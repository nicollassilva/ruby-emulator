package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;

public class WarpCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        if ((room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            sendNotif(Locale.getOrDefault("command.need.rights", "Você não tem direitos para usar esse comando no quarto."), client);
            return;
        }

        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.warp.none", "Quem você quer remover daqui?"), client);
            return;
        }

        final String username = params[0].toUpperCase();

        if (!PlayerManager.getInstance().isOnline(username)) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        final PlayerEntity target = (PlayerEntity) room.getEntities().getEntityByName(username, RoomEntityType.PLAYER);

        if(target == null){
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está no quarto."), client);
            return;
        }

        target.teleportToEntity(client.getPlayer().getEntity());
    }

    @Override
    public String getPermission() {
        return "warp_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.warp.description");
    }

}
