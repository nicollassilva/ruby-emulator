package com.cometproject.server.game.rooms.objects.entities.types.ai;

import com.cometproject.api.config.Configuration;
import com.cometproject.api.game.bots.BotMode;
import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.entities.types.ai.pets.PetAI;
import com.cometproject.server.game.rooms.objects.entities.types.ai.pets.PetMonsterPlantAI;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerBotReachedAvatar;
import com.cometproject.server.game.rooms.types.components.types.RoomMessageType;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.game.utilities.DistanceCalculator;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.utilities.RandomUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractBotAI implements BotAI {
    private static final ExecutorService botPathCalculator = Executors.newFixedThreadPool(Integer.parseInt(Configuration.currentConfig().get("comet.system.BotThreads")));
    protected PlayerEntity followingPlayer;
    private final RoomEntity entity;
    private long ticksUntilComplete = 0;
    private boolean walkNow = false;

    public AbstractBotAI(RoomEntity entity) {
        this.entity = entity;
    }

    @Override
    public void onTick() {
        if (this.ticksUntilComplete != 0) {
            this.ticksUntilComplete--;

            if (this.ticksUntilComplete == 0) {
                this.onTickComplete();
            }
        }

        final int chance = RandomUtil.getRandomInt(1, (this.getEntity().hasStatus(RoomEntityStatus.SIT) || this.getEntity().hasStatus(RoomEntityStatus.LAY)) ? 50 : 20);

        if (!this.getEntity().hasMount()) {
            boolean newStep = true;

            if (this.getEntity() instanceof BotEntity) {
                BotEntity botEntity = ((BotEntity) this.getEntity());

                if (botEntity.getData() == null) {
                    return;
                }

                if (botEntity.getData().getMode() == BotMode.RELAXED) {
                    newStep = false;
                }
            }

            if ((chance < 3 || this.walkNow) && newStep) {
                if (this.walkNow) {
                    this.walkNow = false;
                }

                if (!this.getEntity().isWalking() && this.canMove() && this.getEntity().canWalk() && this.followingPlayer == null && !(this instanceof PetMonsterPlantAI)) {
                    botPathCalculator.submit(() -> {
                        final RoomTile reachableTile = this.getEntity().getRoom().getMapping().getRandomReachableTile(this.getEntity());

                        if (reachableTile != null) {
                            this.getEntity().moveTo(reachableTile.getPosition().getX(), reachableTile.getPosition().getY());
                        }
                    });
                }
            }
        }

        if (this.getEntity() instanceof BotEntity) {
            try {
                final BotEntity botEntity = ((BotEntity) this.getEntity());

                if (botEntity.getCycleCount() == botEntity.getData().getChatDelay() * 2 && botEntity.getData().isAutomaticChat()) {
                    String message = botEntity.getData().getRandomMessage();

                    if (message != null && !message.isEmpty() && !this.getEntity().getRoom().hasAttribute("mutebots")) {
                        message = this.treatMessage(message);

                        botEntity.say(message);
                    }

                    botEntity.resetCycleCount();
                }

                botEntity.incrementCycleCount();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public String treatMessage(String message) {
        return message.replace("%roomname%", this.getEntity().getRoom().getData().getName())
                .replace("%botname%", this.getEntity().getUsername())
                .replace("%usersonline%", Integer.toString(Comet.getStats().getPlayers()))
                .replace("%roomowner%", this.getEntity().getRoom().getData().getOwner());
    }

    @Override
    public void onTickComplete() {

    }

    @Override
    public void onReachedTile(RoomTile tile) {
        final PlayerEntity closestEntity = this.entity.nearestPlayerEntity();

        if (closestEntity != null) {
            // calculate the distance.
            final int distance = DistanceCalculator.calculate(this.entity.getPosition(), closestEntity.getPosition());

            if (distance == 1) {
                WiredTriggerBotReachedAvatar.executeTriggers(closestEntity);
            }
        }
    }

    public void walkNow() {
        this.walkNow = true;
    }

    @Override
    public void setTicksUntilCompleteInSeconds(double seconds) {
        long realTime = Math.round(seconds * 1000 / 500);

        if (realTime < 1) {
            realTime = 1; //0.5s
        }

        this.ticksUntilComplete = realTime;
    }

    @Override
    public void say(String message) {
        this.say(message, ChatEmotion.NONE);
    }

    @Override
    public void say(String message, ChatEmotion emotion) {
        if (message == null) {
            return;
        }

        this.getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(this.getEntity().getId(), message, emotion, 0), false, this instanceof PetAI ? RoomMessageType.PET_CHAT : RoomMessageType.BOT_CHAT);
    }

    protected void moveTo(Position position) {
        this.getEntity().moveTo(position.getX(), position.getY());
    }

    public void sit() {
        this.getEntity().addStatus(RoomEntityStatus.SIT, "" + this.getPetEntity().getRoom().getModel().getSquareHeight()[this.getEntity().getPosition().getX()][this.getEntity().getPosition().getY()]);
        this.getEntity().markNeedsUpdate();
    }

    public void lay() {
        this.getEntity().addStatus(RoomEntityStatus.LAY, "" + this.getPetEntity().getRoom().getModel().getSquareHeight()[this.getEntity().getPosition().getX()][this.getEntity().getPosition().getY()]);
        this.getEntity().markNeedsUpdate();
    }

    @Override
    public boolean onTalk(PlayerEntity entity, String message) {
        return false;
    }

    @Override
    public boolean onPlayerLeave(PlayerEntity entity) {
        return false;
    }

    @Override
    public boolean onPlayerEnter(PlayerEntity entity) {
        return false;
    }

    @Override
    public boolean onAddedToRoom() {
        return false;
    }

    @Override
    public boolean onRemovedFromRoom() {
        if (this.followingPlayer != null) {
            this.followingPlayer.getFollowingEntities().remove(this.entity);
            this.followingPlayer = null;
        }

        return false;
    }

    @Override
    public boolean canMove() {
        return true;
    }

    public RoomEntity getEntity() {
        return entity;
    }

    public BotEntity getBotEntity() {
        return (BotEntity) entity;
    }

    public PetEntity getPetEntity() {
        return (PetEntity) entity;
    }
}
