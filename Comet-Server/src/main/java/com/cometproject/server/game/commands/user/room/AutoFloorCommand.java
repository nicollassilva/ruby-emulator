package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.models.CustomFloorMapData;
import com.cometproject.api.networking.messages.IMessageComposer;
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

public class AutoFloorCommand extends ChatCommand {
    public void execute(Session client, String[] message) {
        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;
        Room room = client.getPlayer().getEntity().getRoom();
        int sizeX = room.getMapping().getModel().getSizeX();
        int sizeY = room.getMapping().getModel().getSizeY();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < sizeY; i++) {
            StringBuilder text2 = new StringBuilder();
            for (int j = 0; j < sizeX; j++) {
                if (!room.getMapping().getTile(j, i).hasItems()) {
                    text2.append("x");
                } else {
                    text2.append(parseInvers(room.getMapping().getTile(j, i).getTileHeight()));
                }
            }
            text.append(text2);
            text.append('\r');
        }
        CustomFloorMapData floorMapData = new CustomFloorMapData(room.getModel().getDoorX(), room.getModel().getDoorY(), room.getModel().getDoorRotation(), text.toString().trim(), room.getModel().getRoomModelData().getWallHeight());
        room.getData().setHeightmap(JsonUtil.getInstance().toJson(floorMapData));
        GameContext.getCurrent().getRoomService().saveRoomData(room.getData());
        RoomReloadListener reloadListener = new RoomReloadListener(room, (players, newRoom) -> {
            for (Player player : players) {
                if (player.getEntity() != null)
                    continue;
                player.getSession().send(new NotificationMessageComposer("furni_placement_error", Locale.get("command.floor.complete")));
                player.getSession().send(new RoomForwardMessageComposer(newRoom.getId()));
            }
        });
        RoomManager.getInstance().addReloadListener(room.getId(), reloadListener);
        room.reload();
    }

    public String getPermission() {
        return "autofloor_command";
    }

    public String getParameter() {
        return "";
    }

    public String getDescription() {
        return Locale.getOrDefault("command.autofloor.description", "Elimina las casillas de tu sala que estén sin utilizar");
    }

    private char parseInvers(double input) {
        int result = (input != 0.0D) ? ((input != 1.0D) ? ((input != 2.0D) ? ((input != 3.0D) ? ((input != 4.0D) ? ((input != 5.0D) ? ((input != 6.0D) ? ((input != 7.0D) ? ((input != 8.0D) ? ((input != 9.0D) ? ((input != 10.0D) ? ((input != 11.0D) ? ((input != 12.0D) ? ((input != 13.0D) ? ((input != 14.0D) ? ((input != 15.0D) ? ((input != 16.0D) ? ((input != 17.0D) ? ((input != 18.0D) ? ((input != 19.0D) ? ((input != 20.0D) ? ((input != 21.0D) ? ((input != 22.0D) ? ((input != 23.0D) ? ((input != 24.0D) ? ((input != 25.0D) ? ((input != 26.0D) ? ((input != 27.0D) ? ((input != 28.0D) ? ((input != 29.0D) ? ((input != 30.0D) ? ((input != 31.0D) ? ((input != 32.0D) ? 120 : 119) : 118) : 117) : 116) : 115) : 114) : 113) : 112) : 111) : 110) : 109) : 108) : 107) : 106) : 105) : 104) : 103) : 102) : 101) : 100) : 99) : 98) : 97) : 57) : 56) : 55) : 54) : 53) : 52) : 51) : 50) : 49) : 48;
        return (char)result;
    }
}
