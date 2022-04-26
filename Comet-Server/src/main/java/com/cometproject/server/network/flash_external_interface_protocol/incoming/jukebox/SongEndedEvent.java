package com.cometproject.server.network.flash_external_interface_protocol.incoming.jukebox;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.YoutubeJukeboxComponent;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.PlaySongComposer;

public class SongEndedEvent extends IncomingExternalInterfaceMessage<SongEndedEvent.JSONSongEndedEvent> {

    public SongEndedEvent() {
        super(JSONSongEndedEvent.class);
    }

    @Override
    public void handle(Session client, JSONSongEndedEvent message) {
        Room room = client.getPlayer().getEntity().getRoom();
        if(room == null)
            return;

        if(room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            YoutubeJukeboxComponent playlist= room.getYoutubeJukebox();
            if(playlist.getCurrentIndex() == message.currentIndex) {
                playlist.nextSong();
                playlist.setPlaying(true);
                PlaySongComposer playSongComposer = new PlaySongComposer(playlist.getCurrentIndex());
                room.getEntities().broadcastMessage(new JavascriptCallbackMessageComposer(playSongComposer));
                room.getEntities().broadcastMessage(playlist.getNowPlayingBubbleAlert());
            }
        }
    }

    static class JSONSongEndedEvent {
        public int currentIndex;
    }
}
