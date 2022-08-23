package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.engine.HeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RelativeHeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class SetZCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper("Você digitou errado o comando. :lj [valor] ~ :lj stop.", client);
            return;
        }

        if (params[0].equals("stop")) {
            if (client.getPlayer().getEntity().hasAttribute("setz.height")) {
                client.getPlayer().getEntity().removeAttribute("setz.height");
                sendWhisper("Altura desativada.", client);

                for (final RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {
                    for (final RoomTile tile : pTile) {

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

            sendWhisper("Altura desativada.", client);
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            try {
                final double height = Double.parseDouble(params[0]);

                if (height > 100 || height < -100) {
                    sendWhisper("A altura deve estar entre -100 e 100.", client);
                    return;
                }

                for (final RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {
                    for (final RoomTile tile : pTile) {
                        if (tile != null) {
                            tile.setMagicTile(true);

                            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                        }
                    }
                }

                sendWhisper("A altura estabelecida foi " + height + ".", client);
                client.getPlayer().getEntity().setAttribute("setz.height", height);

                client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom(), true, height));
                client.sendQueue(new RelativeHeightmapMessageComposer(client.getPlayer().getEntity().getRoom().getModel()));

                client.flush();
            } catch (Exception e) {
                sendWhisper("Você digitou errado o comando. :lj [valor] ~ :lj stop.", client);
            }
        }
    }

    @Override
    public String getPermission() {
        return "setz_command";
    }

    @Override
    public String getParameter() {
        return "(número)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.setz.description", "Use este comando em vez de um bloco empilhável, alturas disponíveis em um intervalo entre -100 e 100.");
    }
}