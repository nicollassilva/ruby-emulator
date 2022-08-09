package com.cometproject.api.config;

import com.cometproject.api.game.rooms.filter.FilterMode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;


public class CometSettings {
    public static int minimumBotMessagesDelay = 5;
    public static int minimumBotNameLength = 30;

    public static int dailyRespects = 3;
    public static int dailyScratchs = 3;

    // Achievements items stack limit
    public static int maxBanzaiTilesInStack = 10;
    public static int maxFootballGatesInStack = 3;
    public static int maxHorseJumpInStack = 3;
    public static int maxAcademyItemsInStack = 3;
    public static int maxFreezeTilesInStack = 3;

    public static int vipRank = 2;
    public static int rankCanSeeVipContent = 13;

    public static boolean talentTrackEnabled = false;
    public static boolean useNewKissSystem = true;

    public static boolean userDisconnectNotification = false;

    public static int seasonalNumber = 103;

    public static int embassadorRank = 12;

    public static boolean motdEnabled = false;
    public static String motdMessage = "";
    public static String hotelName = "";
    public static String hotelUrl = "";
    public static String aboutImg = "";

    public static boolean catalogPageIdEnabled = false;

    public static boolean onlineRewardEnabled = false;
    public static int onlineRewardCredits = 0;
    public static int onlineRewardDuckets = 0;

    public static int cameraCoinsPricing = 0;
    public static int cameraDucketsPricing = 0;

    public static int onlineRewardDiamondsInterval = 45;
    public static int onlineRewardDiamonds = 0;
    public static int onlineRewardInterval = 15;
    public static Set<DayOfWeek> onlineRewardDoubleDays = Sets.newHashSet();

    public static int groupCost = 0;

    public static boolean aboutShowPlayersOnline = true;
    public static boolean aboutShowUptime = true;
    public static boolean aboutShowRoomsActive = true;

    public static int floorEditorMaxX = 0;
    public static int floorEditorMaxY = 0;
    public static int floorEditorMaxTotal = 0;

    public static int roomMaxPlayers = 150;
    public static boolean roomEncryptPasswords = false;
    public static int roomPasswordEncryptionRounds = 10;
    public static boolean roomCanPlaceItemOnEntity = false;
    public static int roomMaxBots = 15;
    public static int roomMaxPets = 15;
    public static int roomIdleMinutes = 20;

    public static FilterMode wordFilterMode = FilterMode.DEFAULT;

    public static boolean useDatabaseIp = false;
    public static boolean saveLogins = false;

    public static boolean playerInfiniteBalance = false;
    public static int playerGiftCooldown = 30;
    public static int playerPurchaseCooldown = 2;

    public static final Map<String, String> strictFilterCharacters = Maps.newHashMap();

    /**
     * Cooldowns player
     */
    public static int PLAYER_GIFT_COOLDOWN = 30;
    public static boolean PLAYER_FIGURE_VALIDATION_ALLOW = false;
    public static boolean PLAYER_FIGURE_VALIDATION_ALLOW_V2 = true;
    public static int playerChangeFigureCooldown = 5;

    public static int messengerMaxFriends = 1100;
    public static boolean messengerLogMessages = true;

    /**
     * Config catalog
     */
    public static boolean CATALOG_ASYNC_PURCHASE_ALLOW = false;

    /**
     * Camera config
     */
    public static int cameraPhotoItemId = 4518;
    public static int cameraPhotoItemIdXXL = 50001;
    public static String cameraPhotoUrl = "http://localhost:8080/camera/photo/%photoId%";
    public static String cameraUploadUrl = "http://localhost:8080/camera/upload/%photoId%";
    public static String thumbnailUploadUrl = "http://localhost:8080/camera/upload/%photoId%";

    /**
     * Config Emoji
     */
    public static String emojiImagePath = "http://localhost:8080/assets/img/emoji/";

    public static int roomWiredRewardMinimumRank = 7;
    public static boolean storageItemQueueEnabled = false;

    public static int maxConnectionsPerIpAddress = 2;

    public static boolean playerRightsItemPlacement = true;

    public static boolean groupChatEnabled = false;
    public static boolean logCatalogPurchases = false;

    /**
     * Hall of fame config
     */
    public static boolean hallOfFameEnabled = false;
    public static String hallOfFameCurrency = "seasonal_points";
    public static int hallOfFameRefreshMinutes = 5;
    public static String hallOfFameTextsKey = "";

    public static boolean gameCenterSnowwarEnabled = true;

    /**
     * Bonusbag config
     */
    public static boolean bonusBagEnabled = false;
    public static String bonusRewardName = "throne";
    public static int bonusHours = 100;
    public static int bonusRewardItemId = 80192;

    public static boolean maxConnectionsBlockSuspicious = true;
    public static int currentEventRoom = 0;
    public static int cantityUsers = 100;
    public static int roomsForUsers = 100;
    public static String rewardConcurrentUsers = "GBREWARD";

    /**
     * Config monsterplant reward
     */
    public static int monsterSeedId = 0;

    /**
     * Config wireds
     */
    public static boolean WIRED_WORKING_ACTIVE = true;


    /**
     * Config crypto
     */
    public static boolean cryptoEnabled = false;
    public static String crypto_d = "59ae13e243392e89ded305764bdd9e92e4eafa67bb6dac7e1415e8c645b0950bccd26246fd0d4af37145af5fa026c0ec3a94853013eaae5ff1888360f4f9449ee023762ec195dff3f30ca0b08b8c947e3859877b5d7dced5c8715c58b53740b84e11fbc71349a27c31745fcefeeea57cff291099205e230e0c7c27e8e1c0512b";
    public static String crypto_n = "86851dd364d5c5cece3c883171cc6ddc5760779b992482bd1e20dd296888df91b33b936a7b93f06d29e8870f703a216257dec7c81de0058fea4cc5116f75e6efc4e9113513e45357dc3fd43d4efab5963ef178b78bd61e81a14c603b24c8bcce0a12230b320045498edc29282ff0603bc7b7dae8fc1b05b52b2f301a9dc783b7";
    public static String crypto_e = "3";

    /**
     * Config nitro
     */
    public static boolean websocketsEnabled = false;
    public static String[] websocketOriginWhitelist = new String[]{"localhost"};
    public static String nitroWsHeader = "X-Forwarded-For";

    /**
     * Config storage
     */
    public static boolean ADAPTIVE_ENTITY_PROCESS_DELAY = false;

    /**
     * Recycler Config
     */
    public static int ECOTRON_RARITY_CHANCE_5 = 1;
    public static int ECOTRON_RARITY_CHANCE_4 = 1;
    public static int ECOTRON_RARITY_CHANCE_3 = 1;
    public static int ECOTRON_RARITY_CHANCE_2 = 1;
    public static int ITEM_ID_ECOTRON_BOX = 2222;
    public static int RECYCLER_VALUE = 8;

    private static final Logger log = LogManager.getLogger(CometSettings.class.getName());

    /**
     * Enable & set the Message Of The Day text
     *
     * @param motd The message to display to the user on-login
     */
    public static void setMotd(String motd) {
        motdEnabled = true;
        motdMessage = motd;
    }

    public static boolean PLACE_ITEMS_ASYNC = false;

    public static void setCurrentEventRoom(int r) {
        currentEventRoom = r;
    }
    public int getEventID() { return currentEventRoom; }
    public static boolean FIGURE_VALIDATION = false;
    public static int CATALOG_SOLD_OUT_LTD_PAGE_ID = 0;
}
