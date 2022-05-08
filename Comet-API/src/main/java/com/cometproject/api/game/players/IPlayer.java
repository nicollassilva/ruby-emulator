package com.cometproject.api.game.players;

import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.players.data.IPlayerData;
import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.api.game.players.data.IPlayerSettings;
import com.cometproject.api.game.players.data.IPlayerStatistics;
import com.cometproject.api.game.players.data.components.*;
import com.cometproject.api.game.rooms.entities.PlayerRoomEntity;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.api.networking.sessions.ISession;

import java.util.List;
import java.util.Set;

public interface IPlayer {
    String INFINITE_BALANCE = "999999999";

    void dispose();

    void sendBalance();

    void setOnHorseJumpingSequence(boolean onSequence);

    boolean onHorseJumpingSequence();

    void isFurnitureEditing(boolean isFurnitureEditing);

    void isFurniturePickup(boolean isFurniturePickup);

    boolean getIsFurnitureEditing();

    boolean getIsFurniturePickup();

    IMessageComposer composeCreditBalance();

    IMessageComposer composeCurrenciesBalance();

    void managePeriodicAchievements();

    void loadRoom(int id, String password);

    IPlayerOfferPurchase getOfferPurchase(int offerId);

    void addOfferPurchase(IPlayerOfferPurchase offer);

    void poof();

    void ignorePlayer(int playerId);

    void unignorePlayer(int playerId);

    boolean ignores(int playerId);

    void resetInteractionHandlers();

    List<Integer> getRooms();

    void setInIceSkate(boolean inIceSkate);

    boolean onTheIceSkate();

    void setInRollerSkate(boolean inRollerSkate);

    boolean onTheRollerSkate();

    List<Integer> getRoomsWithRights();

    void setRooms(List<Integer> rooms);

    void setSession(ISession client);

    PlayerRoomEntity getEntity();

    ISession getSession();

    IPlayerData getData();

    IPlayerSettings getSettings();

    IPlayerStatistics getStats();

    PlayerPermissions getPermissions();

    PlayerAchievements getAchievements();

    PlayerMessenger getMessenger();

    PlayerInventory getInventory();

    SubsComponent getSubscription();

    PlayerRelationships getRelationships();

    PlayerBots getBots();

    PlayerPets getPets();

    PlayerQuests getQuests();

    int getId();

    void sendNotif(String title, String message);

    void sendMotd(String message);

    boolean isTeleporting();

    long getTeleportId();

    void setTeleportId(long teleportId);

    long getRoomLastMessageTime();

    void setRoomLastMessageTime(long roomLastMessageTime);

    double getRoomFloodTime();

    void setRoomFloodTime(double roomFloodTime);

    int getRoomFloodFlag();

    void setRoomFloodFlag(int roomFloodFlag);

    String getLastMessage();

    void setLastMessage(String lastMessage);

    Set<Integer> getGroups();

    int getNotifCooldown();

    void setNotifCooldown(int notifCooldown);

    int getLastRoomId();

    void setLastRoomId(int lastRoomId);

    int getLastGift();

    void setLastGift(int lastGift);

    int getLastSpin();

    void setLastSpin(int lastSpin);

    int getLastCommandRoleplay();

    void setLastCommandRoleplay(int lastCommandRoleplay);

    int getLastPurchase();

    void setLastPurchase(int lastPurchase);

    long getMessengerLastMessageTime();

    void setMessengerLastMessageTime(long messengerLastMessageTime);

    double getMessengerFloodTime();

    void setMessengerFloodTime(double messengerFloodTime);

    int getMessengerFloodFlag();

    void setMessengerFloodFlag(int messengerFloodFlag);

    boolean isDeletingGroup();

    void setDeletingGroup(boolean isDeletingGroup);

    long getDeletingGroupAttempt();

    void setDeletingGroupAttempt(long deletingGroupAttempt);

    void bypassRoomAuth(boolean bypassRoomAuth);

    boolean isBypassingRoomAuth();

    int getLastFigureUpdate();

    void setLastFigureUpdate(int lastFigureUpdate);

    long getLastReward();

    void setLastReward(long lastReward);

    long getLastDiamondReward();

    void setLastDiamondReward(long lastDiamondReward);

    Set<ICatalogItem> getRecentPurchases();

    int getLastRoomCreated();

    void setLastRoomCreated(int lastRoomCreated);

    boolean getLogsClientStaff();

    void setLogsClientStaff(boolean logsClientStaff);

    void flush();
}
