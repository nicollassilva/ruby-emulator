package com.cometproject.server.game.rooms.objects.entities;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.EffectFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.pet.breeding.BreedingBoxFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;

import java.util.List;
import java.util.Set;

public class UserWalkEvent {
    public int Ticks;
    public Integer eventId;

    private static final int MAX_STEEPS = 25;
    private final RoomEntity liveEntity;
    private int walkX;
    private int walkY;
    private int nextX;
    private int nextY;
    private int nextXY;
    private double nextZ;
    private boolean doSteep;
    private boolean findPath;
    public boolean isWalking;

    public UserWalkEvent(RoomEntity entity) {
        liveEntity = entity;

    }


    public void run(Room room) {
        if (!this.isWalking) {
            this.liveEntity.processingPath.clear();
            //   room.entityWalk(this.nextXY, this.liveEntity, false);
            return;
        }
        if (this.doSteep) {
            this.doSteep = false;
            if (!doWalkSteep(room)) {
                this.liveEntity.processingPath.clear();
                stopWalk(room);
                return;
            }
        }


        if (this.findPath) {
            this.findPath = false;
            this.liveEntity.processingPath.clear();
            this.liveEntity.findWalkPath();
        }


        if ((this.liveEntity.processingPath == null) || (this.liveEntity.processingPath.isEmpty())) {
            stopWalk(room);
            return;
        }


        final boolean isPlayer = this.liveEntity instanceof PlayerEntity;

        Square nextSq = this.liveEntity.getProcessingPath().remove(0);
        this.liveEntity.incrementPreviousSteps();

        boolean isLastStep = (this.liveEntity.getProcessingPath().size() == 0);

        if ((nextSq == null ||
                !this.liveEntity.getRoom().getMapping().isValidEntityStep(this.liveEntity, this.liveEntity.getPosition(), new Position(nextSq.x, nextSq.y, 0.0), isLastStep)) && !this.liveEntity.isOverriden()) {

            if (this.liveEntity.getProcessingPath().isEmpty()) {
                stopWalk(room);
                return;
            }

            this.liveEntity.findWalkPath();

            if (this.liveEntity.getProcessingPath().isEmpty()) {
                stopWalk(room);
                return;
            }

            nextSq = this.liveEntity.processingPath.remove(0);

        }

        if (nextSq == null) {
            return;
        }

        var nextPos = new Position(nextSq.x, nextSq.y, nextSq.height);

        boolean isCancelled = this.liveEntity.isWalkCancelled();

        if (room.getEntities().positionHasEntity(nextPos)) {

            final boolean allowWalkthrough = room.getData().getAllowWalkthrough();
            final boolean nextPosIsTheGoal = this.liveEntity.getWalkingGoal().equals(nextPos);
            final boolean isOverriding = this.liveEntity instanceof PlayerEntity && this.liveEntity.isOverriden();
            if (!isOverriding && (!allowWalkthrough && nextPosIsTheGoal)) {
                isCancelled = true;
            }

            final RoomEntity entityOnTile = room.getMapping().getTile(nextPos.getX(), nextPos.getY()).getEntity();
            if (entityOnTile != null && entityOnTile.getMountedEntity() != null && entityOnTile.getMountedEntity() == this.liveEntity) {
                isCancelled = false;
            }

            if (entityOnTile instanceof PetEntity && this.liveEntity instanceof PetEntity) {
                if (entityOnTile.getTile().getTopItemInstance() instanceof BreedingBoxFloorItem) {
                    isCancelled = false;
                }
            }


        }

        if (isCancelled) {

            this.liveEntity.findWalkPath();

            if (this.liveEntity.getProcessingPath().isEmpty()) {
                stopWalk(room);
                return;
            }

            nextSq = this.liveEntity.processingPath.remove(0);
        }

        if (nextSq == null) {
            return;
        }

        final Position currentPos = this.liveEntity.getPosition() != null ? this.liveEntity.getPosition() : new Position(0, 0, 0);

        final double mountHeight = this.liveEntity instanceof PlayerEntity && this.liveEntity.getMountedEntity() != null ? 1.0 : 0;

        final RoomTile tile = room.getMapping().getTile(nextSq.x, nextSq.y);
        final double height = tile.getWalkHeight() + mountHeight;
        boolean effectNeedsRemove = true;


        this.nextZ = height;
        this.nextX = nextSq.x;
        this.nextY = nextSq.y;

        final List<RoomItemFloor> preItems = room.getItems().getItemsOnSquare(nextSq.x, nextSq.y);

        for (final RoomItemFloor item : preItems) {
            if (item != null) {
                if (!(item instanceof EffectFloorItem) && this.liveEntity.getCurrentEffect() != null && this.liveEntity.getCurrentEffect().getEffectId() == item.getDefinition().getEffectId()) {
                    if (item.getId() == tile.getTopItem()) {
                        effectNeedsRemove = false;
                    }
                }

                if (item.isMovementCancelled(this.liveEntity, new Position(nextSq.x, nextSq.y))) {
                    isCancelled = true;
                }

                if (!isCancelled)
                    item.onEntityPreStepOn(this.liveEntity);

            }
        }



        this.liveEntity.setBodyRotation(Position.calculateRotation(currentPos.getX(), currentPos.getY(), nextX, nextY, this.liveEntity.isMoonwalking()));
        this.liveEntity.setHeadRotation(this.liveEntity.getBodyRotation());

        this.liveEntity.addStatus(RoomEntityStatus.MOVE, String.valueOf(nextX).concat(",").concat(String.valueOf(nextY)).concat(",").concat(String.valueOf(nextZ)));

        this.liveEntity.removeStatus(RoomEntityStatus.SIT);
        this.liveEntity.removeStatus(RoomEntityStatus.LAY);

        final Position newPosition = new Position(nextX, nextY, nextZ);

        this.liveEntity.updateAndSetPosition(newPosition);
        this.liveEntity.markNeedsUpdate();


        if (this.liveEntity instanceof PlayerEntity && this.liveEntity.getMountedEntity() != null) {
            final RoomEntity mountedEntity = this.liveEntity.getMountedEntity();

            mountedEntity.moveTo(newPosition.getX(), newPosition.getY());
        }

        final List<RoomItemFloor> postItems = room.getItems().getItemsOnSquare(nextX, nextY);

        for (final RoomItemFloor item : postItems) {
            if (item != null) {
                item.onEntityPostStepOn(this.liveEntity);
            }
        }

        this.liveEntity.addToTile(tile);

        if (effectNeedsRemove && this.liveEntity.getCurrentEffect() != null && this.liveEntity.getCurrentEffect().isItemEffect()) {
            this.liveEntity.applyEffect(this.liveEntity.getLastEffect());
        }


        this.doSteep = true;


        this.Ticks = 0;

    }

    public boolean doWalkSteep(Room room) {
        boolean isCancelled = this.liveEntity.isWalkCancelled();

        final Position nextPos = new Position(nextX, nextY);
        final Position currentPos = this.liveEntity.getPosition() != null ? this.liveEntity.getPosition() : new Position(0, 0, 0);

        final List<RoomItemFloor> preItems = room.getItems().getItemsOnSquare(nextX, nextY);

        final RoomTile tile = room.getMapping().getTile(nextX, nextY);
        boolean effectNeedsRemove = true;

        for (final RoomItemFloor item : preItems) {
            if (item != null) {
                if (!(item instanceof EffectFloorItem) && this.liveEntity.getCurrentEffect() != null && this.liveEntity.getCurrentEffect().getEffectId() == item.getDefinition().getEffectId()) {
                    if (item.getId() == tile.getTopItem()) {
                        effectNeedsRemove = false;
                    }
                }

                if (item.isMovementCancelled(this.liveEntity, new Position(nextX, nextY))) {
                    isCancelled = true;
                }

                if (!isCancelled)
                    item.onEntityPreStepOn(this.liveEntity);

            }
        }
        if (effectNeedsRemove && this.liveEntity.getCurrentEffect() != null && this.liveEntity.getCurrentEffect().isItemEffect()) {
            this.liveEntity.applyEffect(this.liveEntity.getLastEffect());
        }


        return this.isWalking;
    }


    private void stopWalk(Room room) {

        this.liveEntity.walking = false;
        this.isWalking = false;
        this.liveEntity.processingPath.clear();

        //  room.entityWalk(this.nextXY, this.liveEntity, false);
        // room.entityWalk(this.liveEntity.xy, this.liveEntity, true);
        if (this.liveEntity.hasStatus(RoomEntityStatus.MOVE)) {
            this.liveEntity.removeStatus(RoomEntityStatus.MOVE);
            this.liveEntity.removeStatus(RoomEntityStatus.GESTURE);

            this.liveEntity.markNeedsUpdate();
        }
    }


    public void walk(Room room, int x, int y) {
        this.walkX = x;
        this.walkY = y;

        this.findPath = true;
        if (!this.isWalking) {
            this.isWalking = true;
            room.addUserEvent(this, 0);
        }
    }
}
