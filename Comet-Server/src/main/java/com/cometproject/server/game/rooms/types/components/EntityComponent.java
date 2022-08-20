package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.MonsterPlantEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.TeleporterFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.types.RoomMessageType;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.RoomRatingMessageComposer;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class EntityComponent {
    private static final Logger log = LogManager.getLogger(EntityComponent.class.getName());
    private final Map<Integer, RoomEntity> entities = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> playerIdToEntity = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> botIdToEntity = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> petIdToEntity = new ConcurrentHashMap<>();
    private final Map<String, Integer> nameToPlayerEntity = new ConcurrentHashMap<>();

    private final Set<PlayerEntity> playerEntities = new ConcurrentHashSet<>();

    private final Room room;
    private final AtomicInteger entityIdGenerator = new AtomicInteger();

    public EntityComponent(Room room) {
        this.room = room;
    }

    public List<RoomEntity> getEntitiesAt(Position position) {
        final RoomTile tile = this.getRoom().getMapping().getTile(position.getX(), position.getY());

        if (tile != null && tile.getEntities().size() >= 1) {
            return new ArrayList<>(tile.getEntities());
        }

        return new ArrayList<>();
    }

    public boolean positionHasEntity(Position position) {
        final RoomTile tile = this.getRoom().getMapping().getTile(position.getX(), position.getY());

        if (tile != null) {
            return tile.getEntities().size() != 0;
        }

        return false;
    }

    public boolean positionHasEntity(Position position, final Set<RoomEntity> ignoredEntities) {
        final RoomTile tile = this.getRoom().getMapping().getTile(position.getX(), position.getY());

        if (tile != null) {
            for (final RoomEntity entity : tile.getEntities()) {
                if (!ignoredEntities.contains(entity)) return true;
            }
        }

        return false;
    }

    public void broadcastMessageModeBuild(RoomTile tile) {
        for (final RoomEntity entity : this.entities.values()) {
            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                if (playerEntity.getPlayer() == null) continue;

                if (playerEntity.hasAttribute("setz.height") || playerEntity.hasAttribute("build.activated"))
                    continue;

                playerEntity.getPlayer().getSession().send(new UpdateStackMapMessageComposer(tile));
            }
        }
    }

    public PlayerEntity createEntity(Player player) {
        Position startPosition = new Position(this.getRoom().getModel().getRoomModelData().getDoorX(), this.getRoom().getModel().getRoomModelData().getDoorY(), this.getRoom().getModel().getDoorZ());

        if (player.isTeleporting()) {
            final RoomItemFloor item = this.room.getItems().getFloorItem(player.getTeleportId());

            if (item != null) {
                startPosition = new Position(item.getPosition().getX(), item.getPosition().getY(), item.getPosition().getZ());
            }
        }

        final int doorRotation = this.getRoom().getModel().getRoomModelData().getDoorRotation();
        final PlayerEntity entity = new PlayerEntity(player, this.getFreeId(), startPosition, doorRotation, doorRotation, this.getRoom());

        if (player.isTeleporting()) {
            final RoomItemFloor flItem = this.room.getItems().getFloorItem(player.getTeleportId());

            if ((flItem instanceof TeleporterFloorItem)) {
                ((TeleporterFloorItem) flItem).handleIncomingEntity(entity, null);
            }
        }

        return entity;
    }

    public void addEntity(RoomEntity entity) {
        if (entity.getEntityType() == RoomEntityType.PLAYER) {
            final PlayerEntity playerEntity = (PlayerEntity) entity;

            this.nameToPlayerEntity.put(entity.getUsername(), ((PlayerEntity) entity).getPlayerId());
            this.playerIdToEntity.put(playerEntity.getPlayerId(), playerEntity.getId());
            this.playerEntities.add(playerEntity);
        } else if (entity.getEntityType() == RoomEntityType.BOT) {
            final BotEntity botEntity = (BotEntity) entity;

            this.botIdToEntity.put(botEntity.getBotId(), botEntity.getId());
        } else if (entity.getEntityType() == RoomEntityType.PET) {
            final PetEntity petEntity = (PetEntity) entity;

            this.petIdToEntity.put(petEntity.getData().getId(), petEntity.getId());
        }

        this.entities.put(entity.getId(), entity);
    }

    public void removeEntity(RoomEntity entity) {
        final RoomTile tile = this.getRoom().getMapping().getTile(entity.getPosition());

        if (tile != null) {
            entity.removeFromTile(tile);
        }

        // Handle removing entity specifics
        if (entity.getEntityType() == RoomEntityType.PLAYER) {
            final PlayerEntity playerEntity = (PlayerEntity) entity;

            this.playerIdToEntity.remove(playerEntity.getPlayerId());
            this.nameToPlayerEntity.remove(playerEntity.getUsername());
            this.playerEntities.remove(playerEntity);
        } else if (entity.getEntityType() == RoomEntityType.BOT) {
            final BotEntity botEntity = (BotEntity) entity;

            this.botIdToEntity.remove(botEntity.getBotId());
        } else if (entity.getEntityType() == RoomEntityType.PET) {
            final PetEntity petEntity = (PetEntity) entity;

            this.petIdToEntity.remove(petEntity.getData().getId());
        }

        this.entities.remove(entity.getId());
    }

    public void broadcastSocket(Object obj, boolean usersWithRightsOnly) {
        if (obj == null) return;

        for (final RoomEntity entity : this.entities.values()) {
            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                if (playerEntity.getPlayer().getSession().getWsChannel() == null) {
                    System.out.print("Handled exception on Socket.");
                    continue;
                }

                /*if (usersWithRightsOnly && !this.room.getRights().hasRights(playerEntity.getPlayerId()) && !playerEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                    continue;
                }*/
            }
        }
    }

    public void broadcastMessage(MessageComposer msg, boolean usersWithRightsOnly) {
        broadcastMessage(msg, usersWithRightsOnly, RoomMessageType.GENERIC_COMPOSER);
    }
    public void broadcastMessagesQueue(List<MessageComposer> messages, boolean usersWithRightsOnly, RoomMessageType type){
        for (final PlayerEntity playerEntity : this.playerEntities) {
            if (playerEntity.getPlayer() == null)
                continue;

            if (usersWithRightsOnly && !this.room.getRights().hasRights(playerEntity.getPlayerId()) && !playerEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                continue;
            }

            if (type == RoomMessageType.BOT_CHAT && playerEntity.getPlayer().botsMuted()) {
                continue;
            }

            if (type == RoomMessageType.PET_CHAT && playerEntity.getPlayer().petsMuted()) {
                continue;
            }

            for (final MessageComposer msg:messages) {
                playerEntity.getPlayer().getSession().sendQueue(msg);
            }
            playerEntity.getPlayer().getSession().flush();
        }
    }

    public void broadcastMessage(MessageComposer msg, boolean usersWithRightsOnly, RoomMessageType type) {
        if (msg == null) return;

        for (final PlayerEntity playerEntity : this.playerEntities) {
            if (playerEntity.getPlayer() == null)
                continue;

            if (usersWithRightsOnly && !this.room.getRights().hasRights(playerEntity.getPlayerId()) && !playerEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                continue;
            }

            if (type == RoomMessageType.BOT_CHAT && playerEntity.getPlayer().botsMuted()) {
                continue;
            }

            if (type == RoomMessageType.PET_CHAT && playerEntity.getPlayer().petsMuted()) {
                continue;
            }

            playerEntity.getPlayer().getSession().send(msg);
        }
    }

    public void broadcastChatMessage(MessageComposer msg, PlayerEntity sender) {
        for (final PlayerEntity playerEntity : this.playerEntities) {
            if (playerEntity.getPlayer() == null) continue;

            if (!playerEntity.getPlayer().ignores(sender.getPlayerId()))
                playerEntity.getPlayer().getSession().send(msg);
        }
    }

    public void broadcastMessage(MessageComposer msg) {
        broadcastMessage(msg, false);
    }

    public RoomEntity getEntity(int id) {
        return this.entities.get(id);
    }

    public PlayerEntity getEntityByPlayerId(int id) {
        if (!this.playerIdToEntity.containsKey(id)) {
            return null;
        }

        final RoomEntity roomEntity = this.entities.get(this.playerIdToEntity.get(id));

        if (roomEntity == null || roomEntity.getEntityType() != RoomEntityType.PLAYER) {
            return null;
        }

        return (PlayerEntity) roomEntity;
    }

    public PlayerEntity getPlayerEntityByName(final String username) {
        final Integer playerId = this.nameToPlayerEntity.get(username);

        if (playerId != null) {
            return this.getEntityByPlayerId(playerId);
        }

        return null;
    }

    public RoomEntity getEntityByName(String name, RoomEntityType type) {
        for (final RoomEntity entity : this.getAllEntities().values()) {
            if (entity.getUsername() == null) continue;

            if (entity.getUsername().equalsIgnoreCase(name) && entity.getEntityType() == type) {
                return entity;
            }
        }

        return null;
    }

    public BotEntity getEntityByBotId(int id) {
        if (!this.botIdToEntity.containsKey(id)) {
            return null;
        }

        final RoomEntity roomEntity = this.entities.get(this.botIdToEntity.get(id));

        if (roomEntity == null || roomEntity.getEntityType() != RoomEntityType.BOT) {
            return null;
        }

        return (BotEntity) roomEntity;
    }

    public PetEntity getEntityByPetId(int id) {
        if (!this.petIdToEntity.containsKey(id)) {
            return null;
        }

        final RoomEntity roomEntity = this.entities.get(this.petIdToEntity.get(id));

        if (roomEntity == null || roomEntity.getEntityType() != RoomEntityType.PET) {
            return null;
        }

        return (PetEntity) roomEntity;
    }

    public MonsterPlantEntity getEntityByPlantId(int id) {
        if (!this.petIdToEntity.containsKey(id)) {
            return null;
        }

        final RoomEntity roomEntity = this.entities.get(this.petIdToEntity.get(id));

        if (roomEntity == null || roomEntity.getEntityType() != RoomEntityType.PET) {
            return null;
        }

        return (MonsterPlantEntity) roomEntity;
    }

    public List<BotEntity> getBotEntities() {
        final List<BotEntity> entities = new ArrayList<>();

        for (final RoomEntity entity : this.entities.values()) {
            if (entity.getEntityType() == RoomEntityType.BOT) {
                entities.add((BotEntity) entity);
            }
        }

        return entities;
    }

    public List<PetEntity> getPetEntities() {
        final List<PetEntity> entities = new ArrayList<>();

        for (final RoomEntity entity : this.entities.values()) {
            if (entity.getEntityType() == RoomEntityType.PET) {
                entities.add((PetEntity) entity);
            }
        }

        return entities;
    }

    public List<PlayerEntity> getPlayerEntities() {
        final List<PlayerEntity> entities = new ArrayList<>();

        if (this.entities.size() < 1) {
            return entities;
        }

        for (final RoomEntity entity : this.entities.values()) {
            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                entities.add((PlayerEntity) entity);
            }
        }

        return entities;
    }

    public List<PlayerEntity> getWhisperSeers() {
        final List<PlayerEntity> entities = new ArrayList<>();

        if (this.entities.size() < 1) {
            return entities;
        }

        for (final RoomEntity entity : this.entities.values()) {
            if (entity.getEntityType() != RoomEntityType.PLAYER) continue;

            if (((PlayerEntity) entity).getPlayer().getPermissions().getRank().roomSeeWhispers()) {
                entities.add((PlayerEntity) entity);
            }
        }

        return entities;
    }

    public void refreshScore() {
        for (final PlayerEntity entity : getPlayerEntities()) {
            if(entity.getPlayer() == null || entity.getPlayer().getSession() == null) continue;

            entity.getPlayer().getSession().send(new RoomRatingMessageComposer(room.getData().getScore(), entity.canRateRoom()));
        }
    }

    protected int getFreeId() {
        return this.entityIdGenerator.incrementAndGet();
    }

    public int count() {
        int count = 0;

        for (final RoomEntity entity : this.entities.values()) {
            if (entity.isVisible()) count++;
        }

        return count;
    }

    public int playerCount() {
        final List<Integer> countedEntities = Lists.newArrayList();

        try {
            for (final RoomEntity entity : this.entities.values()) {
                if (!(entity instanceof PlayerEntity) || !entity.isVisible()) continue;

                if (!countedEntities.contains(((PlayerEntity) entity).getPlayerId())) {
                    countedEntities.add(((PlayerEntity) entity).getPlayerId());
                }
            }

            return countedEntities.size();
        } catch (Exception e) {
            return 0;
        } finally {
            countedEntities.clear();
        }
    }

    public boolean hasPlayers() {
        return ! this.playerIdToEntity.isEmpty();
    }

    public Map<Integer, RoomEntity> getAllEntities() {
        return this.entities;
    }

    public Room getRoom() {
        return this.room;
    }

    public void dispose() {
        for (final Map.Entry<Integer, RoomEntity> entity : this.entities.entrySet()) {
            entity.getValue().onRoomDispose();
        }

        this.entities.clear();

        this.playerIdToEntity.clear();
        this.botIdToEntity.clear();
        this.petIdToEntity.clear();
    }
}
