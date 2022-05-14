package com.cometproject.server.game.commands.staff.muting;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class RoomMuteCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getEntity().getRoom().hasRoomMute()) {
            for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                playerEntity.setRoomMuted(false);
            }

            for (final RoomEntity entity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                if (entity.getEntityType() == RoomEntityType.PLAYER) {
                    PlayerEntity playerEntity = (PlayerEntity) entity;


                    playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), Locale.getOrDefault("command.room.unmute", "Você pode falar agora :)")));
                }
            }

            client.getPlayer().getEntity().getRoom().setRoomMute(false);
        } else {
            for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                playerEntity.setRoomMuted(true);
            }

            client.getPlayer().getEntity().getRoom().setRoomMute(true);

            for (final RoomEntity entity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                if (entity.getEntityType() == RoomEntityType.PLAYER) {
                    PlayerEntity playerEntity = (PlayerEntity) entity;
                        playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), Locale.getOrDefault("command.room.muted", "Um membro da equipe mutou o quarto.")));
                }
            }
        }

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s mutou o quarto '%b'"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%b", client.getPlayer().getEntity().getRoom().getData().getName());

    }

    @Override
    public String getPermission() {
        return "roommute_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roommute.description");
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return true;
    }
}
