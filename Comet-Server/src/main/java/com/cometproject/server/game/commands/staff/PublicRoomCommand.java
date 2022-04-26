package com.cometproject.server.game.commands.staff;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.RoomType;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.navigator.NavigatorDao;

public class PublicRoomCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(params.length < 1) {
            client.getPlayer().sendNotif("error", Locale.get("command.param.missing"));
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if(room == null) return;

        switch(params[0]) {
            default:
            case "add": {
                if(NavigatorManager.getInstance().getPublicRoom(room.getId()) == null) {
                    room.getData().setType(RoomType.PUBLIC);
                    GameContext.getCurrent().getRoomService().saveRoomData(room.getData());
                    NavigatorDao.insertPublicRoom(room.getId(), room.getData().getName(), room.getData().getDescription());
                    NavigatorManager.getInstance().loadPublicRooms();
                    room.reload();
                    client.getPlayer().sendNotif("success", Locale.getOrDefault("command.publicroom.add.success", "The room was added to Public Rooms!"));
                } else {
                    client.getPlayer().sendNotif("Error", Locale.getOrDefault("command.publicroom.alreadypublic", "The room is already public!"));
                }
                break;
            }
            case "remove": {
                if(NavigatorManager.getInstance().getPublicRoom(room.getId()) != null) {
                    room.getData().setType(RoomType.PRIVATE);
                    GameContext.getCurrent().getRoomService().saveRoomData(room.getData());
                    NavigatorDao.removePublicRoom(room.getId());
                    NavigatorManager.getInstance().loadPublicRooms();
                    room.reload();
                    client.getPlayer().sendNotif("success", Locale.getOrDefault("command.publicroom.remove.success", "The room was removed from Public Rooms!"));
                } else {
                    client.getPlayer().sendNotif("error", Locale.getOrDefault("command.publicroom.notpublic", "The room is not public!"));
                }
            }
        }
    }

    @Override
    public String getPermission() {
        return "publicroom_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.publicroom.description");
    }
}
