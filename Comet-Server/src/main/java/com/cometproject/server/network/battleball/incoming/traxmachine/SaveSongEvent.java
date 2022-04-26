package com.cometproject.server.network.battleball.incoming.traxmachine;

import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.game.items.music.TraxMachineSong;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.battleball.Server;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;
import com.cometproject.server.storage.queries.rooms.TraxMachineDao;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SaveSongEvent extends IncomingEvent {
    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
        final HashMap<String, String> player = Server.userMap.get(this.session);
        final Session client = (Session) SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));

        if(client == null) return;

        if(client.getPlayer().getEntity().getRoom().getData().getOwnerId() != client.getPlayer().getData().getId()) return;

        final String songData = this.data.getJSONObject("data").getString("songData");

        if(songData.isEmpty()) return;

        final Pattern songDataHasNonWordChar = Pattern.compile("[^\\w\"{}\\[\\],:]");

        System.out.println(songDataHasNonWordChar.matcher(songData));
        if (songDataHasNonWordChar.matcher(songData).find()) {
            client.send(new NotificationMessageComposer("traxmachine", "Você foi detectado."));
            return;
        }

        if(songData.length() > 15000) {
            client.send(new NotificationMessageComposer("traxmachine", "Sua música está muito grande!"));
            return;
        }

        final TraxMachineSong roomSong = RoomManager.getInstance().getTraxMachineSongFromUserAndSongId(
                client.getPlayer().getId(),
                client.getPlayer().getEntity().getRoom().getData().getSongId()
        );

        final TraxMachineSong songSaved;

        if(roomSong == null) {
            songSaved = TraxMachineDao.saveSong(client, songData);
        } else {
            songSaved = TraxMachineDao.updateSong(client, roomSong, songData);
        }

        if(songSaved == null) return;

        client.getPlayer().getEntity().getRoom().getData().setSongId(songSaved.getId());

        RoomDao.roomUpdateSongId(
                client.getPlayer().getEntity().getRoom().getId(),
                songSaved.getId()
        );

        client.send(new NotificationMessageComposer("traxmachine", "Música salva com sucesso!"));
    }
}
