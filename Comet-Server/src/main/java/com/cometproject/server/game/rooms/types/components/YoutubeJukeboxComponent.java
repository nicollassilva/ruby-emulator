package com.cometproject.server.game.rooms.types.components;

import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.*;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.ArrayList;

public class YoutubeJukeboxComponent {
    private Room room;

    private final ArrayList<YoutubeVideo> playlist;
    private int current;
    private boolean playing;

    public YoutubeJukeboxComponent(Room room) {
        this.room = room;
        playlist = new ArrayList<>();
        current = 0;
        playing = false;
    }

    public YoutubeVideo nextSong() {
        if(current < playlist.size() - 1)
            this.current++;
        else
            this.current = 0;
        return playlist.get(current);
    }

    public YoutubeVideo prevSong() {
        if(current > 0) {
            current--;
        }
        return playlist.get(current);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void addSong(YoutubeVideo song) {
        playlist.add(song);
    }

    public YoutubeVideo removeSong(int index) {
        YoutubeVideo res = null;
        if(playlist.size() - 1 >= index)
            res = this.playlist.remove(index);
        if(playlist.size() == 0) this.setPlaying(false);
        if(index == this.getCurrentIndex()) {
            if(index > this.playlist.size() - 1 && this.playlist.size() > 0) {
                this.current = this.playlist.size() - 1;
            }
        }
        else if(index < this.getCurrentIndex() && this.getCurrentIndex() > 0) {
            this.current--;
        }
        return res;
    }

    public YoutubeVideo getCurrentSong() {
        return playlist.get(current);
    }

    public int getCurrentIndex() {
        return current;
    }

    public ArrayList<YoutubeVideo> getPlaylist() {
        return playlist;
    }

    public static class YoutubeVideo {
        public String name;
        public String videoId;
        public String channel;

        public YoutubeVideo(String name, String videoId, String channel) {
            this.name = name;
            this.videoId = videoId;
            this.channel = channel;
        }
    }

    public MessageComposer getNowPlayingBubbleAlert() {
        return new NotificationMessageComposer("music", "Now playing " + this.getCurrentSong().name);
    }

    public void onRoomEnter(Player player) {
        player.getSession().send(new JavascriptCallbackMessageComposer(new ChangeVolumeComposer(player.getSettings().getVolumes().getTraxVolume())));
        if(this.playlist.size() > 0) {
            player.getSession().send(new JavascriptCallbackMessageComposer(new PlaylistComposer(room.getYoutubeJukebox())));
            if(room.getYoutubeJukebox().isPlaying()) {
                player.getSession().send(new JavascriptCallbackMessageComposer(new PlaySongComposer(room.getYoutubeJukebox().getCurrentIndex())));
                player.getSession().send(room.getYoutubeJukebox().getNowPlayingBubbleAlert());
            }
        }
    }

    public void onRoomExit(Player player) {
        if(player == null || player.getSession() == null) return;

        player.getSession().send(new JavascriptCallbackMessageComposer(new PlayStopComposer(false)));
        player.getSession().send(new JavascriptCallbackMessageComposer(new DisposePlaylistComposer()));
    }

    public void dispose() {
        this.room = null;
        this.playlist.clear();
    }
}
