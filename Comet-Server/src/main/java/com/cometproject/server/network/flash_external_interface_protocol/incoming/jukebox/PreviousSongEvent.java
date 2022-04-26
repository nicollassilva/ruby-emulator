package com.cometproject.server.network.flash_external_interface_protocol.incoming.jukebox;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.YoutubeJukeboxComponent;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.PlaySongComposer;

public class PreviousSongEvent extends IncomingExternalInterfaceMessage<PreviousSongEvent.JSONPreviousSongEvent> {
    public PreviousSongEvent() {
        super(JSONPreviousSongEvent.class);
    }

    @Override
    public void handle(Session client, JSONPreviousSongEvent message) {
        Room currentRoom = client.getPlayer().getEntity().getRoom();
        if(currentRoom == null)
            return;
        if(currentRoom.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            YoutubeJukeboxComponent playlist= currentRoom.getYoutubeJukebox();
            playlist.prevSong();
            playlist.setPlaying(true);
            PlaySongComposer playSongComposer = new PlaySongComposer(playlist.getCurrentIndex());
            currentRoom.getEntities().broadcastMessage(new JavascriptCallbackMessageComposer(playSongComposer));
            currentRoom.getEntities().broadcastMessage(playlist.getNowPlayingBubbleAlert());
        }
    }

    public static class JSONPreviousSongEvent {
        public int currentIndex;
    }
}
