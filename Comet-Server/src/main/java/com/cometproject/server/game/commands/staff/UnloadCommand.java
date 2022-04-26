package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomReloadListener;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class UnloadCommand extends ChatCommand {

    private final boolean sendToRoom;

    public UnloadCommand(boolean sendToRoom) {
        this.sendToRoom = sendToRoom;
    }

    @Override
    public void execute(Session client, String[] params) {
        if(!client.getPlayer().getPermissions().getRank().roomFullControl() && client.getPlayer().getEntity().getRoom().getData().getOwnerId() != client.getPlayer().getId())
            return;

        final Room room = client.getPlayer().getEntity().getRoom();

        if (this.sendToRoom) {
            final RoomReloadListener reloadListener = new RoomReloadListener(room, (players, newRoom) -> {
                for (final Player player : players) {
                    if (player.getEntity() != null) {
                        player.getSession().send(new RoomForwardMessageComposer(newRoom.getId()));
                    }
                }
            });

            RoomManager.getInstance().addReloadListener(client.getPlayer().getEntity().getRoom().getId(), reloadListener);
        }

        room.reload();
    }


    @Override
    public String getPermission() {
        return "unload_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.unload.description");
    }
}
