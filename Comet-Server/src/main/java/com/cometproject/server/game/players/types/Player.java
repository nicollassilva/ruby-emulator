package com.cometproject.server.game.players.types;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.bots.IBotData;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.pets.IPetData;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.api.game.players.data.PlayerAvatar;
import com.cometproject.api.game.players.data.components.PlayerInventory;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.players.data.components.messenger.IMessengerFriend;
import com.cometproject.api.game.players.data.components.messenger.RelationshipLevel;
import com.cometproject.api.game.quests.IQuest;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.guides.types.HelpRequest;
import com.cometproject.server.game.guides.types.HelperSession;
import com.cometproject.server.game.items.crafting.CraftingMachine;
import com.cometproject.server.game.landing.LandingManager;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.components.*;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.game.quests.QuestManager;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.types.ChatMessageColour;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.quests.QuestStartedMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.purse.CurrenciesMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.purse.SendCreditsMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.storage.cache.objects.items.PlayerItemDataObject;
import com.cometproject.server.storage.queries.catalog.CatalogDao;
import com.cometproject.server.storage.queries.landing.LandingDao;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;
import com.cometproject.storage.api.StorageContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Player extends Observable implements IPlayer {

    private final PermissionComponent permissions;
    private final InventoryComponent inventory;
    private final SubscriptionComponent subscription;
    private final MessengerComponent messenger;
    private final RelationshipComponent relationships;
    private final InventoryBotComponent bots;
    private final PetComponent pets;
    private final QuestComponent quests;
    private final AchievementComponent achievements;
    private final NavigatorComponent navigator;
    private final WardrobeComponent wardrobe;
    private CraftingMachine lastCraftingMachine;

    private final TIntObjectMap<IPlayerOfferPurchase> offerCache = new TIntObjectHashMap<>();
    private boolean online;

    public boolean cancelPageOpen = false;
    public boolean isDisposed = false;
    public int lastBannedListRequest = 0;
    private final int id;
    private PlayerSettings settings;
    private PlayerData data;
    private final PlayerStatistics stats;
    private PlayerEntity entity;
    private Session session;
    private HelperSession helperSession;
    private List<Integer> rooms = new ArrayList<>();
    private List<Integer> roomsWithRights = new ArrayList<>();
    private List<Integer> enteredRooms = new ArrayList<>();
    private Set<Integer> groups = Sets.newConcurrentHashSet();
    private List<Integer> ignoredPlayers = new ArrayList<>();
    private List<String> groupWhispers = new ArrayList<>();
    private long roomLastMessageTime = 0;
    public ArrayList<String> whisperGroupUser;
    private double roomFloodTime = 0;
    private int lastForumPost = 0;
    private long lastRoomRequest = 0;
    private long lastBadgeUpdate = 0;
    private int lastFigureUpdate = 0;
    private int roomFloodFlag = 0;
    private long messengerLastMessageTime = 0;
    private double messengerFloodTime = 0;
    private int messengerFloodFlag = 0;
    private boolean usernameConfirmed = false;
    private long teleportId = 0;
    private int teleportRoomId = 0;
    private String lastMessage = "";
    private int lastVoucherRedeemAttempt = 0;
    private int voucherRedeemAttempts = 0;
    private int notifCooldown = 0;
    private int lastRoomId;
    private int lastGift = 0;
    private int lastPurchase = 0;
    private int lastSpin = 0;
    private int lastCommandRoleplay = 0;
    private int lastSlotMachineAction = 0;
    private int lastRoomCreated = 0;
    private boolean isDeletingGroup = false;
    private long deletingGroupAttempt = 0;
    private boolean bypassRoomAuth;
    private long lastDiamondReward;
    private long lastReward;
    private boolean invisible = false;
    private long lastTradeTime = 0;
    private int lastTradeFlag = 0;
    private long lastTradeFlood = 0;
    private long lastPhotoTaken = 0;
    private boolean isSearchFurni = false;
    private boolean logsClientStaff = false;
    private String ssoTicket;
    private Set<ICatalogItem> recentPurchases;
    private boolean[] calendarGifts;

    private boolean inIceSkate = false;
    private boolean inRollerSkate = false;

    private boolean onHorseJumpingSequence = false;

    private final Set<Integer> listeningPlayers = Sets.newConcurrentHashSet();

    private Set<String> eventLogCategories = Sets.newConcurrentHashSet();

    private ChatMessageColour chatMessageColour = null;

    private HelpRequest helpRequest = null;

    private boolean petsMuted;
    private boolean botsMuted;

    private boolean isFurnitureEditing = false;
    private boolean isFurniturePickup = false;
    private boolean isViewingHeight = false;

    private String lastPhoto = null;
    private String lastPurchasedPhoto = null;
    private int roomQueueId = 0;
    private int spectatorRoomId = 0;

    private int bubbleId = 0;

    private final List<PlayerMention> mentions = new ArrayList<>();
    private Map<String, Long> antiSpam = new ConcurrentHashMap<>();

    private long lastForwardRoomRequest = System.currentTimeMillis();
    private final List<Integer> lastRoomsIds = Lists.newArrayList();
    private boolean nitroEnabled = false;

    public Player(ResultSet data, boolean isFallback) throws SQLException {
        this.id = data.getInt("playerId");

        this.data = new PlayerData(data, this);

        if (isFallback) {
            this.settings = PlayerDao.getSettingsById(this.id);
            this.stats = PlayerDao.getStatisticsById(this.id);
        } else {
            this.settings = new PlayerSettings(data, true, this);
            this.stats = new PlayerStatistics(data, true, this);
        }

        this.permissions = new PermissionComponent(this);
        this.inventory = new InventoryComponent(this);
        this.messenger = new MessengerComponent(this);
        this.subscription = new SubscriptionComponent(this);
        this.relationships = new RelationshipComponent(this);
        this.bots = new InventoryBotComponent(this);
        this.pets = new PetComponent(this);
        this.quests = new QuestComponent(this);
        this.achievements = new AchievementComponent(this);
        this.navigator = new NavigatorComponent(this);
        this.wardrobe = new WardrobeComponent(this);
        this.whisperGroupUser = new ArrayList<>();

        StorageContext.getCurrentContext().getGroupRepository().getGroupIdsByPlayerId(this.id,
                groups -> this.groups.addAll(groups));

        this.entity = null;
        this.lastReward = Comet.getTime();
        this.lastDiamondReward = Comet.getTime();

        this.getCalendarGifts();
        this.loadOfferPurchases();
    }

    public void loadOfferPurchases() {
        PlayerDao.getOffersPurchaseById(this.offerCache, this.id);
    }

    @Override
    public void dispose() {
        this.resetInteractionHandlers();
        flush();

        if (this.getEntity() != null) {
            try {
                this.getEntity().leaveRoom(true, false, false);
            } catch (Exception e) {
                // Player failed to leave room
                this.getSession().getLogger().error("Error while disposing entity when player disconnects", e);
            }
        }

        if (this.getSettings() != null) {
            PlayerDao.saveBubbleId(this.getSettings().getBubbleId(), this.getId());
        }

        if (this.helperSession != null) {
            GuideManager.getInstance().finishPlayerDuty(this.helperSession);
            this.helperSession = null;
        }

        if (this.data != null){
            this.getData().save();
        }

        this.getPets().dispose();
        this.getBots().dispose();
        this.getInventory().dispose();
        this.getMessenger().dispose();
        this.getRelationships().dispose();
        this.getQuests().dispose();
        this.getNavigator().dispose();
        this.getWardrobe().dispose();

        try {
            PlayerManager.getInstance().getSsoTicketToPlayerId().remove(this.ssoTicket);
        } catch (Exception ignored) {

        }

        if(Comet.isDebugging) {
            this.session.getLogger().debug(this.getData().getUsername() + " logged out");
        }

        PlayerDao.updatePlayerStatus(this, false, false);

        this.rooms.clear();
        this.rooms = null;

        this.roomsWithRights.clear();
        this.roomsWithRights = null;

        this.groups.clear();
        this.groups = null;

        this.ignoredPlayers.clear();
        this.ignoredPlayers = null;
        this.groupWhispers.clear();
        this.groupWhispers = null;

        this.enteredRooms.clear();
        this.enteredRooms = null;

        this.antiSpam.clear();
        this.antiSpam = null;

        this.eventLogCategories.clear();
        this.eventLogCategories = null;

        if (this.recentPurchases != null) {
            this.recentPurchases.clear();
            this.recentPurchases = null;
        }

        this.listeningPlayers.clear();

        this.settings = null;
        this.data = null;

        this.isDisposed = true;
    }

    public void isFurnitureEditing(boolean isFurnitureEditing) {
        this.isFurnitureEditing = isFurnitureEditing;
    }

    public boolean getIsFurnitureEditing() {
        return isFurnitureEditing;
    }

    @Override
    public void sendBalance() {
        session.send(composeCurrenciesBalance());
        session.send(composeCreditBalance());
    }

    @Override
    public void sendNotif(String title, String message) {
        session.send(new AdvancedAlertMessageComposer(title, message));
    }

    public void sendBubble(String image, String message) {
        this.session.send(new NotificationMessageComposer(image, message));
    }

    public void setOnHorseJumpingSequence(boolean onSequence) {
        this.onHorseJumpingSequence = onSequence;
    }

    public boolean onHorseJumpingSequence() {
        return this.onHorseJumpingSequence;
    }

    public void setInIceSkate(boolean inIceSkate) {
        this.inIceSkate = inIceSkate;
    }

    public boolean onTheIceSkate() {
        return this.inIceSkate && !this.inRollerSkate;
    }

    public void setInRollerSkate(boolean inRollerSkate) {
        this.inRollerSkate = inRollerSkate;
    }

    public boolean onTheRollerSkate() {
        return this.inRollerSkate && !this.inIceSkate;
    }

    public void managePeriodicAchievements() {
        // IceSkate Achievement
        if(this.onTheIceSkate() && this.getEntity().isWalking()) {
            this.getAchievements().progressAchievement(AchievementType.ICE_SKATES, 1);
        }

        // RollerSkate Achievement
        if(this.onTheRollerSkate() && this.getEntity().isWalking()) {
            this.getAchievements().progressAchievement(AchievementType.ROLLER_SKATES, 1);
        }
    }

    @Override
    public void sendMotd(String message) {
        session.send(new MotdNotificationMessageComposer(message));
    }

    @Override
    public MessageComposer composeCreditBalance() {
        return new SendCreditsMessageComposer(CometSettings.playerInfiniteBalance ? INFINITE_BALANCE : Integer.toString(session.getPlayer().getData().getCredits()));
    }

    @Override
    public MessageComposer composeCurrenciesBalance() {
        Map<Integer, Integer> currencies = new HashMap<>();

        currencies.put(0, getData().getActivityPoints());
        currencies.put(105, getData().getVipPoints());
        currencies.put(5, getData().getVipPoints());
        currencies.put(CometSettings.seasonalNumber, getData().getSeasonalPoints());

        return new CurrenciesMessageComposer(currencies);
    }

    @Override
    public void loadRoom(int id, String password) {
        if (entity != null && entity.getRoom() != null) {
            entity.leaveRoom(true, false, false);
            setEntity(null);
        }

        final Room room = RoomManager.getInstance().get(id);

        if (room == null) {
            session.send(new HotelViewMessageComposer());
            return;
        }

        if (room.getEntities() == null) {
            return;
        }

        if (room.getEntities().getEntityByPlayerId(this.id) != null) {
            room.getEntities().getEntityByPlayerId(this.id).leaveRoom(true, false, false);
        }

        final PlayerEntity playerEntity = room.getEntities().createEntity(this);
        setEntity(playerEntity);

        if (!playerEntity.joinRoom(room, password)) {
            setEntity(null);
        }

        if (this.getData().getQuestId() != 0) {
            final IQuest quest = QuestManager.getInstance().getById(this.getData().getQuestId());

            if (quest != null && this.getQuests().hasStartedQuest(quest.getId()) && !this.getQuests().hasCompletedQuest(quest.getId())) {
                this.getSession().send(new QuestStartedMessageComposer(quest, this));

                if (quest.getType() == QuestType.SOCIAL_VISIT) {
                    this.getQuests().progressQuest(QuestType.SOCIAL_VISIT);
                }
            }
        }

        /* Nux Status
        if(this.getSettings().getNuxStatus() == 0){
            this.getSession().send(new NuxAlertComposer(2));
            this.getSession().send(new OpenLinkMessageComposer("helpBubble/add/CHAT_INPUT/" + Locale.getOrDefault("CHAT_INPUT", "¡Haz click aquí para escribir!")));
        }*/

        if (!this.enteredRooms.contains(id) && !this.rooms.contains(id)) {
            this.enteredRooms.add(id);
        }
    }

    @Override
    public void poof() {
        this.getSession().send(new UpdateInfoMessageComposer(-1, this.getData().getFigure(), this.getData().getGender(), this.getData().getMotto(), this.getData().getAchievementPoints(), this.getData().getBanner()));

        if (this.getEntity() != null && this.getEntity().getRoom() != null && this.getEntity().getRoom().getEntities() != null) {
            this.getEntity().unIdle();
            this.getEntity().getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(this.getEntity()));
        }
    }

    @Override
    public void ignorePlayer(int playerId) {
        if (this.ignoredPlayers == null) {
            this.ignoredPlayers = new ArrayList<>();
        }

        this.ignoredPlayers.add(playerId);
    }

    @Override
    public void unignorePlayer(int playerId) {
        this.ignoredPlayers.remove((Integer) playerId);
    }

    @Override
    public boolean ignores(int playerId) {
        return this.ignoredPlayers != null && this.ignoredPlayers.contains(playerId);
    }

    public List<String> getGroupWhispers() {
        return this.groupWhispers;
    }

    public boolean handleGroupWhisper(String username) {
        if (this.groupWhispers == null) {
            this.groupWhispers = new ArrayList<String>();
        }
        if (!this.isGroupWhisperValid(username)) {
            this.groupWhispers.add(username);
            return true;
        }
        this.groupWhispers.remove(username);
        return false;
    }

    public Player getPlayerByGroupChat(String username) {
        if (this.groupWhispers.contains(username)) {
            final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

            if (session == null) {
                this.handleGroupWhisper(username);
                return null;
            }

            return session.getPlayer();
        }

        return null;
    }

    public boolean isGroupWhisperValid(String username) {
        return this.groupWhispers != null && this.groupWhispers.contains(username);
    }

    public void sendPopup(String title, String message) {
        session.send(new AdvancedAlertMessageComposer(title, message, "Ok", "event:", "admin"));
    }

    @Override
    public List<Integer> getRooms() {
        return rooms;
    }

    @Override
    public void setRooms(List<Integer> rooms) {
        this.rooms = rooms;

        flush();
    }

    public boolean antiSpam(String name, double expire) {
        if (this.antiSpam.containsKey(name)) {
            if ((double)(System.currentTimeMillis() - this.antiSpam.get(name)) < expire * 1000.0) {
                return true;
            }
            this.antiSpam.replace(name, System.currentTimeMillis());
        } else {
            this.antiSpam.put(name, System.currentTimeMillis());
        }
        return false;
    }

    @Override
    public List<Integer> getRoomsWithRights() {
        return roomsWithRights;
    }

    public PlayerEntity getEntity() {
        return this.entity;
    }

    public void setEntity(PlayerEntity avatar) {
        this.entity = avatar;

        flush();
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public void setSession(ISession client) {
        this.session = ((Session) client);
    }

    @Override
    public PlayerData getData() {
        return this.data;
    }

    public void setData(PlayerData playerData) {
        this.data = playerData;
    }

    @Override
    public PlayerStatistics getStats() {
        return this.stats;
    }

    @Override
    public PermissionComponent getPermissions() {
        return this.permissions;
    }

    public MessengerComponent getMessenger() {
        return this.messenger;
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    @Override
    public SubscriptionComponent getSubscription() {
        return this.subscription;
    }

    @Override
    public RelationshipComponent getRelationships() {
        return this.relationships;
    }

    public InventoryBotComponent getBots() {
        return this.bots;
    }

    public PetComponent getPets() {
        return this.pets;
    }

    public QuestComponent getQuests() {
        return quests;
    }

    public AchievementComponent getAchievements() {
        return achievements;
    }

    @Override
    public PlayerSettings getSettings() {
        return this.settings;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isTeleporting() {
        return this.teleportId != 0;
    }

    @Override
    public long getTeleportId() {
        return this.teleportId;
    }

    @Override
    public void setTeleportId(long teleportId) {
        this.teleportId = teleportId;
    }

    @Override
    public long getRoomLastMessageTime() {
        return roomLastMessageTime;
    }

    @Override
    public void setRoomLastMessageTime(long roomLastMessageTime) {
        this.roomLastMessageTime = roomLastMessageTime;
    }

    @Override
    public double getRoomFloodTime() {
        return roomFloodTime;
    }

    @Override
    public void setRoomFloodTime(double roomFloodTime) {
        this.roomFloodTime = roomFloodTime;
    }

    @Override
    public int getRoomFloodFlag() {
        return roomFloodFlag;
    }

    @Override
    public void setRoomFloodFlag(int roomFloodFlag) {
        this.roomFloodFlag = roomFloodFlag;
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public Set<Integer> getGroups() {
        return groups == null ? Sets.newHashSet() : groups;
    }

    @Override
    public int getNotifCooldown() {
        return this.notifCooldown;
    }

    @Override
    public void setNotifCooldown(int notifCooldown) {
        this.notifCooldown = notifCooldown;
    }

    @Override
    public int getLastRoomId() {
        return lastRoomId;
    }

    @Override
    public void setLastRoomId(int lastRoomId) {
        this.lastRoomId = lastRoomId;

        flush();
    }

    @Override
    public int getLastGift() {
        return lastGift;
    }

    @Override
    public void setLastGift(int lastGift) {
        this.lastGift = lastGift;
    }

    @Override
    public int getLastPurchase() {
        return lastPurchase;
    }

    @Override
    public int getLastSpin() { return lastSpin; }

    @Override
    public void setLastSpin(int lastSpin) { this.lastSpin = lastSpin; }

    @Override
    public int getLastCommandRoleplay() {
        return lastCommandRoleplay;
    }

    public int getLastSlotMachineAction() {
        return lastSlotMachineAction;
    }

    @Override
    public void setLastCommandRoleplay(int lastCommandRoleplay) { this.lastCommandRoleplay = lastCommandRoleplay; }

    public void setLastSlotMachineAction(int lastSlotMachineAction) { this.lastSlotMachineAction = Player.this.lastSlotMachineAction; }

    @Override
    public void setLastPurchase(int lastPurchase) {
        this.lastPurchase = lastPurchase;
    }

    @Override
    public long getMessengerLastMessageTime() {
        return messengerLastMessageTime;
    }

    @Override
    public void setMessengerLastMessageTime(long messengerLastMessageTime) {
        this.messengerLastMessageTime = messengerLastMessageTime;
    }

    @Override
    public double getMessengerFloodTime() {
        return messengerFloodTime;
    }

    @Override
    public void setMessengerFloodTime(double messengerFloodTime) {
        this.messengerFloodTime = messengerFloodTime;
    }

    @Override
    public int getMessengerFloodFlag() {
        return messengerFloodFlag;
    }

    @Override
    public void setMessengerFloodFlag(int messengerFloodFlag) {
        this.messengerFloodFlag = messengerFloodFlag;
    }

    @Override
    public boolean isDeletingGroup() {
        return isDeletingGroup;
    }

    @Override
    public void setDeletingGroup(boolean isDeletingGroup) {
        this.isDeletingGroup = isDeletingGroup;
    }

    @Override
    public long getDeletingGroupAttempt() {
        return deletingGroupAttempt;
    }

    @Override
    public void setDeletingGroupAttempt(long deletingGroupAttempt) {
        this.deletingGroupAttempt = deletingGroupAttempt;
    }

    @Override
    public void bypassRoomAuth(final boolean bypassRoomAuth) {
        this.bypassRoomAuth = bypassRoomAuth;
    }

    @Override
    public boolean isBypassingRoomAuth() {
        return bypassRoomAuth;
    }

    @Override
    public int getLastFigureUpdate() {
        return lastFigureUpdate;
    }

    @Override
    public void setLastFigureUpdate(int lastFigureUpdate) {
        this.lastFigureUpdate = lastFigureUpdate;
    }

    public int getTeleportRoomId() {
        return teleportRoomId;
    }

    public void setTeleportRoomId(int teleportRoomId) {
        this.teleportRoomId = teleportRoomId;
    }

    @Override
    public long getLastReward() {
        return lastReward;
    }

    @Override
    public void setLastReward(long lastReward) {
        this.lastReward = lastReward;
    }

    @Override
    public long getLastDiamondReward() {
        return lastDiamondReward;
    }

    @Override
    public void setLastDiamondReward(long lastDiamondReward) {
        this.lastDiamondReward = lastDiamondReward;
    }

    public int getLastForumPost() {
        return lastForumPost;
    }

    public void setLastForumPost(int lastForumPost) {
        this.lastForumPost = lastForumPost;
    }

    public boolean hasQueued(int id) {
        return roomQueueId == id;

    }

    public void setRoomQueueId(int id) {
        this.roomQueueId = id;
    }

    public boolean isSpectating(int id) {
        return this.spectatorRoomId == id;

    }

    public void setSpectatorRoomId(int id) {
        this.spectatorRoomId = id;
    }

    public int getLastRoomCreated() {
        return lastRoomCreated;
    }

    public void setLastRoomCreated(int lastRoomCreated) {
        this.lastRoomCreated = lastRoomCreated;
    }

    public long getLastRoomRequest() {
        return lastRoomRequest;
    }

    public void setLastRoomRequest(long lastRoomRequest) {
        this.lastRoomRequest = lastRoomRequest;
    }

    public long getLastBadgeUpdate() {
        return lastBadgeUpdate;
    }

    public void setLastBadgeUpdate(long lastBadgeUpdate) {
        this.lastBadgeUpdate = lastBadgeUpdate;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;

        flush();
    }

    public long getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(long lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    public int getLastTradeFlag() {
        return lastTradeFlag;
    }

    public void setLastTradeFlag(int lastTradeFlag) {
        this.lastTradeFlag = lastTradeFlag;
    }

    public long getLastTradeFlood() {
        return lastTradeFlood;
    }

    public void setLastTradeFlood(long lastTradeFlood) {
        this.lastTradeFlood = lastTradeFlood;
    }

    public String getSsoTicket() {
        return this.ssoTicket;
    }

    public void setSsoTicket(final String ssoTicket) {
        this.ssoTicket = ssoTicket;
    }

    public long getLastPhotoTaken() {
        return lastPhotoTaken;
    }

    public void setLastPhotoTaken(long lastPhotoTaken) {
        this.lastPhotoTaken = lastPhotoTaken;
    }

    public int getLastVoucherRedeemAttempt() {
        return lastVoucherRedeemAttempt;
    }

    public void setLastVoucherRedeemAttempt(int lastVoucherRedeem) {
        this.lastVoucherRedeemAttempt = lastVoucherRedeem;
    }

    public int getVoucherRedeemAttempts() {
        return voucherRedeemAttempts;
    }

    public void setVoucherRedeemAttempts(int voucherRedeemAttempts) {
        this.voucherRedeemAttempts = voucherRedeemAttempts;
    }

    public boolean isUsernameConfirmed() {
        return usernameConfirmed;
    }

    public void setUsernameConfirmed(boolean usernameConfirmed) {
        this.usernameConfirmed = usernameConfirmed;
    }

    public Set<String> getEventLogCategories() {
        return eventLogCategories;
    }

    public ChatMessageColour getChatMessageColour() {
        return chatMessageColour;
    }

    public void setChatMessageColour(ChatMessageColour chatMessageColour) {
        this.chatMessageColour = chatMessageColour;
    }

    public HelperSession getHelperSession() {
        return helperSession;
    }

    public void setHelperSession(HelperSession helperSession) {
        this.helperSession = helperSession;
    }

    public HelpRequest getHelpRequest() {
        return helpRequest;
    }

    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest = helpRequest;
    }

    public Set<ICatalogItem> getRecentPurchases() {
        if (this.recentPurchases == null) {
            this.recentPurchases = new ConcurrentHashSet<>();

            this.recentPurchases.addAll(CatalogDao.findRecentPurchases(42, this.id));
        }

        return this.recentPurchases;
    }

    public NavigatorComponent getNavigator() {
        return navigator;
    }

    public boolean petsMuted() {
        return petsMuted;
    }

    public void setPetsMuted(boolean petsMuted) {
        this.petsMuted = petsMuted;
    }

    public boolean botsMuted() {
        return botsMuted;
    }

    public void setBotsMuted(boolean botsMuted) {
        this.botsMuted = botsMuted;
    }

    public WardrobeComponent getWardrobe() {
        return wardrobe;
    }

    public String getLastPhoto() {
        return lastPhoto;
    }

    public void setLastPhoto(String lastPhoto) {
        this.lastPhoto = lastPhoto;
    }

    public void setLogsClientStaff(boolean logsClientStaff) { this.logsClientStaff = logsClientStaff;}

    public boolean getLogsClientStaff(){return logsClientStaff;}

    public void setIsSearchFurni(boolean status) {
        this.isSearchFurni = status;
    }

    public boolean isSearchFurni() {
        return isSearchFurni;
    }

    public CraftingMachine getLastCraftingMachine() {
        return lastCraftingMachine;
    }

    public void setLastCraftingMachine(CraftingMachine machine) {
        this.lastCraftingMachine = machine;
    }

    public Set<Integer> getListeningPlayers() {
        return listeningPlayers;
    }

    public List<PlayerMention> getMentions() { return this.mentions; }

    public void addMention(PlayerMention mention) {
        this.mentions.add(mention);
    }

    public int getBubbleId() {
        return this.bubbleId;
    }

    public void setBubbleId(int bubbleId) {
        this.bubbleId = bubbleId;
    }

    public void flush() {
        setChanged();
        notifyObservers();
    }

    public IPlayerOfferPurchase getOfferPurchase(int offerId) {
        return this.offerCache.get(offerId);
    }

    public void addOfferPurchase(IPlayerOfferPurchase offer) {
        this.offerCache.put(offer.getOfferId(), offer);
    }

    public JsonObject toJson() {
        final JsonObject coreObject = new JsonObject();
        final JsonObject playerDataObject = new JsonObject();
        final JsonObject rankDataObject = new JsonObject();
        final JsonObject inventoryDataObject = new JsonObject();
        final JsonArray itemsDataArray = new JsonArray();
        final JsonArray badgesDataArray = new JsonArray();
        final JsonObject messengerDataObject = new JsonObject();
        final JsonArray messengerFriendsDataArray = new JsonArray();
        final JsonArray messengerRequestsDataArray = new JsonArray();
        final JsonArray relationshipsDataArray = new JsonArray();
        final JsonArray botsDataArray = new JsonArray();
        final JsonArray petsDataArray = new JsonArray();
        final JsonArray roomsArray = new JsonArray();

        coreObject.addProperty("id", id);

        coreObject.addProperty("isOnline", isOnline());

        playerDataObject.addProperty("username", data.getUsername());
        playerDataObject.addProperty("motto", data.getMotto());
        playerDataObject.addProperty("figure", data.getFigure());
        playerDataObject.addProperty("gender", data.getGender());
        playerDataObject.addProperty("email", data.getEmail());
        playerDataObject.addProperty("ip_adress", data.getIpAddress());
        playerDataObject.addProperty("credits", data.getCredits());
        playerDataObject.addProperty("vip_points", data.getVipPoints());
        playerDataObject.addProperty("activity_points", data.getActivityPoints());
        playerDataObject.addProperty("seasonal_points", data.getSeasonalPoints());
        playerDataObject.addProperty("favourite_group", data.getFavouriteGroup());

        coreObject.add("data", playerDataObject);

        rankDataObject.addProperty("id", permissions.getRank().getId());
        rankDataObject.addProperty("name", permissions.getRank().getName());

        coreObject.add("rank", rankDataObject);

        if (inventory.getInventoryItems() != null) {
            for (final PlayerItem playerItem : inventory.getInventoryItems().values()) {
                itemsDataArray.add(new PlayerItemDataObject(playerItem).toJson());
            }
        }

        inventoryDataObject.addProperty("isViewingInventory", inventory.isViewingInventory());

        inventoryDataObject.add("items", itemsDataArray);

        if (inventory.getBadges() != null) {
            for (Map.Entry<String, Integer> badge : inventory.getBadges().entrySet()) {
                final JsonObject badgeDataObject = new JsonObject();

                badgeDataObject.addProperty("code", badge.getKey());
                badgeDataObject.addProperty("slot", badge.getValue());

                badgesDataArray.add(badgeDataObject);
            }
        }

        inventoryDataObject.add("badges", badgesDataArray);

        coreObject.add("inventory", inventoryDataObject);

        for (IMessengerFriend friend : messenger.getFriends().values()) {
            messengerFriendsDataArray.add(friend.toJson());
        }

        messengerDataObject.add("friends", messengerFriendsDataArray);

        for (PlayerAvatar request : messenger.getRequestAvatars()) {
            final JsonObject requestDataObject = new JsonObject();

            requestDataObject.addProperty("username", request.getUsername());
            requestDataObject.addProperty("figure", request.getFigure());
            requestDataObject.addProperty("motto", request.getMotto());
            requestDataObject.addProperty("gender", request.getGender());

            messengerRequestsDataArray.add(requestDataObject);
        }

        messengerDataObject.add("requests", messengerRequestsDataArray);

        coreObject.add("messenger", messengerDataObject);

        for (Map.Entry<Integer, RelationshipLevel> relationshipEntry : relationships.getRelationships().entrySet()) {
            final JsonObject relationshipDataObject = new JsonObject();

            relationshipDataObject.addProperty("userId", relationshipEntry.getKey());
            relationshipDataObject.addProperty("level", relationshipEntry.getValue().getLevelId());

            relationshipsDataArray.add(relationshipDataObject);
        }

        coreObject.add("relationships", relationshipsDataArray);

        if (bots.getBots() != null) {
            for (IBotData botData : bots.getBots().values()) {
                botsDataArray.add(botData.toJsonObject());
            }
        }

        coreObject.add("bots", botsDataArray);

        if (pets.getPets() != null) {
            for (final IPetData petData : pets.getPets().values()) {
                petsDataArray.add(petData.toJsonObject());
            }
        }

        coreObject.add("pets", petsDataArray);

        coreObject.add("achievements", achievements.toJson());

        coreObject.add("settings", settings.toJson());

        coreObject.add("stats", stats.toJson());

        coreObject.add("room", (getEntity() != null && getEntity().getRoom() != null) ? getEntity().getRoom().getCacheObject().toJson() : null);

        for (Integer roomId : rooms)
            roomsArray.add(roomId);

        coreObject.add("rooms", roomsArray);

        return coreObject;
    }

    public String toString() {
        final JsonObject jsonObject = this.toJson();

        if (jsonObject != null) {
            return JsonUtil.getInstance().toJson(jsonObject);
        }

        return JsonUtil.getInstance().toJson(this);
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void getCalendarGifts(){
        final int total = LandingManager.getInstance().getTotalDays();
        this.calendarGifts = new boolean[total];

        Arrays.fill(this.calendarGifts, false);

        this.calendarGifts = LandingDao.calendarDays(this.id,total);
    }

    public boolean[] getGifts(){
        return this.calendarGifts;
    }

    public String getLastPurchasedPhoto() {
        return lastPurchasedPhoto;
    }

    public void setLastPurchasedPhoto(String photo){
        this.lastPurchasedPhoto = photo;
    }

    public void isFurniturePickup(boolean isFurniturePickup) {
        this.isFurniturePickup = isFurniturePickup;
    }

    public boolean getIsFurniturePickup() {
        return isFurniturePickup;
    }

    public void setHeightView(boolean viewHeight) {
        this.isViewingHeight = viewHeight;
    }

    public boolean viewingHeight() {
        return isViewingHeight;
    }

    public void resetInteractionHandlers() {
        this.setInIceSkate(false);
        this.setInRollerSkate(false);
        this.isFurniturePickup(false);
        this.isFurnitureEditing(false);
    }

    @Override
    public long getLastForwardRoomRequest() {
        return lastForwardRoomRequest;
    }

    @Override
    public void setLastForwardRoomRequest(long lastForwardRoomRequest) {
        this.lastForwardRoomRequest = lastForwardRoomRequest;
    }

    @Override
    public List<Integer> getLastRoomsIds() {
        return lastRoomsIds;
    }

    public boolean getNitro() {
        return nitroEnabled;
    }

    public boolean setNitro(boolean nitro) {
        nitroEnabled = nitro;
        return nitroEnabled;
    }
}
