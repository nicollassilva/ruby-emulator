package com.cometproject.server.game.players.types;

import com.cometproject.api.game.players.data.IPlayerSettings;
import com.cometproject.api.game.players.data.types.IPlaylistItem;
import com.cometproject.api.game.players.data.types.IVolumeData;
import com.cometproject.api.game.players.data.types.IWardrobeItem;
import com.cometproject.api.game.players.data.types.MentionType;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.components.types.settings.PlaylistItem;
import com.cometproject.server.game.players.components.types.settings.VolumeData;
import com.cometproject.server.game.players.components.types.settings.WardrobeItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PlayerSettings implements IPlayerSettings {
    private final VolumeData volumes;

    private List<IWardrobeItem> wardrobe;
    private final List<IPlaylistItem> playlist;

    private final boolean hideOnline;
    private final boolean hideInRoom;
    private boolean allowFriendRequests;
    private boolean allowTrade;
    private boolean allowFollow;
    private boolean allowMimic;
    private boolean pinSuccess = false;
    private int pinTries;
    private String personalPin;
    private int nuxStatus;

    private final int citizenLevel;
    private final int helperLevel;

    private int homeRoom;
    private boolean useOldChat;
    private boolean ignoreInvites;

    private int navigatorX;
    private int navigatorY;
    private int navigatorHeight;
    private int navigatorWidth;
    private boolean roomCameraFollow;
    private boolean navigatorShowSearches;

    private boolean disableWhisper;
    private int bubbleId;
    private boolean ignoreEvents;
    private boolean claimedGoal;

    private Player player;

    private boolean sendLoginNotification;
    private MentionType mentionType;

    public PlayerSettings(ResultSet data, boolean isLogin, Player player) throws SQLException {
        if (isLogin) {
            String volumeData = data.getString("playerSettings_volume");

            if (volumeData != null && volumeData.startsWith("{")) {
                volumes = JsonUtil.getInstance().fromJson(volumeData, VolumeData.class);
            } else {
                volumes = new VolumeData(100, 100, 100);
            }

            this.hideOnline = data.getString("playerSettings_hideOnline").equals("1");
            this.hideInRoom = data.getString("playerSettings_hideInRoom").equals("1");
            this.allowFriendRequests = data.getString("playerSettings_allowFriendRequests").equals("1");
            this.allowTrade = data.getString("playerSettings_allowTrade").equals("1");
            this.allowFollow = data.getString("playerSettings_allowFollow").equals("1");
            this.allowMimic = data.getString("playerSettings_allowMimic").equals("1");
            this.nuxStatus = data.getInt("playerSettings_nux");

            this.citizenLevel = data.getInt("playerSettings_citizenLevel");
            this.helperLevel = data.getInt("playerSettings_helperLevel");

            this.homeRoom = data.getInt("playerSettings_homeRoom");
            this.player = player;

            String wardrobeText = data.getString("playerSettings_wardrobe");

            if (wardrobeText == null || wardrobeText.isEmpty()) {
                wardrobe = new ArrayList<>();
            } else {
                wardrobe = JsonUtil.getInstance().fromJson(wardrobeText, new TypeToken<ArrayList<WardrobeItem>>() {
                }.getType());
            }

            String playlistText = data.getString("playerSettings_playlist");

            if (playlistText == null || playlistText.isEmpty()) {
                playlist = new ArrayList<>();
            } else {
                playlist = JsonUtil.getInstance().fromJson(playlistText, new TypeToken<ArrayList<PlaylistItem>>() {
                }.getType());
            }

            this.useOldChat = data.getString("playerSettings_useOldChat").equals("1");
            this.ignoreInvites = data.getString("playerSettings_ignoreInvites").equals("1");

            this.navigatorX = data.getInt("playerSettings_navigatorX");
            this.navigatorY = data.getInt("playerSettings_navigatorY");
            this.navigatorHeight = data.getInt("playerSettings_navigatorHeight");
            this.navigatorWidth = data.getInt("playerSettings_navigatorWidth");
            this.personalPin = data.getString("playerSettings_personalPin");

            this.navigatorShowSearches = data.getString("playerSettings_navigatorShowSearches").equals("1");

            this.ignoreEvents = data.getString("playerSettings_ignoreEvents").equalsIgnoreCase("1");
            this.disableWhisper = data.getString("playerSettings_disableWhisper").equalsIgnoreCase("1");
            this.bubbleId = data.getInt("playerSettings_bubbleId");
            this.sendLoginNotification = data.getString("playerSettings_sendLoginNotif").equalsIgnoreCase("1");
            this.mentionType = MentionType.valueOf(data.getString("playerSettings_mentionType"));
            this.roomCameraFollow = data.getString("playerSettings_roomCameraFollow").equalsIgnoreCase("1");
            this.claimedGoal = data.getString("playerSettings_claimedGoal").equalsIgnoreCase("1");
        } else {
            String volumeData = data.getString("volume");

            if (volumeData != null && volumeData.startsWith("{")) {
                volumes = JsonUtil.getInstance().fromJson(volumeData, VolumeData.class);
            } else {
                volumes = new VolumeData(100, 100, 100);
            }

            this.hideOnline = data.getString("hide_online").equals("1");
            this.hideInRoom = data.getString("hide_inroom").equals("1");
            this.personalPin = data.getString("personal_pin");
            this.allowFriendRequests = data.getString("allow_friend_requests").equals("1");
            this.allowTrade = data.getString("allow_trade").equals("1");
            this.allowFollow = data.getString("allow_follow").equals("1");
            this.allowFollow = data.getString("allow_mimic").equals("1");
            this.nuxStatus = data.getInt("nux");
            this.roomCameraFollow = data.getString("camera_follow").equals("1");
            this.claimedGoal = data.getString("claimed_goal").equals("1");

            this.homeRoom = data.getInt("home_room");

            this.citizenLevel = data.getInt("citizen_level");
            this.helperLevel = data.getInt("helper_level");

            String wardrobeText = data.getString("wardrobe");

            if (wardrobeText == null || wardrobeText.isEmpty()) {
                wardrobe = new ArrayList<>();
            } else {
                wardrobe = JsonUtil.getInstance().fromJson(wardrobeText, new TypeToken<ArrayList<WardrobeItem>>() {
                }.getType());
            }

            String playlistText = data.getString("playlist");

            if (playlistText == null || playlistText.isEmpty()) {
                playlist = new ArrayList<>();
            } else {
                playlist = JsonUtil.getInstance().fromJson(playlistText, new TypeToken<ArrayList<PlaylistItem>>() {
                }.getType());
            }

            this.useOldChat = data.getString("chat_oldstyle").equals("1");
            this.ignoreInvites = data.getString("ignore_invites").equals("1");

            this.navigatorX = data.getInt("navigator_x");
            this.navigatorY = data.getInt("navigator_y");
            this.navigatorHeight = data.getInt("navigator_height");
            this.navigatorWidth = data.getInt("navigator_width");

            this.navigatorShowSearches = data.getString("navigator_show_searches").equals("1");

            this.ignoreEvents = data.getString("ignore_events").equals("1");
            this.disableWhisper = data.getString("disable_whisper").equals("1");
            this.bubbleId = data.getInt("bubble_id");
            this.sendLoginNotification = data.getString("send_login_notif").equals("1");
            this.mentionType = MentionType.valueOf(data.getString("mention_type"));
        }

        flush();
    }

    public PlayerSettings() {
        this.volumes = new VolumeData(100, 100, 100);
        this.hideInRoom = false;
        this.homeRoom = 0;
        this.hideOnline = false;
        this.allowFriendRequests = true;
        this.allowTrade = true;
        this.allowFollow = true;
        this.allowMimic = true;
        this.wardrobe = new ArrayList<>();
        this.playlist = new ArrayList<>();
        this.useOldChat = false;
        this.roomCameraFollow = false;
        this.claimedGoal = false;
        this.nuxStatus = 0;
        this.citizenLevel = 0;
        this.helperLevel = 0;
        this.navigatorX = 68;
        this.navigatorY = 42;
        this.navigatorWidth = 425;
        this.navigatorHeight = 592;
        this.navigatorShowSearches = false;
        this.disableWhisper = false;
        this.bubbleId = 0;
        this.mentionType = MentionType.ALL;
    }

    public IVolumeData getVolumes() {
        return this.volumes;
    }

    public boolean getHideOnline() {
        return this.hideOnline;
    }

    public boolean getHideInRoom() {
        return this.hideInRoom;
    }

    public boolean verifyGoal() { return this.claimedGoal; }

    public void setClaimedGoal(boolean claimedGoal) {
        this.claimedGoal = claimedGoal;
    }

    public int getNuxStatus() { return this.nuxStatus; }

    public void incrementNuxStatus() { this.nuxStatus++; }

    public boolean roomCameraFollow() {
        return this.roomCameraFollow;
    }

    public void setRoomCameraFollow(boolean roomCameraFollow) {
        this.roomCameraFollow = roomCameraFollow;
    }

    public boolean getAllowFriendRequests() {
        return this.allowFriendRequests;
    }

    public void setAllowFriendRequests(boolean allowFriendRequests) {
        this.allowFriendRequests = allowFriendRequests;

        flush();
    }

    public int getBubbleId() {
        return this.bubbleId;
    }

    public void setBubbleId(int bubbleId) {
        this.bubbleId = bubbleId;
    }

    public boolean getAllowTrade() {
        return this.allowTrade;
    }

    public void setAllowTrade(boolean allowTrade) {
        this.allowTrade = allowTrade;

        flush();
    }

    public boolean getAllowFollow() {
        return this.allowFollow;
    }

    public boolean getAllowMimic() {
        return this.allowMimic;
    }

    public void setAllowMimic(boolean value) {
        this.allowMimic = value;

        flush();
    }

    public int getHomeRoom() {
        return this.homeRoom;
    }

    public void setHomeRoom(int homeRoom) {
        this.homeRoom = homeRoom;

        flush();
    }

    public List<IWardrobeItem> getWardrobe() {
        return wardrobe;
    }

    public void setWardrobe(List<IWardrobeItem> wardrobe) {
        this.wardrobe = wardrobe;

        flush();
    }

    public List<IPlaylistItem> getPlaylist() {
        return playlist;
    }

    public boolean isUseOldChat() {
        return this.useOldChat;
    }

    public void setUseOldChat(boolean useOldChat) {
        this.useOldChat = useOldChat;

        flush();
    }

    public boolean ignoreEvents() {
        return ignoreEvents;
    }

    public void setIgnoreInvites(boolean ignoreInvites) {
        this.ignoreInvites = ignoreInvites;
    }

    public boolean getIgnoreInvites() {
        return this.ignoreInvites;
    }

    public int getNavigatorX() {
        return navigatorX;
    }

    public void setNavigatorX(int navigatorX) {
        this.navigatorX = navigatorX;
    }

    public int getNavigatorY() {
        return navigatorY;
    }

    public void setNavigatorY(int navigatorY) {
        this.navigatorY = navigatorY;
    }

    public int getNavigatorHeight() {
        return navigatorHeight;
    }

    public void setNavigatorHeight(int navigatorHeight) {
        this.navigatorHeight = navigatorHeight;
    }

    public int getNavigatorWidth() {
        return navigatorWidth;
    }

    public void setNavigatorWidth(int navigatorWidth) {
        this.navigatorWidth = navigatorWidth;
    }

    public boolean getNavigatorShowSearches() {
        return navigatorShowSearches;
    }

    public void setNavigatorShowSearches(boolean navigatorShowSearches) {
        this.navigatorShowSearches = navigatorShowSearches;
    }

    public void setIgnoreEvents(boolean ignoreEvents) {
        this.ignoreEvents = ignoreEvents;
    }

    public boolean disableWhisper() {
        return disableWhisper;
    }

    public void setDisableWhisper(boolean disableWhisper) {
        this.disableWhisper = disableWhisper;

        flush();
    }

    public MentionType getMentionType() {
        return mentionType;
    }

    public void setMentionType(MentionType mentionType) {
        this.mentionType = mentionType;
    }

    public boolean sendLoginNotif() {
        return sendLoginNotification;
    }

    public void setSendLoginNotification(boolean sendLoginNotification) {
        this.sendLoginNotification = sendLoginNotification;
    }

    public JsonObject toJson() {
        final JsonObject coreObject = new JsonObject();
        final JsonArray wardrobeArray = new JsonArray();

        coreObject.add("volumes", volumes.toJson());

        for(IWardrobeItem wardrobeItem : wardrobe) {
            wardrobeArray.add(wardrobeItem.toJson());
        }

        coreObject.add("wardrobe", wardrobeArray);

        coreObject.addProperty("hideOnline", hideOnline);
        coreObject.addProperty("hideInRoom", hideInRoom);
        coreObject.addProperty("allowFriendRequests", allowFriendRequests);
        coreObject.addProperty("allowTrade", allowTrade);
        coreObject.addProperty("allowFollow", allowFollow);
        coreObject.addProperty("allowMimic", allowMimic);
        coreObject.addProperty("homeRoom", homeRoom);
        coreObject.addProperty("useOldChat", useOldChat);
        coreObject.addProperty("ignoreInvites", ignoreInvites);
        coreObject.addProperty("disableWhisper", disableWhisper);
        coreObject.addProperty("ignoreEvents", ignoreEvents);

        return coreObject;
    }

    public Player getPlayer() {
        return player;
    }

    public void flush() {
        if (player != null) {
            this.getPlayer().flush();
        }
    }

    public boolean allowedFollowToRoom() {
        return !this.getHideOnline() &&
                !this.getHideInRoom() &&
                this.getAllowFollow() &&
                this.getPlayer() != null &&
                this.getPlayer().getEntity() != null;
    }

    public String getPersonalPin() {
        return personalPin;
    }

    public boolean isPinSuccess() {
        return pinSuccess;
    }

    public int getPinTries() {
        return pinTries;
    }

    public void incrementPinTries() {
        this.pinTries++;
    }

    public void setPinSucces() {
        this.pinSuccess = true;
    }

    public int getCitizenLevel() {
        return this.citizenLevel;
    }

    public int getHelperLevel() {
        return this.helperLevel;
    }

    public int talentTrackLevel(TalentTrackType type) {
        if (type == TalentTrackType.CITIZENSHIP) {
            return this.getCitizenLevel();
        }

        if (type == TalentTrackType.HELPER) {
            return this.getHelperLevel();
        }

        return -1;
    }
}