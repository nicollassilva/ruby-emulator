package com.cometproject.server.network.flash_external_interface_protocol.incoming.jukebox;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.YoutubeJukeboxComponent;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.AddSongComposer;

public class AddSongEvent extends IncomingExternalInterfaceMessage<AddSongEvent.JSONAddSong> {

    public AddSongEvent() {
        super(JSONAddSong.class);
    }

    @Override
    public void handle(Session client, JSONAddSong message) {
        Room room = client.getPlayer().getEntity().getRoom();
        if(room == null)
            return;
        if(room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            YoutubeJukeboxComponent playlist= room.getYoutubeJukebox();
            YoutubeJukeboxComponent.YoutubeVideo song = new YoutubeJukeboxComponent.YoutubeVideo(message.name, message.videoId, message.channel);
            playlist.addSong(song);
            AddSongComposer addSongComposer = new AddSongComposer(song);
            room.getEntities().broadcastMessage(new JavascriptCallbackMessageComposer(addSongComposer));
        }
    }

    public static class JSONAddSong {
        public String name;
        public String videoId;
        public String channel;
    }
}
