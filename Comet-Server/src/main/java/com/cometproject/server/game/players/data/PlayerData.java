package com.cometproject.server.game.players.data;

import com.cometproject.api.game.players.data.IPlayerData;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.players.types.PlayerBanner;
import com.cometproject.server.game.utilities.validator.PlayerFigureValidator;
import com.cometproject.server.storage.queries.player.PlayerDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerData implements IPlayerData {
    public static final String DEFAULT_FIGURE = "hr-100-61.hd-180-2.sh-290-91.ch-210-66.lg-270-82";

    private static final Logger log = LogManager.getLogger(PlayerData.class.getName());

    private final int id;
    private int rank;

    private Player player;

    private String username;
    private String motto;
    private String figure;
    private String gender;
    private String email;

    private String ipAddress;

    private int credits;
    private int vipPoints;
    private int activityPoints;
    private int seasonalPoints;

    private final String regDate;
    private int lastVisit;
    private int regTimestamp;
    private int achievementPoints;
    private int xpPoints;

    private int favouriteGroup;

    private String temporaryFigure;

    private boolean vip;
    private int questId;

    private int timeMuted;
    private boolean ask = false;
    private String nameColour;
    private String tag;
    private String banner;
    private boolean emojiEnabled;
    private int gamesWin;
    private int bonusPoints;
    private final int endVipTimestamp;
    private int snowXp;
    private int kisses;

    private Session websocket_session;
    private boolean pass;
    private String machineID;

    private boolean changingName = false;

    private boolean flaggingUser = false;

    private Object tempData = null;
    private Map<String, PlayerBanner> banners;

    public PlayerData(int id, String username, String motto, String figure, String gender, String email, int rank, int credits, int vipPoints, int activityPoints,
                      int seasonalPoints, String reg, int lastVisit, boolean vip, int achievementPoints, int xpPoints, int regTimestamp, int favouriteGroup, String ipAddress, int questId, int timeMuted, String nameColour, String tag, boolean emojiEnabled, int gamesWin, int bonusPoints, int endVipTimestamp, int snowXp, int kisses, String banner, Player player) {
        this.id = id;
        this.username = username;
        this.motto = motto;
        this.figure = figure;
        this.rank = rank;
        this.credits = credits;
        this.vipPoints = vipPoints;
        this.activityPoints = activityPoints;
        this.seasonalPoints = seasonalPoints;
        this.gender = gender;
        this.vip = vip;
        this.achievementPoints = achievementPoints;
        this.xpPoints = xpPoints;
        this.email = email;
        this.regDate = reg;
        this.lastVisit = lastVisit;
        this.regTimestamp = regTimestamp;
        this.favouriteGroup = favouriteGroup;
        this.ipAddress = ipAddress;
        this.questId = questId;
        this.timeMuted = timeMuted;
        this.nameColour = nameColour;
        this.tag = tag;
        this.emojiEnabled = emojiEnabled;
        this.gamesWin = gamesWin;
        this.bonusPoints = bonusPoints;
        this.endVipTimestamp = endVipTimestamp;
        this.snowXp = snowXp;
        this.kisses = kisses;
        this.banner = banner;
        this.player = player;
        this.banners = new ConcurrentHashMap<>();
        this.loadPlayerBanners();

        flush();
    }

    public PlayerData(ResultSet data, Player player) throws SQLException {
        this(data.getInt("playerId"),
                data.getString("playerData_username"),
                data.getString("playerData_motto"),
                data.getString("playerData_figure"),
                data.getString("playerData_gender"),
                data.getString("playerData_email"),
                data.getInt("playerData_rank"),
                data.getInt("playerData_credits"),
                data.getInt("playerData_vipPoints"),
                data.getInt("playerData_activityPoints"),
                data.getInt("playerData_seasonalPoints"),
                data.getString("playerData_regDate"),
                data.getInt("playerData_lastOnline"),
                data.getString("playerData_vip").equals("1"),
                data.getInt("playerData_achievementPoints"),
                data.getInt("playerData_xpPoints"),
                data.getInt("playerData_regTimestamp"),
                data.getInt("playerData_favouriteGroup"),
                data.getString("playerData_lastIp"),
                data.getInt("playerData_questId"),
                data.getInt("playerData_timeMuted"),
                data.getString("playerData_nameColour"),
                data.getString("playerData_tag"),
                data.getBoolean("playerData_emojiEnabled"),
                data.getInt("playerData_gamesWin"),
                data.getInt("playerData_bonusPoints"),
                data.getInt("playerData_endVipTimestamp"),
                data.getInt("playerData_snowXp"),
                data.getInt("playerData_kisses"),
                data.getString("playerData_banner"), player);
        this.websocket_session = null;
        this.banners = new ConcurrentHashMap<>();

        this.loadPlayerBanners();
    }

    @Override
    public Object tempData() {
        return this.tempData;
    }

    @Override
    public void tempData(Object obj) {
        this.tempData = obj;
    }

    public void save() {
//        if(CometSettings.storagePlayerQueueEnabled) {
//            PlayerDataStorageQueue.getInstance().queueSave(this);
//        } else {
        this.saveNow();
//        }
    }

    public void saveNow() {
        PlayerDao.updatePlayerData(id, username, rank, motto, figure, credits, vipPoints, gender, favouriteGroup, activityPoints, seasonalPoints, questId, achievementPoints, xpPoints, nameColour, tag, emojiEnabled, gamesWin, bonusPoints, endVipTimestamp, snowXp, kisses, banner);
    }

    public void decreaseCredits(int amount) {
        this.credits -= amount;

        flush();
    }

    public void increaseCredits(int amount) {
        this.credits += amount;

        flush();
    }

    public void decreaseVipPoints(int points) {
        this.vipPoints -= points;

        flush();
    }

    public void increaseVipPoints(int points) {
        this.vipPoints += points;

        flush();
    }

    public void increaseActivityPoints(int points) {
        this.activityPoints += points;

        flush();
    }

    public void decreaseActivityPoints(int points) {
        this.activityPoints -= points;

        flush();
    }

    public void increaseSeasonalPoints(int points) {
        this.seasonalPoints += points;

        flush();
    }

    public void decreaseSeasonalPoints(int points) {
        this.seasonalPoints -= points;

        flush();
    }

    public void increaseAchievementPoints(int points) {
        this.achievementPoints += points;

        flush();
    }

    public void increaseXpPoints(int points) {
        this.xpPoints += points;

        int level = this.getPlayer().getStats().getLevel();
        boolean isLevelUp = this.xpPoints >= (level * 1500);

        if(isLevelUp) {
            this.getPlayer().getStats().incrementLevel();
            this.getPlayer().sendBubble("level_up", Locale.getOrDefault("level.up.text", "¡Acabas de subir a nivel %level%, recuerda que puedes recibir recompensas según tu nivel en la vista del hotel.").replace("%level%", level + 1 + ""));
            //WebSocketSessionManager.getInstance().sendMessage(this.getPlayer().getSession().getWsChannel(), new BattlePassWebPacket("sendBattlePass", this.getFigure(), this.getUsername(), this.getPlayer().getStats().getLevel(), this.getXpPoints()));
        }

        flush();
    }

    public void increaseBonusPoints(int bonusPoints) {
        this.bonusPoints += bonusPoints;

        flush();
    }

    public void increaseGamesWin(int gamesWin) {
        this.gamesWin += gamesWin;

        flush();
    }

    public int getId() {
        return this.id;
    }

    public int getEndVipTimestamp() { return this.endVipTimestamp; }

    public int getBonusPoints() { return this.bonusPoints; }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;

        flush();
    }

    public int getAchievementPoints() {
        return this.achievementPoints;
    }

    public int getXpPoints() { return this.xpPoints; }

    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public void setXpPoints(int xpPoints) { this.xpPoints = xpPoints;}

    public String getMotto() {
        return this.motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;

        flush();
    }

    public String getFigure() {
        return this.figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;

        flush();
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;

        flush();
    }

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;

        flush();
    }

    public int getVipPoints() {
        return this.vipPoints;
    }

    public void setVipPoints(int vipPoints) {
        this.vipPoints = vipPoints;
    }

    public int getLastVisit() {
        return this.lastVisit;
    }

    public void setLastVisit(long time) {
        this.lastVisit = (int) time;
    }

    public String getRegDate() {
        return this.regDate;
    }

    public boolean isVip() {
        return this.vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;

        flush();
    }

    public boolean getAsk() {
        return this.ask;
    }

    public void setAsk(boolean ask) {
        this.ask = ask;
    }

    public int getRegTimestamp() {
        return regTimestamp;
    }

    public void setRegTimestamp(int regTimestamp) {
        this.regTimestamp = regTimestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFavouriteGroup() {
        return favouriteGroup;
    }

    public void setFavouriteGroup(int favouriteGroup) {
        this.favouriteGroup = favouriteGroup;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getActivityPoints() {
        return activityPoints;
    }

    public void setActivityPoints(int activityPoints) {
        this.activityPoints = activityPoints;
    }

    public String getTemporaryFigure() {
        return temporaryFigure;
    }

    public void setTemporaryFigure(String temporaryFigure) {
        this.temporaryFigure = temporaryFigure;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public int getTimeMuted() {
        return this.timeMuted;
    }

    public void setTimeMuted(int Time) {
        this.timeMuted = Time;

        flush();
    }

    public boolean getChangingName() {
        return this.changingName;
    }

    public void setChangingName(boolean changingName) {
        this.changingName = changingName;

        flush();
    }

    public boolean getFlaggingUser() {
        return this.flaggingUser;
    }

    public void setFlaggingUser(boolean flaggingUser) {
        this.flaggingUser = flaggingUser;

        flush();
    }

    public String getBanner() { return this.banner;}

    public void setBanner(String banner) {
        this.banner = banner;

        flush();
    }

    @Override
    public int getKisses() { return this.kisses; }

    @Override
    public void setKisses(int amount) { this.kisses = amount; flush();}

    @Override
    public void increaseKisses(int amount) { this.kisses += amount;}

    @Override
    public void incrementKisses() { this.kisses++;}

    @Override
    public void decreaseKisses(int amount) { this.kisses -= amount; flush(); }

    public String getNameColour() {
        return this.nameColour;
    }

    @Override
    public void setNameColour(String nameColour) {
        this.nameColour = nameColour;

        flush();
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.flush();
    }

    public boolean isEmojiEnabled() {
        return emojiEnabled;
    }

    public void setEmojiEnabled(boolean emojiEnabled) {
        this.emojiEnabled = emojiEnabled;
    }

    public int getSeasonalPoints() {
        return seasonalPoints;
    }

    public void setSeasonalPoints(int seasonalPoints) {
        this.seasonalPoints = seasonalPoints;

        flush();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void flush() {
        if (getPlayer() != null) {
            getPlayer().flush();
        }
    }

    @Override
    public PlayerData clone() {
        PlayerData data;
        try {
            data = (PlayerData) super.clone();
        } catch (CloneNotSupportedException e) {
            data = new PlayerData(this.id, this.username, this.motto, this.figure, this.gender, this.email, this.rank, this.credits, this.vipPoints, this.activityPoints, this.seasonalPoints, this.regDate, this.lastVisit, this.vip, this.achievementPoints, this.xpPoints, this.regTimestamp, this.favouriteGroup, this.ipAddress, this.questId, this.timeMuted, this.nameColour, this.tag, this.emojiEnabled, this.gamesWin, this.bonusPoints, this.endVipTimestamp, this.snowXp, this.kisses, this.banner, this.player);
        }
        return data;
    }

    public void loadPlayerBanners() {
        try {
            if (this.banners.size() > 0)
                this.banners.clear();

            this.banners = PlayerDao.getPlayerBanners(this.id);
        } catch (Exception e) {
            log.error("Error while reloading players banner permissions", e);
            return;
        }

        log.info("Loaded " + this.banners.size() + " player banners");
    }

    public Map<String, PlayerBanner> getPlayerBanner() {
        return this.banners;
    }

    public int getSnowXp() {
        return snowXp;
    }

    public void increaseSnowXp(int snowXp) {
        this.snowXp += snowXp;
    }

    public int getLevelSnow() {
        if (this.getSnowXp() >= 2000)
            return 10;
        if (this.getSnowXp() >= 1600)
            return 9;
        if (this.getSnowXp() >= 1300)
            return 8;
        if (this.getSnowXp() >= 1000)
            return 7;
        if (this.getSnowXp() >= 750)
            return 6;
        if (this.getSnowXp() >= 500)
            return 5;
        if (this.getSnowXp() >= 300)
            return 4;
        if (this.getSnowXp() >= 150)
            return 3;
        if (this.getSnowXp() >= 50)
            return 2;
        return 1;
    }

    /*public String getMachineID() {
        return this.machineID;
    }

    public void setMachineId(String machineID) {
        this.machineID = machineID;
    }*/
    
    public Session getWebsocketSession() { return websocket_session; }

    public void setWebsocketSession(Session session) { this.websocket_session = session; }

}