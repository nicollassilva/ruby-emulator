package com.cometproject.api.game.players.data;

import com.cometproject.api.game.players.data.types.IPlaylistItem;
import com.cometproject.api.game.players.data.types.IVolumeData;
import com.cometproject.api.game.players.data.types.IWardrobeItem;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;

import java.util.List;

public interface IPlayerSettings {

    IVolumeData getVolumes();

    boolean getHideOnline();

    boolean getHideInRoom();

    int talentTrackLevel(TalentTrackType type);

    boolean getAllowFriendRequests();

    void setAllowFriendRequests(boolean allowFriendRequests);

    boolean getAllowTrade();
    
    boolean getAllowFollow();
    
    boolean getAllowMimic();

    int getHomeRoom();

    int getCitizenLevel();

    int getHelperLevel();

    boolean allowedFollowToRoom();

    void setHomeRoom(int homeRoom);

    int getBubbleId();

    void setBubbleId(int bubbleId);

    List<IWardrobeItem> getWardrobe();

    void setWardrobe(List<IWardrobeItem> wardrobe);

    List<IPlaylistItem> getPlaylist();

    boolean isUseOldChat();

    void setUseOldChat(boolean useOldChat);

    boolean ignoreEvents();

    void setIgnoreInvites(boolean ignoreInvites);
}
