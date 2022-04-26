package com.cometproject.api.game.players.data;

import java.util.HashMap;

public interface IPlayerData extends PlayerAvatar {

    void save();

    void decreaseCredits(int amount);

    void increaseCredits(int amount);

    void decreaseVipPoints(int points);

    void increaseVipPoints(int points);

    void increaseActivityPoints(int points);

    void decreaseActivityPoints(int points);

    void increaseSeasonalPoints(int points);

    void decreaseSeasonalPoints(int points);

    int getId();

    int getRank();

    String getUsername();

    void setUsername(String username);

    int getAchievementPoints();

    void increaseAchievementPoints(int amount);

    int getXpPoints();

    void increaseXpPoints(int amount);

    String getMotto();

    void setMotto(String motto);

    String getFigure();

    String getGender();

    int getCredits();

    void setCredits(int credits);

    int getVipPoints();

    int getSeasonalPoints();

    int getBonusPoints();

    void setSeasonalPoints(int points);

    int getLastVisit();

    String getRegDate();

    boolean isVip();

    void setVip(boolean vip);

    void setLastVisit(long time);

    void setFigure(String figure);

    void setGender(String gender);

    int getRegTimestamp();

    void setRegTimestamp(int regTimestamp);

    String getEmail();

    void setEmail(String email);

    int getFavouriteGroup();

    void setFavouriteGroup(int favouriteGroup);

    String getIpAddress();

    void setIpAddress(String ipAddress);

    int getActivityPoints();

    void setActivityPoints(int activityPoints);

    void setVipPoints(int vipPoints);

    void setRank(int rank);

    String getTemporaryFigure();

    void setTemporaryFigure(String temporaryFigure);

    int getQuestId();

    void setQuestId(int questId);

    String getNameColour();

    void setNameColour(String nameColour);

    void setTag(String var1);

    int getKisses();

    void setKisses(int amount);

    void increaseKisses(int amount);

    void incrementKisses();

    void decreaseKisses(int amount);

    boolean battlePassGiftUnlocked(int level, boolean vip);

    HashMap<String, String> battlePassType(int level, boolean vip);

    void increaseLevel(int level);

    void decreaseLevel(int level);

    void setLevel(int level);

    int getLevel();

    int getXp();

    void increaseXp(int xp);

    void decreaseXp(int xp);

    void setXp(int xp);

    /*void setPass(boolean pass);

    boolean havePass();*/

}