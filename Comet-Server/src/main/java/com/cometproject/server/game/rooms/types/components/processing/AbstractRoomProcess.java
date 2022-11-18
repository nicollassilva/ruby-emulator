package com.cometproject.server.game.rooms.types.components.processing;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.rooms.RoomProcessingType;
import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.WiredTriggerExecutor;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.EffectFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.TeleportPadFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.pet.breeding.BreedingBoxFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerBotReachedFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.TimeSpan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AbstractRoomProcess implements CometTask {
    protected final Room room;

    protected final Logger log;
    private ScheduledFuture processFuture;
    private ScheduledFuture processFutureRoom;
    protected boolean active = false;

    private final boolean adaptiveProcessTimes;
    private List<Long> processTimes;

    private long lastProcess = 0;

    private final long delay;

    private List<RoomEntity> entitiesToUpdate;
    private List<PlayerEntity> playersToRemove;


    public AbstractRoomProcess(Room room, long delay) {
        this.room = room;
        this.delay = delay;
        this.log = LogManager.getLogger("Room Process [" + room.getData().getName() + ", #" + room.getId() + "]");

        this.adaptiveProcessTimes = CometSettings.ADAPTIVE_ENTITY_PROCESS_DELAY;

        this.entitiesToUpdate = new ArrayList<>();
        this.playersToRemove = new ArrayList<>();

        //  this.playerWalkTask = this::startProcessing;


    }

    //private final Runnable playerWalkTask;

    public void tick() {
        final long timeStart = System.currentTimeMillis();

        if (!this.active) {
            return;
        }


        final long timeSinceLastProcess = this.lastProcess == 0 ? 0 : (System.currentTimeMillis() - this.lastProcess);
        this.lastProcess = System.currentTimeMillis();

        if (this.getProcessTimes() != null && this.getProcessTimes().size() < 30) {
            log.info("Time since last cleanWord: " + timeSinceLastProcess + "ms");
        }

        try {
            this.startProcessing();
        } catch (Exception ex) {
            log.error("Error during room entity processing [1]", ex);
            ex.printStackTrace();
        }

        final TimeSpan span = new TimeSpan(timeStart, System.currentTimeMillis());

        if (this.getProcessTimes() != null && this.getProcessTimes().size() < 30) {
            this.getProcessTimes().add(span.toMilliseconds());
        }

        if (this.adaptiveProcessTimes) {
            CometThreadManager.getInstance().executeSchedule(this, 240 - span.toMilliseconds(), TimeUnit.MILLISECONDS);
        }

        // System.out.println("room cycle took " + (System.currentTimeMillis() - timeStart) + "ms");

    }

    public void start() {
        if (this.active) {
            stop();
        }

        if (this.adaptiveProcessTimes) {
            CometThreadManager.getInstance().executeSchedule(this, 235, TimeUnit.MILLISECONDS);
        } else {
            this.processFuture = CometThreadManager.getInstance().executePeriodic(this, this.delay, 500, TimeUnit.MILLISECONDS);
        }

        this.processFutureRoom = CometThreadManager.getInstance().executePeriodic(() -> {
            try {
                getRoom().tick();
            } catch (Exception e) {
                log.error("Error while cycling room: " + room.getData().getId() + ", " + room.getData().getName(), e);
            }
        }, 0, 500, TimeUnit.MILLISECONDS);


        this.active = true;

        if (Comet.isDebugging) {
            log.debug("Processing started");
        }
    }

    public void stop() {
        if (this.getProcessTimes() != null) {
            this.getProcessTimes().clear();
        }

        if (this.processFuture != null) {
            this.active = false;

            if (!this.adaptiveProcessTimes)
                this.processFuture.cancel(false);

            if (Comet.isDebugging) {
                log.debug("Processing stopped");
            }
        }

        if (this.processFutureRoom != null) {
            this.active = false;


            this.processFutureRoom.cancel(false);

            if (Comet.isDebugging) {
                log.debug("Processing room stopped");
            }
        }

        this.entitiesToUpdate.clear();
        this.playersToRemove.clear();


    }

    @Override
    public void run() {
        this.tick();
    }

    public void setDelay(int time) {
        this.processFuture.cancel(false);
        this.processFuture = CometThreadManager.getInstance().executePeriodic(this, 0L, time, TimeUnit.MILLISECONDS);
    }

    private void startProcessing() {

        final Map<Integer, RoomEntity> entities = this.room.getEntities().getAllEntities();


        List<RoomEntity> arrayList = new ArrayList<>(entities.values());

        if (this.room.getData().getRoomProcessType() != RoomProcessingType.DEFAULT) {
            Collections.shuffle(arrayList);
        }


        for (final RoomEntity entity : arrayList) {
            if (entity == null)
                continue;

            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                try {
                    if (playerEntity.getPlayer() == null || playerEntity.getPlayer().isDisposed || playerEntity.getPlayer().getSession() == null) {
                        playersToRemove.add(playerEntity);
                        return;
                    }
                } catch (Exception e) {
                    log.warn("Failed to remove null player from room - user data was null");
                    return;
                }

                final boolean playerNeedsRemove = processEntity(playerEntity);

                if (playerNeedsRemove) {
                    playersToRemove.add(playerEntity);
                }
            } else {
                if (entity.getAI() != null) {
                    entity.getAI().onTick();
                }
                if (entity.getEntityType() == RoomEntityType.BOT) {
                    processEntity(entity);
                } else if (entity.getEntityType() == RoomEntityType.PET && entity.getMountedEntity() == null) {
                    processEntity(entity);
                }
            }

            if ((entity.needsUpdate() && !entity.needsUpdateCancel() || entity.isNeedsForcedUpdate()) && entity.isVisible()) {
                if (entity.isNeedsForcedUpdate() && entity.updatePhase == 1) {
                    entity.setNeedsForcedUpdate(false);
                    entity.updatePhase = 0;

                    entitiesToUpdate.add(entity);
                } else if (entity.isNeedsForcedUpdate()) {
                    entity.removeStatus(RoomEntityStatus.MOVE);


                    entity.updatePhase = 1;
                    entitiesToUpdate.add(entity);
                } else {
                    if (entity instanceof PlayerEntity && entity.getMountedEntity() != null) {
                        processEntity(entity.getMountedEntity());
                        entity.getMountedEntity().markUpdateComplete();
                        entitiesToUpdate.add(entity.getMountedEntity());
                    }

                    if (entity.isWarped()) {
                        entity.setWarped(false);
                    }

                    entity.markUpdateComplete();
                    entitiesToUpdate.add(entity);
                }
            }
        }



        if (entitiesToUpdate.size() > 0) {
            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entitiesToUpdate));
        }

        for (final RoomEntity entity : entitiesToUpdate) {
            if (entity.updatePhase == 1) continue;

            if (this.updateEntityStuff(entity) && entity instanceof PlayerEntity) {
                playersToRemove.add((PlayerEntity) entity);
            }
        }


        for (final PlayerEntity entity : playersToRemove) {
            if (entity == null)
                continue;

            entity.leaveRoom(entity.getPlayer() == null, false, true);
        }

        playersToRemove.clear();
        entitiesToUpdate.clear();
    }

    public boolean updateEntityStuff(RoomEntity entity) {
        if (entity.getPositionToSet() != null) {
            if ((entity.getPositionToSet().getX() == this.room.getModel().getDoorX()) && (entity.getPositionToSet().getY() == this.room.getModel().getDoorY())) {
                boolean leaveRoom = true;
                final List<RoomItemFloor> floorItemsAtDoor = this.getRoom().getItems().getItemsOnSquare(entity.getPositionToSet().getX(), entity.getPositionToSet().getY());

                if (!floorItemsAtDoor.isEmpty()) {
                    for (final RoomItemFloor floorItem : floorItemsAtDoor) {
                        if (floorItem instanceof TeleportPadFloorItem) {
                            leaveRoom = false;
                            break;
                        }
                    }
                }

                if (leaveRoom) {
                    entity.updateAndSetPosition(null);
                    return true;
                }
            }

            entity.removeStatus(RoomEntityStatus.SIT);

            // Create the new position
            final Position newPosition = entity.getPositionToSet().copy();
            final Position oldPosition = entity.getPosition().copy();

            final List<RoomItemFloor> itemsOnSq = this.getRoom().getItems().getItemsOnSquare(entity.getPositionToSet().getX(), entity.getPositionToSet().getY());
            final List<RoomItemFloor> itemsOnOldSq = this.getRoom().getItems().getItemsOnSquare(entity.getPosition().getX(), entity.getPosition().getY());

            final RoomTile oldTile = this.getRoom().getMapping().getTile(entity.getPosition().getX(), entity.getPosition().getY());
            final RoomTile newTile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

            if (oldTile != null) {
                entity.removeFromTile(oldTile);
            }

            if (newTile != null) {
                entity.addToTile(newTile);
            }

            entity.updateAndSetPosition(null);
            entity.setPosition(newPosition);

            PlayerEntity playerEntity = null;

            if (entity instanceof PlayerEntity) {
                playerEntity = (PlayerEntity) entity;
            }

            if (playerEntity != null && playerEntity.hasAttribute("tp")) {
                playerEntity.removeAttribute("tp");
            }

            if (entity instanceof BotEntity) {
                entity.getAI().onReachedTile(newTile);
            }

            if (entity instanceof PlayerEntity) {
                entity.onReachedTile(newTile);
            }

            // Step off
            for (final RoomItemFloor item : itemsOnOldSq) {
                if (!itemsOnSq.contains(item)) {
                    item.onEntityStepOff(entity);
                    this.getRoom().getItemProcess().queueAction(new WiredTriggerExecutor(WiredTriggerWalksOffFurni.class, entity, item));
                }
            }


            for (final RoomItemFloor item : itemsOnSq) {
                if (playerEntity != null) {
                    if (playerEntity.getPlayer() != null && playerEntity.getPlayer().getData().getQuestId() != 0 && playerEntity.getPlayer().getQuests() != null)
                        ((PlayerEntity) entity).getPlayer().getQuests().progressQuest(QuestType.EXPLORE_FIND_ITEM, item.getDefinition().getSpriteId());
                }
            }

            if (entity.getFollowingEntities().size() != 0) {
                entity.getFollowingEntities().forEach(e -> e.moveTo(oldPosition));
            }

            if (newTile != null && newTile.getTopItem() != 0 && !entity.isWarped()) {
                final RoomItemFloor topItem = this.getRoom().getItems().getFloorItem(newTile.getTopItem());

                if (topItem != null) {
                    final int itemEffectId = topItem.getDefinition().getEffectId();

                    if (!(topItem instanceof EffectFloorItem) && itemEffectId != 0 && entity.getMountedEntity() == null && (entity.getCurrentEffect() == null || entity.getCurrentEffect().getEffectId() != itemEffectId)) {
                        entity.applyEffect(new PlayerEffect(topItem.getDefinition().getEffectId(), true));
                    }

                    topItem.onEntityStepOn(entity);

                    if (entity instanceof PlayerEntity) {
                        this.getRoom().getItemProcess().queueAction(new WiredTriggerExecutor<>(WiredTriggerWalksOnFurni.class, entity, topItem));
                    }

                    if (entity instanceof BotEntity) {
                        WiredTriggerBotReachedFurni.executeTriggers(entity, topItem, entity.getUsername());
                    }
                }
            } else if (newTile != null) {
                newTile.onEntityEntersTile(entity);
            }
        }

        return false;
    }

    private boolean processEntity(RoomEntity entity) {
        return this.processEntity(entity, false);
    }

    private boolean processEntity(RoomEntity entity, boolean isRetry) {
        final boolean isPlayer = entity instanceof PlayerEntity;

        if (isPlayer && ((PlayerEntity) entity).getPlayer() == null || entity.getRoom() == null) {
            return true; // adds it to the to remove list automatically..
        }

        if (!isRetry) {
            if (isPlayer) {
                // Handle flood
                if (((PlayerEntity) entity).getPlayer().getRoomFloodTime() >= 0.5) {
                    ((PlayerEntity) entity).getPlayer().setRoomFloodTime(((PlayerEntity) entity).getPlayer().getRoomFloodTime() - 0.5);

                    if (((PlayerEntity) entity).getPlayer().getRoomFloodTime() < 0) {
                        ((PlayerEntity) entity).getPlayer().setRoomFloodTime(0);
                    }
                }

                if (((PlayerEntity) entity).isAway()) {
                    final long currentTime = Comet.getTime();

                    if ((currentTime - ((PlayerEntity) entity).getLastAwayReminder()) >= 60) {
                        this.getRoom().getEntities().broadcastChatMessage(new TalkMessageComposer(entity.getId(), String.format("Eu já estou fora por %s", TimeSpan.millisecondsToDate(((PlayerEntity) entity).getAwayTime())), ChatEmotion.NONE, 0), ((PlayerEntity) entity));
                        ((PlayerEntity) entity).setLastAwayReminder(currentTime);
                    }
                }
            }

            if (entity.handItemNeedsRemove() && entity.getHandItem() != 0) {
                entity.carryItem(0);
                entity.setHandItemTimer(0);
            }

            if (entity.isBodyRotating()) {
                int rotation = entity.getBodyRotation();
                if (rotation >= 8)
                    rotation = 0;

                rotation++;
                entity.setBodyRotation(rotation);
                entity.markNeedsUpdate();
            }

            if (entity.isHeadRotating()) {
                int rotation = entity.getHeadRotation();
                if (rotation >= 8)
                    rotation = 0;

                rotation++;
                entity.setHeadRotation(rotation);
                entity.markNeedsUpdate();
            }

            if (entity instanceof PlayerEntity) {
                if (((PlayerEntity) entity).isSpinBody()) {
                    ((PlayerEntity) entity).incrementSpinBodyRotation();

                    final int rotation = ((PlayerEntity) entity).getSpinBodyRotation();

                    entity.setHeadRotation(rotation);
                    entity.setBodyRotation(rotation);
                    entity.markNeedsUpdate(true);
                }
            }

            // Handle signs
            if (entity.hasStatus(RoomEntityStatus.SIGN) && !entity.isDisplayingSign()) {
                entity.removeStatus(RoomEntityStatus.SIGN);
                entity.markNeedsUpdate();
            }

            if (entity instanceof PlayerEntity && entity.isIdleAndIncrement() && entity.isVisible()) {

                if (entity.getIdleTime() >= 60 * CometSettings.roomIdleMinutes * 2) {
                    //if (this.getRoom().getData().getOwnerId() != playerEntity.getPlayerId() && !playerEntity.getPlayer().getPermissions().getRank().roomFullControl())
                    return false;
                }
            }
        }

        if (entity.findPath) {
            entity.getProcessingPath().clear();
            entity.findWalkPath(true, false);
            entity.findPath = false;
        }

        if (entity.hasStatus(RoomEntityStatus.MOVE)) {
            entity.removeStatus(RoomEntityStatus.MOVE);
            entity.removeStatus(RoomEntityStatus.GESTURE);

            entity.markNeedsUpdate();
        }

        if (entity.processingPath.size() > 0) {
            Square nextSq = entity.getProcessingPath().remove(0);
            entity.incrementPreviousSteps();

            if (isPlayer && ((PlayerEntity) entity).isKicked()) {

                if (((PlayerEntity) entity).getKickWalkStage() > 5) {
                    return true;
                }

                ((PlayerEntity) entity).increaseKickWalkStage();
            }


            boolean isLastStep = (entity.getProcessingPath().size() == 0);

            if ((nextSq == null || !entity.getRoom().getMapping().isValidEntityStep(entity, entity.getPosition(), new Position(nextSq.x, nextSq.y, 0.0), isLastStep)) && !entity.isOverriden()) {

                if (entity.getProcessingPath().isEmpty()) {
                    entity.findPath = false;
                    return false;
                }

                entity.findWalkPath(false, true);

                if (entity.getProcessingPath().isEmpty()) {
                    entity.findPath = false;
                    return false;
                }

                nextSq = entity.processingPath.remove(0);
            }


            if (nextSq == null)
                return false;

            final Position currentPos = entity.getPosition() != null ? entity.getPosition() : new Position(0, 0, 0);
            final Position nextPos = new Position(nextSq.x, nextSq.y);

            final double mountHeight = entity instanceof PlayerEntity && entity.getMountedEntity() != null ? 1.0 : 0;

            final RoomTile tile = this.room.getMapping().getTile(nextSq.x, nextSq.y);
            final double height = tile.getWalkHeight() + mountHeight;
            boolean isCancelled = entity.isWalkCancelled();
            boolean effectNeedsRemove = true;

            final List<RoomItemFloor> preItems = this.getRoom().getItems().getItemsOnSquare(nextSq.x, nextSq.y);

            for (final RoomItemFloor item : preItems) {
                if (item != null) {
                    if (!(item instanceof EffectFloorItem) && entity.getCurrentEffect() != null && entity.getCurrentEffect().getEffectId() == item.getDefinition().getEffectId()) {
                        if (item.getId() == tile.getTopItem()) {
                            effectNeedsRemove = false;
                        }
                    }

                    if (item.isMovementCancelled(entity, new Position(nextSq.x, nextSq.y))) {
                        entity.findPath = false;
                        entity.processingPath.clear();
                        return false;
                    }

                    if (!isCancelled)
                        item.onEntityPreStepOn(entity);

                }
            }
            if (effectNeedsRemove && entity.getCurrentEffect() != null && entity.getCurrentEffect().isItemEffect()) {
                entity.applyEffect(entity.getLastEffect());
            }


            if (this.getRoom().getEntities().positionHasEntity(nextPos)) {
                final boolean allowWalkthrough = this.getRoom().getData().getAllowWalkthrough();
                final boolean nextPosIsTheGoal = entity.getWalkingGoal().equals(nextPos);
                final boolean isOverriding = isPlayer && entity.isOverriden();
                if (!isOverriding && !allowWalkthrough) {
                    isCancelled = true;
                }

                final RoomEntity entityOnTile = this.getRoom().getMapping().getTile(nextPos.getX(), nextPos.getY()).getEntity();
                if (entityOnTile != null && entityOnTile.getMountedEntity() != null && entityOnTile.getMountedEntity() == entity) {
                    isCancelled = false;
                }

                if (entityOnTile instanceof PetEntity && entity instanceof PetEntity) {
                    if (entityOnTile.getTile().getTopItemInstance() instanceof BreedingBoxFloorItem) {
                        isCancelled = false;
                    }
                }
            }

            if (isCancelled) {
                if (entity.isWalkCancelled()) {
                    entity.processingPath.clear();
                    entity.findPath = false;
                    entity.setWalkCancelled(false);

                } else {
                    entity.processingPath.clear();
                    entity.findPath = true;
                }
                return false;
            }


            if (nextSq == null)
                return false;

            //  if (!isCancelled) {
            entity.setBodyRotation(Position.calculateRotation(currentPos.getX(), currentPos.getY(), nextSq.x, nextSq.y, entity.isMoonwalking()));
            entity.setHeadRotation(entity.getBodyRotation());

            entity.addStatus(RoomEntityStatus.MOVE, String.valueOf(nextSq.x).concat(",").concat(String.valueOf(nextSq.y)).concat(",").concat(String.valueOf(height)));

            entity.removeStatus(RoomEntityStatus.SIT);
            entity.removeStatus(RoomEntityStatus.LAY);

            final Position newPosition = new Position(nextSq.x, nextSq.y, height);

            entity.updateAndSetPosition(newPosition);
            entity.markNeedsUpdate();

            if (entity instanceof PlayerEntity && entity.getMountedEntity() != null) {
                final RoomEntity mountedEntity = entity.getMountedEntity();

                mountedEntity.moveTo(newPosition.getX(), newPosition.getY());
            }

            final List<RoomItemFloor> postItems = this.getRoom().getItems().getItemsOnSquare(nextSq.x, nextSq.y);

            for (final RoomItemFloor item : postItems) {
                if (item != null) {
                    item.onEntityPostStepOn(entity);
                }
            }

            entity.addToTile(tile);


            if (isLastStep)
                entity.findPath = false;

        } else {

            entity.findPath = false;

            if (isPlayer && ((PlayerEntity) entity).isKicked())
                return true;
        }

        // Handle expiring effects
        if (entity.getCurrentEffect() != null) {
            entity.getCurrentEffect().decrementDuration();

            if (entity.getCurrentEffect().getDuration() == 0 && entity.getCurrentEffect().expires()) {
                entity.applyEffect(entity.getLastEffect() != null ? entity.getLastEffect() : null);

                if (entity.getLastEffect() != null)
                    entity.setLastEffect(null);
            }
        }

        if (entity.isWalkCancelled()) {
            entity.setWalkCancelled(false);
        }


        return false;
    }

    public boolean isActive() {
        return this.active;
    }

    public Room getRoom() {
        return this.room;
    }

    public List<Long> getProcessTimes() {
        return processTimes;
    }

    public void setProcessTimes(List<Long> processTimes) {
        this.processTimes = processTimes;
    }

    protected boolean needsProcessing(RoomEntity entity) {
        return true;
    }
}