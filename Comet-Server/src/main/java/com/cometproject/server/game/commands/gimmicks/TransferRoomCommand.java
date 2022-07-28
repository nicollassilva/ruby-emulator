package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomReloadListener;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.storage.queries.rooms.RoomDao;

public class TransferRoomCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper(Locale.getOrDefault("command.user.invalid", "Usuário inválido!"), client);
            return;
        }

        final String username = params[0];

        Room room = client.getPlayer().getEntity().getRoom();

        //Check if user exists
        int userId = PlayerDao.getIdByUsername(username);
        if (userId == 0) {
            sendWhisper("Oops, este usuário não existe!", client);
            return;
        }

        //Check if the target user id is the same as the room owner
        if (room.getData().getOwnerId() == userId) {
            sendWhisper("Oops, você não pode usar este comando porque este quarto é o mesmo dono que acabou de digitar!", client);
            return;
        }

        //Get the right username in case the user writes it wrong
        final String targetUsername = PlayerDao.getUsernameByPlayerId(userId);

        final boolean result = RoomDao.transferRoom(room.getId(), userId, targetUsername);

        if (result) {
            room.getData().setOwner(targetUsername);
            room.getData().setOwnerId(userId);

            final RoomReloadListener reloadListener = new RoomReloadListener(room, (players, newRoom) -> {
                for (final Player player : players) {
                    if (player.getEntity() != null) {
                        player.getSession().send(new RoomForwardMessageComposer(newRoom.getId()));
                    }
                }
            });

            RoomManager.getInstance().addReloadListener(client.getPlayer().getEntity().getRoom().getId(), reloadListener);

            room.reload();
        }

        sendWhisper(result ? "Este quarto agora é do usuário " + targetUsername + "!" : "Aconteceu algum erro ao tentar transferir este quarto. Tente novamente!", client);
    }

    @Override
    public String getPermission() {
        return "transfer_room_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.transfer_room.description", "Transferir o quarto para um usuário.");
    }
}
