package com.cometproject.server.network.flash_external_interface_protocol.incoming.jukebox;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.YoutubeJukeboxComponent;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.PlayStopComposer;

public class PlayStopEvent extends IncomingExternalInterfaceMessage<PlayStopEvent.JSONPlayStopEvent> {
    public PlayStopEvent() {
        super(JSONPlayStopEvent.class);
    }

    @Override
    public void handle(Session client, JSONPlayStopEvent message) {
        Room room = client.getPlayer().getEntity().getRoom();
        if(room == null)
            return;

        if(room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            YoutubeJukeboxComponent playlist= room.getYoutubeJukebox();
            playlist.setPlaying(message.play);
            room.getEntities().broadcastMessage(new JavascriptCallbackMessageComposer(new PlayStopComposer(message.play)));
            if(message.play) {
                room.getEntities().broadcastMessage(playlist.getNowPlayingBubbleAlert());
            }
        }
    }

    public static class JSONPlayStopEvent {
        public boolean play;
    }
}
