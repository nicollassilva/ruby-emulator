package com.cometproject.server.network.flash_external_interface_protocol.incoming.jukebox;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.YoutubeJukeboxComponent;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.RemoveSongComposer;

public class RemoveSongEvent extends IncomingExternalInterfaceMessage<RemoveSongEvent.JSONRemoveSongEvent> {
    public RemoveSongEvent() {
        super(JSONRemoveSongEvent.class);
    }

    @Override
    public void handle(Session client, JSONRemoveSongEvent message) {
        Room room = client.getPlayer().getEntity().getRoom();
        if(room == null)
            return;

        if(room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            YoutubeJukeboxComponent playlist= room.getYoutubeJukebox();
            playlist.removeSong(message.index);
            room.getEntities().broadcastMessage(new JavascriptCallbackMessageComposer(new RemoveSongComposer(message.index)));
        }
    }

    public static class JSONRemoveSongEvent {
        public int index;
    }
}
