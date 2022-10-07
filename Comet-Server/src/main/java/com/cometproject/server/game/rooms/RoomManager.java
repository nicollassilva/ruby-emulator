package com.cometproject.server.game.rooms;

import com.cometproject.api.config.Configuration;
import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.api.game.rooms.models.CustomFloorMapData;
import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.api.game.rooms.settings.RoomTradeState;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.utilities.Initialisable;
import com.cometproject.server.game.items.music.TraxMachineSong;
import com.cometproject.server.game.navigator.types.search.NavigatorSearchService;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.filter.WordFilter;
import com.cometproject.server.game.rooms.models.types.StaticRoomModel;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomPromotion;
import com.cometproject.server.game.rooms.types.RoomReloadListener;
import com.cometproject.server.game.rooms.types.misc.ChatEmojiManager;
import com.cometproject.server.game.rooms.types.misc.ChatEmotionsManager;
import com.cometproject.server.game.rooms.types.misc.NameColorManager;
import com.cometproject.server.game.rooms.vote.RoomVote;
import com.cometproject.server.network.messages.outgoing.room.events.RoomPromotionMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;
import com.cometproject.server.storage.queries.rooms.TraxMachineDao;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.util.ConcurrentLRUCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;


public class RoomManager implements Initialisable {

    public static final Logger log = LogManager.getLogger(RoomManager.class.getName());
    public static final int LRU_MAX_ENTRIES = Integer.parseInt(Configuration.currentConfig().getProperty("comet.game.rooms.data.max"));
    public static final int LRU_MAX_LOWER_WATERMARK = Integer.parseInt(Configuration.currentConfig().getProperty("comet.game.rooms.data.lowerWatermark"));
    private static RoomManager roomManagerInstance;
    private ConcurrentLRUCache<Integer, IRoomData> roomDataInstances;

    private Map<Integer, Room> loadedRoomInstances;
    private Map<Integer, Room> unloadingRoomInstances;

    private Map<Integer, RoomPromotion> roomPromotions;

    private final Set<Integer> deletedRooms = Sets.newConcurrentHashSet();

    private Map<Integer, Map<Integer, TraxMachineSong>> traxMachineSongs;

    private WordFilter filterManager;

    private RoomCycle globalCycle;
    private ChatEmotionsManager emotions;
    private ChatEmojiManager emojis;
    private NameColorManager nameColors;

    private ExecutorService executorService;

    private Map<Integer, RoomReloadListener> reloadListeners;

    private RoomVote roomVote;

    public RoomManager() {

    }

    public static RoomManager getInstance() {
        if (roomManagerInstance == null)
            roomManagerInstance = new RoomManager();

        return roomManagerInstance;
    }

    @Override
    public void initialize() {
        this.roomDataInstances = new ConcurrentLRUCache<>(LRU_MAX_ENTRIES, LRU_MAX_LOWER_WATERMARK);

        this.loadedRoomInstances = new ConcurrentHashMap<>();
        this.unloadingRoomInstances = new ConcurrentHashMap<>();
        this.roomPromotions = new ConcurrentHashMap<>();
        this.reloadListeners = new ConcurrentHashMap<>();
        this.emotions = new ChatEmotionsManager();
        this.emojis = new ChatEmojiManager();
        this.nameColors = new NameColorManager();
        this.filterManager = new WordFilter();
        this.globalCycle = new RoomCycle();
        this.traxMachineSongs = new HashMap<>();
        this.loadPromotedRooms();

        this.loadTraxMachineSongs();

        this.globalCycle.start();

        this.executorService = Executors.newFixedThreadPool(Integer.parseInt(Configuration.currentConfig().get("comet.system.roomLoaderThreads")), r -> {
            final Thread roomThread = new Thread(r, "Room-Worker-" + UUID.randomUUID());

            roomThread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());

            return roomThread;
        });

        log.info("RoomManager initialized");
    }

    public void loadTraxMachineSongs() {
        TraxMachineDao.loadSongs();

        log.info("Loaded " + this.traxMachineSongs.size() + " trax machine songs");
    }

    public void loadPromotedRooms() {
        RoomDao.deleteExpiredRoomPromotions();
        RoomDao.getActivePromotions(this.roomPromotions);

        log.info("Loaded " + this.getRoomPromotions().size() + " room promotions");
    }

    public void initializeRoom(Session initializer, int roomId, String password) {
        if (this.deletedRooms.contains(roomId)) return;

        this.executorService.submit(() -> {
            if (initializer != null && initializer.getPlayer() != null) {
                initializer.getPlayer().loadRoom(roomId, password);
            }
        });
    }


    public Room get(int id) {
        if (id < 1) return null;

        if (this.getRoomInstances().containsKey(id)) {
            return this.getRoomInstances().get(id);
        }

        final IRoomData data = GameContext.getCurrent().getRoomService().getRoomData(id);

        if (data == null) {
            return null;
        }

        final Room room = new Room(data);
        room.load();

        this.loadedRoomInstances.put(id, room);
        this.finalizeRoomLoad(room);

        return room;
    }

    private void finalizeRoomLoad(Room room) {
        if (room == null) {
            return;
        }

        room.getItems().onLoaded();
    }

    public void unloadIdleRooms() {

        for (final Room room : this.unloadingRoomInstances.values()) {
            this.executorService.submit(() -> {
                room.dispose();

                if (room.isReloading()) {
                    final Room newRoom = this.get(room.getId());

                    if (newRoom != null) {
                        if (this.reloadListeners.containsKey(room.getId())) {
                            final RoomReloadListener reloadListener = this.reloadListeners.get(newRoom.getId());

                            reloadListener.onReloaded(newRoom);
                            this.reloadListeners.remove(room.getId());
                        }
                    }
                }

            });
        }

        this.unloadingRoomInstances.clear();

        final List<Room> idleRooms = new ArrayList<>();

        for (final Room room : this.loadedRoomInstances.values()) {
            if (room.isIdle()) {
                idleRooms.add(room);
            }
        }

        for (final Room room : idleRooms) {
            this.loadedRoomInstances.remove(room.getId());
            this.unloadingRoomInstances.put(room.getId(), room);
        }
    }

    public void forceUnload(int id) {
        if (this.loadedRoomInstances.containsKey(id)) {
            this.loadedRoomInstances.remove(id).dispose();
        }
    }

    public void removeData(int roomId) {
        this.getRoomDataInstances().remove(roomId);
    }

    public void roomDeleted(int roomId) {
        this.removeData(roomId);
        this.forceUnload(roomId);

        this.deletedRooms.add(roomId);
    }

    public void addReloadListener(int roomId, RoomReloadListener listener) {
        this.reloadListeners.put(roomId, listener);
    }

    public void loadRoomsForUser(Player player) {
        player.getRooms().clear();
        player.getRoomsWithRights().clear();

        final Map<Integer, IRoomData> rooms = RoomDao.getRoomsByPlayerId(player.getId());
        final Map<Integer, IRoomData> roomsWithRights = RoomDao.getRoomsWithRightsByPlayerId(player.getId());

        for (final Map.Entry<Integer, IRoomData> roomEntry : rooms.entrySet()) {
            player.getRooms().add(roomEntry.getKey());

            if (!this.getRoomDataInstances().getMap().containsKey(roomEntry.getKey())) {
                this.getRoomDataInstances().put(roomEntry.getKey(), roomEntry.getValue());
            }
        }

        for (final Map.Entry<Integer, IRoomData> roomEntry : roomsWithRights.entrySet()) {
            player.getRoomsWithRights().add(roomEntry.getKey());

            if (!this.getRoomDataInstances().getMap().containsKey(roomEntry.getKey())) {
                this.getRoomDataInstances().put(roomEntry.getKey(), roomEntry.getValue());
            }
        }
    }

    public List<IRoomData> getRoomsByQuery(String query) {
        final List<IRoomData> rooms = new ArrayList<>();

        // empty query, return empty rooms;
        if (query.equals("owner:")) return rooms;

        if (query.equals("tag:")) return rooms;

        if (query.equals("group:")) return rooms;

        if (query.startsWith("roomname:")) {
            query = query.substring(9);
        }

        final List<IRoomData> roomSearchResults = RoomDao.getRoomsByQuery(query);

        for (final IRoomData data : roomSearchResults) {
            if (!this.getRoomDataInstances().getMap().containsKey(data.getId())) {
                this.getRoomDataInstances().put(data.getId(), data);
            }

            rooms.add(data);
        }

        if (rooms.size() == 0 && !query.toLowerCase().startsWith("owner:")) {
            return this.getRoomsByQuery("owner:" + query);
        }

        return rooms;
    }

    public boolean isActive(int id) {
        return this.getRoomInstances().containsKey(id);
    }

    public int createRoom(String name, String description, CustomFloorMapData model, int category, int maxVisitors, int tradeState, int creationTime, int wallTickness, int floorThickness, String decorations, boolean hideWalls) {
        return RoomDao.createRoom(name, model, description, category, maxVisitors, RoomTradeState.valueOf(tradeState), creationTime, 0, "Battle Ball", wallTickness, floorThickness, decorations, hideWalls);
    }

    public int createRoom(String name, String description, CustomFloorMapData model, int category, int maxVisitors, int tradeState, int creationTime, ISession client, int wallTickness, int floorThickness, String decorations, boolean hideWalls) {
        int roomId = RoomDao.createRoom(name, model, description, category, maxVisitors, RoomTradeState.valueOf(tradeState), creationTime, client.getPlayer().getId(), client.getPlayer().getData().getUsername(), wallTickness, floorThickness, decorations, hideWalls);

        this.loadRoomsForUser((Player) client.getPlayer());

        return roomId;
    }

    public int createRoom(String name, String description, String model, int category, int maxVisitors, int tradeState, int creationTime, Session client) {
        int roomId = RoomDao.createRoom(name, model, description, category, maxVisitors, RoomTradeState.valueOf(tradeState), creationTime, client.getPlayer().getId(), client.getPlayer().getData().getUsername());

        this.loadRoomsForUser(client.getPlayer());

        return roomId;
    }

    public void rightsRoomsUpdate(Session client) {
        this.loadRoomsForUser(client.getPlayer());
    }

    private List<Integer> getActiveAvailableRooms() {
        final List<Integer> rooms = new ArrayList<>();

        for (final Room activeRoom : this.loadedRoomInstances.values()) {
            if (!this.unloadingRoomInstances.containsKey(activeRoom.getId())) {
                final int playerCount = activeRoom.getEntities().playerCount();

                if (playerCount != 0 && playerCount < activeRoom.getData().getMaxUsers() &&
                        activeRoom.getData().getAccess() == RoomAccessType.OPEN) {
                    rooms.add(activeRoom.getId());
                }
            }
        }

        return rooms;
    }

    public int getRandomActiveRoom() {
        final List<Integer> rooms = this.getActiveAvailableRooms();
        final Integer roomId = WiredUtil.getRandomElement(rooms);

        rooms.clear();

        if (roomId != null) {
            return roomId;
        }

        return -1;
    }

    public List<IRoomData> getRoomsByCategory(int category, Player player) {
        return this.getRoomsByCategory(category, 0, player);
    }

    public List<IRoomData> getRoomsByCategory(int category, int minimumPlayers, Player player) {
        final List<IRoomData> rooms = new ArrayList<>();

        for (final Room room : this.getRoomInstances().values()) {
            if (category != -1 && (room.getCategory() == null || room.getCategory().getId() != category)) {
                continue;
            }

            if (!NavigatorSearchService.checkRoomVisibility(player, room)) {
                continue;
            }

            if (room.getEntities() != null && room.getEntities().playerCount() < minimumPlayers) {
                continue;
            }

            rooms.add(room.getData());
        }

        return rooms;
    }

    public void promoteRoom(int roomId, String name, String description) {
        if (this.roomPromotions.containsKey(roomId)) {
            final RoomPromotion promo = this.roomPromotions.get(roomId);
            promo.setTimestampFinish(promo.getTimestampFinish() + (RoomPromotion.DEFAULT_PROMO_LENGTH * 60));

            RoomDao.updatePromotedRoom(promo);
        } else {
            final RoomPromotion roomPromotion = new RoomPromotion(roomId, name, description);
            RoomDao.createPromotedRoom(roomPromotion);

            this.roomPromotions.put(roomId, roomPromotion);
        }

        final Room room = this.get(roomId);

        if (room != null) {
            if (room.getEntities() != null && room.getEntities().hasPlayers()) {
                room.getEntities().broadcastMessage(new RoomPromotionMessageComposer(room.getData(), this.roomPromotions.get(roomId)));
            }
        }
    }

    public boolean hasPromotion(int roomId) {
        return this.roomPromotions.containsKey(roomId) && !this.roomPromotions.get(roomId).isExpired();
    }

    public final ChatEmotionsManager getEmotions() {
        return this.emotions;
    }

    public final ChatEmojiManager getEmojis() {
        return this.emojis;
    }

    public final NameColorManager getNameColors() {
        return this.nameColors;
    }

    public final void reloadNameColors() {
        this.nameColors = new NameColorManager();
    }

    public final void reloadEmojis() {
        this.emojis = new ChatEmojiManager();
    }

    public final Map<Integer, Room> getRoomInstances() {
        return this.loadedRoomInstances;
    }

    private ConcurrentLRUCache<Integer, IRoomData> getRoomDataInstances() {
        return this.roomDataInstances;
    }

    public final RoomCycle getGlobalCycle() {
        return this.globalCycle;
    }


    public final WordFilter getFilter() {
        return filterManager;
    }

    public Map<Integer, RoomPromotion> getRoomPromotions() {
        return roomPromotions;
    }

    public RoomVote getRoomVote() {
        return roomVote;
    }

    public void setRoomVote(RoomVote roomVote) {
        this.roomVote = roomVote;
    }

    public void setTraxMachineSongFromUserId(int userId, TraxMachineSong traxMachineSong) {
        if (this.traxMachineSongs.containsKey(userId)) {
            this.traxMachineSongs.get(userId).put(traxMachineSong.getId(), traxMachineSong);
        }

        final Map<Integer, TraxMachineSong> map = new HashMap<>();
        map.put(traxMachineSong.getId(), traxMachineSong);

        this.traxMachineSongs.put(userId, map);
    }

    public Map<Integer, TraxMachineSong> getTraxMachineSongsFromUserId(int userId) {
        if (!this.traxMachineSongs.containsKey(userId)) {
            return null;
        }

        return this.traxMachineSongs.get(userId);
    }

    public TraxMachineSong getTraxMachineSongFromUserAndSongId(int userId, int songId) {
        final Map<Integer, TraxMachineSong> userSongs = this.getTraxMachineSongsFromUserId(userId);

        if (userSongs == null) {
            return null;
        }

        return this.traxMachineSongs.get(userId).get(songId);
    }
}
