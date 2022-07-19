package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;

public class TeleportToMeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendWhisper("Por favor, digite o nick do usuário que quer teleportar.", client);
            return;
        }

        final Session targetClient = NetworkManager.getInstance().getSessions().getByPlayerUsername(params[0]);

        if (targetClient == null) {
            sendWhisper("O usuário informado não está online.", client);
            return;
        }

        if (targetClient.getPlayer().getId() == client.getPlayer().getId()) {
            sendWhisper("Você não pode se teleportar a si mesmo.", client);
            return;
        }

        final RoomEntity targetEntity = targetClient.getPlayer().getEntity();

        if (targetEntity == null || targetEntity.getRoom().getId() != client.getPlayer().getEntity().getRoom().getId()) {
            sendWhisper("O usuário informado não está neste quarto.", client);
            return;
        }

        final RoomTile currentTargetTile = targetEntity.getTile();

        targetEntity.cancelWalk();

        if(currentTargetTile != null) {
            targetEntity.removeFromTile(currentTargetTile);

            if(currentTargetTile.getTopItemInstance() != null) {
                currentTargetTile.getTopItemInstance().onEntityStepOff(targetEntity);
            }
        }

        targetEntity.warpImmediately(client.getPlayer().getEntity().getPosition());

        sendWhisper("O usuário " + targetClient.getPlayer().getData().getUsername() + " foi teleportado para perto de você!", client);
    }

    @Override
    public String getPermission() {
        return "teleport_to_me_command";
    }

    @Override
    public String getParameter() {
        return "(usuário)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.teleport_to_me.description", "Teleportar usuário para perto.");
    }
}
