package com.cometproject.server.game.rooms.objects.entities;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.rooms.objects.RoomFloorObject;
import com.cometproject.server.game.rooms.objects.RoomObject;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.types.EntityPathfinder;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.entities.types.ai.BotAI;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.SeatFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom.WiredTriggerCustomIdle;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom.WiredTriggerCustomTotalIdle;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomEntityMovementNode;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.avatar.*;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RoomEntity extends RoomFloorObject implements AvatarEntity {
    public int updatePhase = 0;
    private boolean needsForcedUpdate = false;
    private RoomEntityType entityType;
    private Position walkingGoal;
    private Position positionToSet;
    private int bodyRotation;
    private int headRotation;
    public List<Square> processingPath;

    private int previousSteps = 0;
    private int idleTime;
    private int signTime;
    private int danceId;
    private PlayerEffect teamEffect;
    private PlayerEffect lastEffect;
    private PlayerEffect effect;
    private int handItem;
    private int handItemTimer;
    private boolean needsUpdate;
    private boolean isMoonwalking;
    private boolean overriden;
    private boolean overrideA;
    private boolean isVisible;
    private boolean cancelNextUpdate;
    private boolean doorbellAnswered;
    private boolean walkCancelled = false;
    private boolean canWalk = true;
    private boolean isFreeze = false;
    private boolean freeze = false;
    private boolean isTeleported = false;
    private boolean isIdle = false;
    private boolean isRolling;
    private boolean isBodyRotating = false;
    private boolean isHeadRotating = false;
    public ArrayList<String> addTagUser;
    private int idleTimeWiredWalk;
    private int walkTimeOut;
    private final ConcurrentHashMap<RoomEntityStatus, String> status;

    /**
     * For use with the anti-afk wired
     */
    private int afkTime = 0;

    private final Set<RoomTile> tiles = Sets.newConcurrentHashSet();

    private boolean isRoomMuted = false;

    private final long joinTime;

    private RoomEntity mountedEntity;
    private final Set<RoomEntity> followingEntities = new ConcurrentHashSet<>();

    private long privateChatItemId = 0;

    private final Map<RoomEntityStatus, String> statuses = new ConcurrentHashMap<>();
    private Position pendingWalk;

    private boolean fastWalkEnabled = false;
    private boolean isWarped;
    private RoomTile warpedTile;
    private boolean sendUpdateMessage = true;
    private boolean hasMount = false;

    private boolean warping;
    private boolean clickThrough = false;

    private UserWalkEvent evtWalk;
    public RoomEntity(int identifier, Position startPosition, int startBodyRotation, int startHeadRotation, Room roomInstance) {
        super(identifier, startPosition, roomInstance);

        if (this instanceof PlayerEntity) {
            this.entityType = RoomEntityType.PLAYER;
        } else if (this instanceof BotEntity) {
            this.entityType = RoomEntityType.BOT;
        } else if (this instanceof PetEntity) {
            this.entityType = RoomEntityType.PET;
        }

        this.bodyRotation = startBodyRotation;
        this.headRotation = startHeadRotation;

        this.idleTime = 0;
        this.signTime = 0;
        this.handItem = 0;
        this.handItemTimer = 0;
        this.isRolling = false;

        this.evtWalk = new UserWalkEvent(this);

        this.danceId = 0;
        this.walkTimeOut = (int) Comet.getTime();
        this.status = new ConcurrentHashMap<>();

        this.needsUpdate = false;
        this.isMoonwalking = false;
        this.overriden = false;
        this.isVisible = true;
        this.cancelNextUpdate = false;
        this.addTagUser = new ArrayList<>();

        this.doorbellAnswered = false;

        this.processingPath = new ArrayList<>();

        if (this.getRoom().hasRoomMute()) {
            this.isRoomMuted = true;
        }

        this.joinTime = System.currentTimeMillis();
    }



    public RoomEntityType getEntityType() {
        return this.entityType;
    }

    @Override
    public Position getWalkingGoal() {
        if (this.walkingGoal == null) {
            return this.getPosition();
        } else {
            return this.walkingGoal;
        }
    }

    @Override
    public void setWalkingGoal(int x, int y) {
        this.walkingGoal = new Position(x, y, 0.0);
    }

    public void setWalkingGoal(int x, int y, double z) {
        this.walkingGoal = new Position(x, y, z);
    }

    public void moveTo(Position position) {
        this.moveTo(position.getX(), position.getY());
    }

    @Override
    public void moveTo(int x, int y) {
        if (this.isWarped()) {
            return;
        }

        if (isFreeze) {
            return;
        }

        PlayerEntity playerEntity = null;

        if (this instanceof PlayerEntity) {
            playerEntity = (PlayerEntity) this;
        }

        if (playerEntity != null && (playerEntity.hasAttribute("botcontrol") || playerEntity.hasAttribute("playercontrol"))) {
            if (playerEntity.hasAttribute("botcontrol")) {
                final Object botEntityObject = playerEntity.getAttribute("botcontrol");

                if (botEntityObject != null) {
                    playerEntity.moveTo(new Position(x, y));
                }
            }
            if (playerEntity.hasAttribute("playercontrol")) {
                final Object playerEntityObject = playerEntity.getAttribute("playercontrol");

                if (playerEntityObject != null) {
                    playerEntity.moveTo(new Position(x, y));
                }
            }
            return;
        }


        final RoomTile tile = this.getRoom().getMapping().getTile(x, y);

        if (tile == null) return;

        if ((tile.getState() == RoomTileState.INVALID || tile.getMovementNode() == RoomEntityMovementNode.CLOSED) && tile.getRedirect() == null) {
            if (tile.getMovementNode() == RoomEntityMovementNode.CLOSED) {
                var sqFront = tile.getPosition().squareInFront(this.getBodyRotation());
                if (sqFront != null) {
                    x = sqFront.getX();
                    y = sqFront.getY();
                }

            } else {
                if (playerEntity != null && !playerEntity.usingTeleportItem())
                    return;
            }
        } else {
            if (playerEntity != null && playerEntity.usingTeleportItem())
                return;
        }

        if (tile.getRedirect() != null) {
            x = tile.getRedirect().getX();
            y = tile.getRedirect().getY();
        }

        this.walking = true;
        this.previousSteps = 0;

        // Set the goal we are wanting to achieve
        this.setWalkingGoal(x, y);

        findWalkPath();

       // this.evtWalk.walk(this.getRoom(), x, y);


    }

    public void findWalkPath(){
        // Create a walking path
        List<Square> path = EntityPathfinder.getInstance().makePath(this, new Position(walkingGoal.getX(), walkingGoal.getY()), this.getRoom().getData().getRoomDiagonalType().getKey(), false);

        // Check returned path to see if it calculated one
        if (path == null || path.size() == 0) {
            path = EntityPathfinder.getInstance().makePath(this, new Position(walkingGoal.getX(), walkingGoal.getY()), this.getRoom().getData().getRoomDiagonalType().getKey(), true);

            if (path == null || path.size() == 0) {
                // Reset the goal and return as no path was found
                this.setWalkingGoal(this.getPosition().getX(), this.getPosition().getY());
                return;
            }
        }


        // UnIdle the user and set the path (if the path has nodes it will mean the user is walking)
        this.unIdle();
        this.setWalkingPath(path);
        this.setProcessingPath(path);
    }

    public void sit(double height, int rotation) {
        this.removeStatus(RoomEntityStatus.LAY);

        this.addStatus(RoomEntityStatus.SIT, String.valueOf(height).replace(",", "."));
        this.setHeadRotation(getSitRotation(rotation));
        this.setBodyRotation(getSitRotation(rotation));
        this.markNeedsUpdate(true);
    }

    private int getSitRotation(int rotation) {
        switch (rotation) {
            case 1:
            case 3:
            case 5:
            case 7: {
                rotation++;
                break;
            }
        }

        return rotation;
    }

    public void lookTo(int x, int y, boolean body) {
        if (x == this.getPosition().getX() && y == this.getPosition().getY())
            return;

        final int currentRotation = this.bodyRotation;
        final int rotation = Position.calculateRotation(this.getPosition().getX(), this.getPosition().getY(), x, y, false);
        final int rotationDifference = currentRotation - rotation;

        this.unIdle();

        if (!this.hasStatus(RoomEntityStatus.SIT) && !this.hasStatus(RoomEntityStatus.LAY) && !this.isIdle) {
            if (rotationDifference == 1 || rotationDifference == -1 || rotationDifference == -7) {
                this.setHeadRotation(rotation);
            } else if (body) {
                this.setHeadRotation(rotation);
                this.setBodyRotation(rotation);
            }

            this.markNeedsUpdate();
        }
    }

    public boolean canWalkOn() {
        return true;
    }

    public void lookTo(int x, int y) {
        lookTo(x, y, true);
    }

    @Override
    public Position getPositionToSet() {
        return this.positionToSet;
    }

    @Override
    public void updateAndSetPosition(Position pos) {
        this.positionToSet = pos;
    }

    @Override
    public void markPositionIsSet() {
        this.positionToSet = null;
    }

    public boolean hasPositionToSet() {
        return (this.positionToSet != null);
    }

    @Override
    public int getBodyRotation() {
        return this.bodyRotation;
    }

    @Override
    public void setBodyRotation(int rotation) {
        this.bodyRotation = rotation;
    }

    @Override
    public int getHeadRotation() {
        return this.headRotation;
    }

    @Override
    public void setHeadRotation(int rotation) {
        this.headRotation = rotation;
    }

    @Override
    public List<Square> getProcessingPath() {
        return this.processingPath;
    }

    @Override
    public void setProcessingPath(List<Square> path) {
        this.processingPath = path;
    }

    @Override
    public List<Square> getWalkingPath() {
        return this.processingPath;
    }

    @Override
    public void setWalkingPath(List<Square> path) {
        this.processingPath = path;
    }

    public boolean walking;
    @Override
    public boolean isWalking() {
        return walking || ((this.processingPath != null) && (this.processingPath.size() > 0));
    }


    @Override
    public Map<RoomEntityStatus, String> getStatuses() {
        return this.statuses;
    }

    @Override
    public void addStatus(RoomEntityStatus key, String value) {
        if (this.statuses.containsKey(key)) {
            this.statuses.replace(key, value);
        } else {
            this.statuses.put(key, value);
        }
    }

    @Override
    public void removeStatus(RoomEntityStatus status) {
        if (!this.statuses.containsKey(status)) {
            return;
        }

        this.statuses.remove(status);
    }

    public void clearStatus() {
        this.statuses.clear();
    }

    @Override
    public boolean hasStatus(RoomEntityStatus key) {
        return this.statuses.containsKey(key);
    }

    @Override
    public void markNeedsUpdate() {
        this.needsUpdate = true;
    }

    public void setSendUpdateMessage(boolean sendUpdateMessage) {
        this.sendUpdateMessage = sendUpdateMessage;
    }

    public void markNeedsUpdate(boolean sendMessage) {
        this.needsUpdate = true;
        this.sendUpdateMessage = sendMessage;
    }

    public void markUpdateComplete() {
        this.needsUpdate = false;
    }

    @Override
    public boolean needsUpdate() {
        return this.needsUpdate;
    }

    public boolean isMoonwalking() {
        return this.isMoonwalking;
    }

    public void setIsMoonwalking(boolean isMoonwalking) {
        this.isMoonwalking = isMoonwalking;
    }

    @Override
    public int getIdleTimeWiredWalk() {
        return this.idleTimeWiredWalk;
    }

    @Override
    public void resetIdleTimeWiredWalk() {
        this.idleTimeWiredWalk = 0;
    }

    @Override
    public int getIdleTime() {
        return this.idleTime;
    }

    public boolean isIdle() {
        return this.idleTime >= 600;
    }

    @Override
    public boolean isIdleAndIncrement() {
        if (this instanceof PlayerEntity) {
            if (!this.isWalking()) {
                this.idleTime++;
                this.afkTime++;
            }

            if (this.afkTime >= 5) {
                WiredTriggerCustomTotalIdle.executeTriggers((PlayerEntity) this);
            }

            if (this.idleTime >= 600) {
                if (!this.isIdle) {
                    this.isIdle = true;
                    WiredTriggerCustomIdle.executeTriggers((PlayerEntity) this);
                    WiredTriggerCustomTotalIdle.executeTriggers((PlayerEntity) this);
                    this.getRoom().getEntities().broadcastMessage(new IdleStatusMessageComposer((PlayerEntity) this, true));
                }
            }

//            if (this.afkTime >= this.getRoom().getData().getUserIdleTicks()) {
//                this.resetAfkTimer();
//                WiredTriggerCustomIdle.executeTriggers((PlayerEntity) this);
//                WiredTriggerCustomTotalIdle.executeTriggers((PlayerEntity) this);
//            }
        }

        return this.isIdle;
    }

    @Override
    public void resetIdleTime() {
        this.idleTime = 0;
    }

    @Override
    public void setIdle() {
        this.idleTime = 600;
    }

    public boolean handItemNeedsRemove() {
        if (this.handItemTimer == -999)
            return false;

        this.handItemTimer--;

        return this.handItemTimer <= 0;
    }

    public void unIdle() {
        final boolean sendUpdate = this.isIdle;
        this.isIdle = false;
        this.resetIdleTime();
        this.resetAfkTimer();

        if (this instanceof BotEntity) {
            return;
        }

        if (sendUpdate) {
            this.getRoom().getEntities().broadcastMessage(new IdleStatusMessageComposer((PlayerEntity) this, false));
        }
    }

    public void resetAfkTimer() {
        this.afkTime = 0;
    }

    public int getAfkTime() {
        return this.afkTime;
    }

    @Override
    public int getSignTime() {
        return this.signTime;
    }

    @Override
    public void markDisplayingSign() {
        this.signTime = 6;
    }

    @Override
    public boolean isDisplayingSign() {
        this.signTime--;

        if (this.signTime <= 0) {
            if (this.signTime < 0) {
                this.signTime = 0;
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getDanceId() {
        return this.danceId;
    }

    @Override
    public void setDanceId(int danceId) {
        this.danceId = danceId;
    }

    @Override
    public PlayerEffect getCurrentEffect() {
        return this.effect;
    }

    @Override
    public int getHandItem() {
        return this.handItem;
    }

    @Override
    public void carryItem(int id) {
        this.carryItem(id, 240);
    }

    public void carryItem(int id, int timer) {
        this.handItem = id;
        this.handItemTimer = timer;

        this.getRoom().getEntities().broadcastMessage(new HandItemMessageComposer(this.getId(), handItem));
    }

    @Override
    public void carryItem(int id, boolean timer) {
        if (timer) {
            this.carryItem(id);
            return;
        }

        this.handItem = id;
        this.handItemTimer = -999;

        this.getRoom().getEntities().broadcastMessage(new HandItemMessageComposer(this.getId(), handItem));
    }

    @Override
    public int getHandItemTimer() {
        return this.handItemTimer;
    }

    @Override
    public void setHandItemTimer(int time) {
        this.handItemTimer = time;
    }

    @Override
    public void applyEffect(PlayerEffect effect) {
        if (this instanceof PetEntity)
            return;

        if (effect == null) {
            if (this.teamEffect != null && this.effect != null) {
                this.applyEffect(teamEffect);
                return;
            }

            this.getRoom().getEntities().broadcastMessage(new ApplyEffectMessageComposer(this.getId(), 0));
        } else if (this instanceof PlayerEntity) {
            final PlayerEntity playerEntity = (PlayerEntity) this;
            int effectId = effect.getEffectId();
            final Integer minimumRank = PermissionsManager.getInstance().getEffects().get(effect.getEffectId());

            if (minimumRank != null && playerEntity.getPlayer().getData().getRank() < minimumRank) {
                effectId = 10;
                effect = new PlayerEffect(10);
            }

            this.getRoom().getEntities().broadcastMessage(new ApplyEffectMessageComposer(this.getId(), effectId));
        } else {
            this.getRoom().getEntities().broadcastMessage(new ApplyEffectMessageComposer(this.getId(), effect.getEffectId()));
        }

        if (effect != null && effect.expires()) {
            this.lastEffect = this.effect;
        }

        this.effect = effect;
    }

    public void applyTeamEffect(PlayerEffect effect) {
        this.teamEffect = effect;

        this.applyEffect(effect);
    }

    public boolean isOverriden() {
        return this.overriden;
    }
    public boolean isClickThrough() {
        return this.clickThrough;
    }

    public void setClickThrough(boolean clickThrough) {
        this.clickThrough = clickThrough;
    }
    public void setOverriden(boolean overriden) {
        this.overriden = overriden;
    }

    public boolean isOverrideA() {
        return this.overrideA;
    }

    public void setOverrideA(boolean overriden) {
        this.overrideA = overriden;
    }

    public abstract boolean joinRoom(Room room, String password);

    protected abstract void finalizeJoinRoom();

    public abstract void leaveRoom(boolean isOffline, boolean isKick, boolean toHotelView);

    public abstract boolean onChat(String message);

    public abstract boolean onRoomDispose();

    public boolean isVisible() {
        return isVisible;
    }

    public void updateVisibility(boolean isVisible) {
        if (isVisible && !this.isVisible) {
            this.getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(this));
        } else {
            this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));
        }

        this.isVisible = isVisible;
    }

    public void refresh() {
        this.updateVisibility(false);
        this.updateVisibility(true);
        this.markNeedsUpdate();
    }

    public void cancelWalk() {
        this.setWalkCancelled(true);
        this.markNeedsUpdate();
    }

    public boolean isHeadRotating() {
        return isHeadRotating;
    }

    public void setHeadRotating(boolean value) {
        this.isHeadRotating = value;
    }

    public boolean isBodyRotating() {
        return isBodyRotating;
    }

    public void setBodyRotating(boolean value) {
        this.isBodyRotating = value;
    }

    public void teleportToItemImmediately(RoomItemFloor itemFloor) {
        this.teleportToObjectImmediately(itemFloor);
    }

    public void teleportToItem(RoomItemFloor itemFloor) {
        this.teleportToObject(itemFloor);
    }

    public void teleportToEntity(RoomEntity entity) {
        this.teleportToObject(entity);
    }

    public void teleportToObjectImmediately(RoomObject roomObject) {
        this.applyEffect(new PlayerEffect(4, 3));

        this.warpedTile = this.getRoom().getMapping().getTile(this.getPosition());

        final Position position = roomObject.getPosition().copy();
        position.setZ(roomObject.getTile().getWalkHeight());

        this.cancelWalk();
        this.newWarpImmediately(position, false);
    }

    public void newWarpImmediately(Position position, boolean cancelNextUpdate) {
        if (cancelNextUpdate) {
            this.cancelNextUpdate();
        } else {
            this.updatePhase = 1;
        }

        this.needsForcedUpdate = true;
        this.updateAndSetPosition(position);
        this.markNeedsUpdate();

        final RoomTile tile = this.getRoom().getMapping().getTile(position);

        if (tile == null)
            return;

        this.addToTile(tile);
    }

    public void teleportToObject(RoomObject roomObject) {
        this.warpedTile = this.getRoom().getMapping().getTile(this.getPosition());
        this.cancelWalk();

        if (roomObject == null) {
            return;
        }

        this.warp(roomObject.getPosition());
    }

    public void warp(Position position, boolean cancelNextUpdate) {
        if (this instanceof PlayerEntity) {
            ((PlayerEntity) this).setAttribute("tp", true);
        }

        if (cancelNextUpdate) {
            this.cancelNextUpdate();
        } else {
            this.updatePhase = 1;
        }

        this.needsForcedUpdate = true;

        this.updateAndSetPosition(position);
        this.markNeedsUpdate();

        final RoomTile tile = this.getRoom().getMapping().getTile(position);

        if (tile != null) {
            this.addToTile(tile);

            if (tile.getTopItemInstance() != null) {
                if (tile.getTopItemInstance() instanceof SeatFloorItem)
                    ((SeatFloorItem) tile.getTopItemInstance()).onEntityStepOn(this, false);
                else
                    tile.getTopItemInstance().onEntityStepOn(this);
            }
        }
    }

    public void warpBanzai(final Position position, final boolean cancelNextUpdate) {
        if (cancelNextUpdate) {
            this.cancelNextUpdate();
        } else {
            this.updatePhase = 1;
        }
        this.needsForcedUpdate = true;
        this.updateAndSetPosition(position);
        this.markNeedsUpdate();
        final RoomTile tile = this.getRoom().getMapping().getTile(position);
        if (tile != null) {
            tile.getEntities().add(this);
        }
    }

    @Override
    public void warpBanzai(final Position position) {
        if (this.needsForcedUpdate) {
            return;
        }
        this.warpBanzai(position, true);
    }

    @Override
    public void warp(Position position) {
        this.warp(position, true);
    }

    @Override
    public void warpImmediately(Position position) {
        this.setPosition(position);
        this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(this));

        final RoomTile tile = this.getRoom().getMapping().getTile(position);

        if (tile != null) {
            this.addToTile(tile);

            if (tile.getTopItemInstance() != null) {
                if (tile.getTopItemInstance() instanceof SeatFloorItem)
                    ((SeatFloorItem) tile.getTopItemInstance()).onEntityStepOn(this, false);
                else
                    tile.getTopItemInstance().onEntityStepOn(this);
            }
        }
    }

    public boolean needsUpdateCancel() {
        if (this.cancelNextUpdate) {
            this.cancelNextUpdate = false;
            return true;
        } else {
            return false;
        }
    }

    public void cancelNextUpdate() {
        this.cancelNextUpdate = true;
    }

    public boolean isDoorbellAnswered() {
        return this.doorbellAnswered;
    }

    public void setDoorbellAnswered(boolean b) {
        this.doorbellAnswered = b;
    }

    public PlayerEffect getLastEffect() {
        return lastEffect;
    }

    public void setLastEffect(PlayerEffect lastEffect) {
        this.lastEffect = lastEffect;
    }

    public boolean isWalkCancelled() {
        return walkCancelled;
    }

    public void setWalkCancelled(boolean walkCancelled) {
        this.walkCancelled = walkCancelled;
    }

    public RoomEntity getMountedEntity() {
        if (this.mountedEntity == null) return null;

        if (this.getRoom().getEntities().getEntity(this.mountedEntity.getId()) == null) {
            return null;
        }

        return mountedEntity;
    }

    public void setMountedEntity(RoomEntity mountedEntity) {
        this.mountedEntity = mountedEntity;
    }

    public boolean hasMount() {
        return hasMount;
    }

    public void setHasMount(boolean hasMount) {
        this.hasMount = hasMount;
    }

    @Override
    public void kick() {
        this.leaveRoom(false, true, true);
    }

    public boolean canWalk() {
        return canWalk;
    }

    public void setCanWalk(boolean canWalk) {
        this.canWalk = canWalk;
    }

    public void setFreeze(boolean Freeze) {
        this.freeze = Freeze;
    }

    public boolean getFreeze() {
        return this.freeze;
    }

    public boolean isFreeze() {
        return isFreeze;
    }

    public void setIsFreeze(boolean isFreeze) {
        this.isFreeze = isFreeze;
    }

    @Override
    public boolean equals(Object entity) {
        if (entity instanceof RoomEntity) {
            return ((RoomEntity) entity).getId() == this.getId();
        }

        return false;
    }

    public ArrayList getTagUser() {
        return addTagUser;
    }

    public boolean isRoomMuted() {
        return isRoomMuted;
    }

    public void setRoomMuted(boolean isRoomMuted) {
        this.isRoomMuted = isRoomMuted;
    }

    @Override
    public long getJoinTime() {
        return joinTime;
    }

    public long getPrivateChatItemId() {
        return privateChatItemId;
    }

    public void setPrivateChatItemId(long privateChatItemId) {
        this.privateChatItemId = privateChatItemId;
    }

    public BotAI getAI() {
        return null;
    }

    public Set<RoomEntity> getFollowingEntities() {
        return followingEntities;
    }

    public Position getPendingWalk() {
        return pendingWalk;
    }

    public void setPendingWalk(Position pendingWalk) {
        this.pendingWalk = pendingWalk;
    }

    public void toggleFastWalk() {
        this.fastWalkEnabled = !this.fastWalkEnabled;
    }

    public boolean isFastWalkEnabled() {
        return this.fastWalkEnabled;
    }

    public void setFastWalkEnabled(boolean fastWalkEnabled) {
        this.fastWalkEnabled = fastWalkEnabled;
    }

    public boolean isWarped() {
        return isWarped;
    }

    public void setWarped(boolean warped) {
        isWarped = warped;
    }

    public int getPreviousSteps() {
        return previousSteps;
    }

    public void incrementPreviousSteps() {
        this.previousSteps++;
    }

    public boolean sendUpdateMessage() {
        return sendUpdateMessage;
    }

    public boolean isTeleported() {
        return isTeleported;
    }

    public void setTeleported(boolean isTeleported) {
        this.isTeleported = isTeleported;
    }

    public boolean isWarping() {
        return warping;
    }

    public void setWarping(boolean warping) {
        this.warping = warping;
    }

    public RoomTile getWarpedTile() {
        return warpedTile;
    }

    public void setWarpedTile(RoomTile warpedTile) {
        this.warpedTile = warpedTile;
    }

    public void removeFromTile(RoomTile tile) {
        tile.getEntities().remove(this);
        this.tiles.remove(tile);
    }

    public void addToTile(RoomTile tile) {
        if (this.tiles.size() != 0) {
            for (final RoomTile oldTile : this.tiles) {
                oldTile.getEntities().remove(this);
            }

            this.tiles.clear();
        }

        tile.getEntities().add(this);
        this.tiles.add(tile);
    }

    public Set<RoomTile> getTiles() {
        return tiles;
    }

    public void setStatusType(int statusType) {
    }

    public int getWalkTimeOut() {
        return this.walkTimeOut;
    }

    public void setWalkTimeOut(int walkTimeOut) {
        this.walkTimeOut = walkTimeOut;
    }

    public String getStatus(RoomEntityStatus key) {
        return this.status.get(key);
    }

    public boolean isRolling() {
        return this.isRolling;
    }

    public void setRolling(final boolean rolling) {
        this.isRolling = rolling;
    }

    public void setNeedsForcedUpdate(boolean needsForcedUpdate) {
        this.needsForcedUpdate = needsForcedUpdate;
    }

    public boolean isNeedsForcedUpdate() {
        return needsForcedUpdate;
    }

    public boolean usingTeleportItem() {
        return this.hasAttribute("interacttpencours") && this.hasAttribute("tptpencours");
    }
}
