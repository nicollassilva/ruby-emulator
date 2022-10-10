package com.cometproject.server.game.commands.vip;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TransformCommand extends ChatCommand {
    public static void composeTransformation(IComposer msg, String[] transformationData, PlayerEntity entity) {
        // TODO: create global composer for entity types maybe
        msg.writeInt(entity.getPlayerId());
        msg.writeString(entity.getUsername());
        msg.writeString(entity.getMotto());
        msg.writeString(transformationData[0]);
        msg.writeInt(entity.getId());

        msg.writeInt(entity.getPosition().getX());
        msg.writeInt(entity.getPosition().getY());
        msg.writeDouble(entity.getPosition().getZ());

        msg.writeInt(0); // 2 = user 4 = bot 0 = pet ??????
        msg.writeInt(2); // 1 = user 2 = pet 3 = bot ??????n

        msg.writeInt(Integer.parseInt(transformationData[1]));
        msg.writeInt(entity.getPlayerId());
        msg.writeString(entity.getUsername());
        msg.writeInt(1);
        msg.writeBoolean(true); // has saddle
        msg.writeBoolean(false); // has rider?

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeString("");
    }

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper(Locale.getOrDefault("command.transform.none", "Oops! Which pet do you want to be?"), client);
            return;
        }

        String option = params[0];

        if (option.equalsIgnoreCase("lista")) {
            final List<String> names = new ArrayList<>(PetManager.getInstance().getTransformablePets().keySet());

            sendAlert(Locale.getOrDefault("command.transform.list", "Lista de pets disponíveis:\n%pets").replace("%pets", String.join("\n", names)), client);
            return;
        }

        if (option.equalsIgnoreCase("human")) {
            client.getPlayer().getEntity().removeAttribute("transformation");
        } else {
            final String data = PetManager.getInstance().getTransformationData(option.toLowerCase());

            if (data == null || data.isEmpty()) {
                sendWhisper(Locale.getOrDefault("command.transform.notexists", "Ops! Esse nome de Pet não existe."), client);
                return;
            }

            client.getPlayer().getEntity().setAttribute("transformation", data);
        }

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(client.getPlayer().getEntity().getId()));
        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(client.getPlayer().getEntity()));

        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "transform_command";
    }

    @Override
    public String getParameter() {
        return "(nome do pet)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.transform.description", "Transforme-se em um Pet");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
