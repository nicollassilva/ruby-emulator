package com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox;

import com.cometproject.server.game.rooms.types.components.YoutubeJukeboxComponent;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JukeboxComposer extends OutgoingExternalInterfaceMessage {
    public JukeboxComposer(YoutubeJukeboxComponent playlist) {
        super("jukebox_player");
        JsonArray playlistJson = new JsonArray();
        for (YoutubeJukeboxComponent.YoutubeVideo video:playlist.getPlaylist()) {
            JsonObject song = new JsonObject();
            song.add("name", new JsonPrimitive(video.name));
            song.add("videoId", new JsonPrimitive(video.videoId));
            song.add("channel", new JsonPrimitive(video.channel));
            playlistJson.add(song);
        }
        this.data.add("playlist", playlistJson);
    }
}
