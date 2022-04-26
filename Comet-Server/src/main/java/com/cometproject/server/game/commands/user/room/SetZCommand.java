package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.engine.HeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RelativeHeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.List;


public class SetZCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

        if (params.length != 1) {
            sendWhisper("Vous avez mal formulé la commande. :setz [valeur] ~ :setz stop.", client);
            return;
        }

        if(params[0].equals("stop")) {
            if(client.getPlayer().getEntity().hasAttribute("setz.height")) {
                client.getPlayer().getEntity().removeAttribute("setz.height");
                sendWhisper("Setz desactivado.", client);
                for (RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {

                    for (RoomTile tile : pTile) {

                        if (tile != null) {
                            tile.reload();

                            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                        }
                    }
                }
                client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom()));
                client.sendQueue(new RelativeHeightmapMessageComposer(client.getPlayer().getEntity().getRoom().getModel()));
                client.flush();
                return;
            }
            sendWhisper("Setz desactivado", client);
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            try {
                double height = Double.parseDouble(params[0]);

                if (height > 100 || height < -100) {
                    sendWhisper("La altura debe ser entre 100 y -100.", client);
                    return;
                }

                for (RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {

                    for (RoomTile tile : pTile) {

                        if (tile != null) {
                            tile.setMagicTile(true);

                            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                        }
                    }
                }


                sendWhisper("Estableciste la altura en " + height + ".", client);
                client.getPlayer().getEntity().setAttribute("setz.height", height);
                client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom(), true, height));
                client.sendQueue(new RelativeHeightmapMessageComposer(client.getPlayer().getEntity().getRoom().getModel()));
                client.flush();


            } catch (Exception e) {
                sendWhisper("Vous avez mal formulé la commande. :setz [valeur] ~ :setz stop.", client);
            }
        }


    }

    @Override
    public String getPermission() {
        return "commands_command";
    }

    @Override
    public String getParameter() {
        return "(altura)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.setz.description", "Usa este comando en lugar de una baldosa apilable, alturas disponibles en un rango entre -100 y 100");
    }
}