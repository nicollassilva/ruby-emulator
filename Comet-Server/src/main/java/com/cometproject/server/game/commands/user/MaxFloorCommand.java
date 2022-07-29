package com.cometproject.server.game.commands.user;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.models.CustomFloorMapData;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomReloadListener;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class UpdateFloorCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        if (params.length == 0) {
            sendWhisper("Digite o valor da largura! Entre 1 a 64.", client);
            return;
        }

        int valueX = 0;

        try {
            valueX = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            sendWhisper("Não é válido este número para a largura!", client);
            return;
        }

        if (valueX < 1 || valueX > 64) {
            sendWhisper("Oops, valor da largura inválido! Só entre 1 a 64.", client);
            return;
        }

        if (params.length == 1) {
            sendWhisper("Digite o valor do comprimento! Entre 1 a 64.", client);
            return;
        }

        int valueY = 0;

        try {
            valueY = Integer.parseInt(params[1]);
        } catch (NumberFormatException ex) {
            sendWhisper("Não é válido este número para o comprimento!", client);
            return;
        }

        if (valueY < 1 || valueY > 64) {
            sendWhisper("Oops, valor do comprimento inválido! Só entre 1 a 64.", client);
            return;
        }

        //final int maxLength = 64;
        final StringBuilder map = new StringBuilder(valueX * valueY);
        for (int y = 0; y <= valueY; ++y) {
            for (int x = 0; x <= valueX; ++x) {
                if (y == 0) {
                    map.append("x");
                }
                else if (y == 1 && x == 0) {
                    map.append("0");
                }
                else if (x == 0) {
                    map.append("x");
                }
                else {
                    map.append("0");
                }
            }

            map.append("\r");
        }

        final int doorX = 0;
        final int doorY = 1;
        final int doorZ = 0;
        final int doorRotation = 2;
        final int wallHeight = -1;

        final Room room = client.getPlayer().getEntity().getRoom();
        CustomFloorMapData floorMapData = new CustomFloorMapData(doorX, doorY,doorZ, doorRotation, map.toString().trim(), wallHeight);

        room.getData().setHeightmap(JsonUtil.getInstance().toJson(floorMapData));

        GameContext.getCurrent().getRoomService().saveRoomData(room.getData());

        final RoomReloadListener reloadListener = new RoomReloadListener(room, (players, newRoom) -> {

            for (final Player player : players) {
                if (player.getEntity() == null) continue;

                player.getSession().send(new NotificationMessageComposer("furni_placement_error", Locale.get("command.floor.complete")));
                player.getSession().send(new RoomForwardMessageComposer(newRoom.getId()));
            }
        });

        RoomManager.getInstance().addReloadListener(room.getId(), reloadListener);
        room.reload();
    }

    @Override
    public String getPermission() {
        return "updatefloor_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.updatefloor", "(largura) (comprimento)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.updatefloor.description", "Extiende el floor de tu sala al máximo posible");
    }
}
