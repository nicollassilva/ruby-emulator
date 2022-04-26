package com.cometproject.server.network.battleball.incoming.room;

import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.battleball.Server;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.HeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RelativeHeightmapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class CloseBuildToolEvent extends IncomingEvent {

    private static Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
        final HashMap<String, String> player = Server.userMap.get(this.session);
        final Session client = (Session) SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));

        if(!client.getPlayer().getEntity().hasAttribute("build.activated")) return;

        client.getPlayer().getEntity().removeAttribute("build.activated");
        client.getPlayer().getEntity().removeAttribute("setz.height");
        client.getPlayer().getEntity().removeAttribute("rotation.height");
        client.getPlayer().getEntity().removeAttribute("state.height");

        for (final RoomTile[] pTile : client.getPlayer().getEntity().getRoom().getMapping().getTiles()) {
            for (final RoomTile tile : pTile) {
                if (tile != null) {
                    tile.reload();

                    client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                }
            }
        }

        client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.build_websocket.disabled", "Mode construction désactivé.")));
        client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom()));
        client.sendQueue(new RelativeHeightmapMessageComposer(client.getPlayer().getEntity().getRoom().getModel()));
    }
}
