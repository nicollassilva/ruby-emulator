package com.cometproject.server.network.battleball.incoming.room;

import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.battleball.gameserver.GameServer;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.messages.outgoing.room.engine.HeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RelativeHeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class SetBuildToolEvent extends IncomingEvent {

    private static Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {

        HashMap<String, String> player = GameServer.userMap.get(this.session);
        Session client = (Session) SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));


        if(!client.getPlayer().getEntity().hasAttribute("build.activated")) return;

        if(this.data.getJSONObject("data").getString("type").equals("setz")) {
            if (this.data.getJSONObject("data").get("value") instanceof Double || this.data.getJSONObject("data").get("value") instanceof Integer || this.data.getJSONObject("data").get("value") instanceof Float) {

                double value = this.data.getJSONObject("data").getDouble("value");

                client.getPlayer().getEntity().setAttribute("setz.height", value);
                for (RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {

                    for (RoomTile tile : pTile) {

                        if (tile != null) {
                            tile.setMagicTile(true);

                            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                        }
                    }
                }
                //client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "Hauteur de construction mise à " + value + "."));
                client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom(), true, value));
                client.sendQueue(new RelativeHeightmapMessageComposer(client.getPlayer().getEntity().getRoom().getModel()));

            } else {
                for (RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {

                    for (RoomTile tile : pTile) {

                        if (tile != null) {
                            tile.reload();

                            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                        }
                    }
                }
                client.getPlayer().getEntity().removeAttribute("setz.height");
                client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom()));
                client.sendQueue(new RelativeHeightmapMessageComposer(client.getPlayer().getEntity().getRoom().getModel()));

            }
        }

        if(this.data.getJSONObject("data").getString("type").equals("rotation")) {
            if(this.data.getJSONObject("data").get("value") instanceof Integer) {
                int value = this.data.getJSONObject("data").getInt("value");

                if (value > 7 || value < 0) return;

                client.getPlayer().getEntity().setAttribute("rotation.height", value);
                //client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "Rotation mise à " + value + "."));

            } else {
                client.getPlayer().getEntity().removeAttribute("rotation.height");
            }
        }

        if(this.data.getJSONObject("data").getString("type").equals("state")) {
            if(this.data.getJSONObject("data").get("value") instanceof Integer) {
                int value = this.data.getJSONObject("data").getInt("value");

                if (value > 100 || value < 0) return;

                client.getPlayer().getEntity().setAttribute("state.height", value);
                //client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "État du mobis mis à " + value + "."));

            } else {
                client.getPlayer().getEntity().removeAttribute("state.height");
            }
        }
    }
}

