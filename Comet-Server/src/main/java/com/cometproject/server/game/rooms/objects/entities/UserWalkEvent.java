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

public class UserWalkEvent {
    public int Ticks;
    public int eventId;

    private static final int MAX_STEEPS = 25;
    private final RoomEntity entity;

    public boolean isWalking;

    public UserWalkEvent(RoomEntity entity) {
        this.entity = entity;

    }


    public void run(Room room) {
        if (!this.isWalking) {
            this.entity.processingPath.clear();
            stopWalk(room);
            //   room.entityWalk(this.nextXY, this.entity, false);
            return;
        }

        if (this.entity.getWalkingGoal().equals(this.entity.getPosition()))
        {
            stopWalk(room);
            return;
        }
        final boolean isPlayer = entity instanceof PlayerEntity;

        if (entity.findPath) {
            entity.findPath = false;
            entity.findWalkPath(true);
        }


        if (entity.isWalking() && entity.processingPath.size() > 0) {
            Square nextSq = entity.getProcessingPath().remove(0);
            entity.incrementPreviousSteps();


            boolean isLastStep = (entity.getProcessingPath().size() == 0);

            if ((nextSq == null || !entity.getRoom().getMapping().isValidEntityStep(entity, entity.getPosition(), new Position(nextSq.x, nextSq.y, 0.0), isLastStep)) && !entity.isOverriden()) {

                if (entity.getProcessingPath().isEmpty()) {
                    stopWalk(room);
                    return;
                }

                entity.findWalkPath(false);

                if (entity.getProcessingPath().isEmpty()) {
                    stopWalk(room);
                    return;
                }

                nextSq = entity.processingPath.remove(0);
            }


            if (nextSq == null) {
                stopWalk(room);
                return;
            }

            final Position currentPos = entity.getPosition() != null ? entity.getPosition() : new Position(0, 0, 0);
            final Position nextPos = new Position(nextSq.x, nextSq.y);

            final double mountHeight = entity instanceof PlayerEntity && entity.getMountedEntity() != null ? 1.0 : 0;

            final RoomTile tile = room.getMapping().getTile(nextSq.x, nextSq.y);
            final double height = tile.getWalkHeight() + mountHeight;
            boolean isCancelled = entity.isWalkCancelled();
            boolean effectNeedsRemove = true;

            final List<RoomItemFloor> preItems = room.getItems().getItemsOnSquare(nextSq.x, nextSq.y);

            for (final RoomItemFloor item : preItems) {
                if (item != null) {
                    if (!(item instanceof EffectFloorItem) && entity.getCurrentEffect() != null && entity.getCurrentEffect().getEffectId() == item.getDefinition().getEffectId()) {
                        if (item.getId() == tile.getTopItem()) {
                            effectNeedsRemove = false;
                        }
                    }

                    if (item.isMovementCancelled(entity, new Position(nextSq.x, nextSq.y))) {
                        isCancelled = true;
                    }

                    if (!isCancelled)
                        item.onEntityPreStepOn(entity);

                }
            }
            if (effectNeedsRemove && entity.getCurrentEffect() != null && entity.getCurrentEffect().isItemEffect()) {
                entity.applyEffect(entity.getLastEffect());
            }


            if (room.getEntities().positionHasEntity(nextPos)) {
                final boolean allowWalkthrough = room.getData().getAllowWalkthrough();
                final boolean nextPosIsTheGoal = entity.getWalkingGoal().equals(nextPos);
                final boolean isOverriding = isPlayer && entity.isOverriden();
                if (!isOverriding && (!allowWalkthrough && nextPosIsTheGoal)) {
                    isCancelled = true;
                }

                final RoomEntity entityOnTile = room.getMapping().getTile(nextPos.getX(), nextPos.getY()).getEntity();
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
                if (entity.isWalkCancelled())
                {
                    stopWalk(room);
                    return;
                }

                entity.findWalkPath(false);

                if (entity.getProcessingPath().isEmpty()) {
                    stopWalk(room);
                    return;
                }

                nextSq = entity.processingPath.remove(0);
            }

            if (nextSq == null) {
                stopWalk(room);
                return;
            }

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

            final List<RoomItemFloor> postItems = room.getItems().getItemsOnSquare(nextSq.x, nextSq.y);

            for (final RoomItemFloor item : postItems) {
                if (item != null) {
                    item.onEntityPostStepOn(entity);
                }
            }

            entity.addToTile(tile);


            if (isLastStep)
                entity.walking = false;

        } else {
            stopWalk(room);

            if (isPlayer && ((PlayerEntity) entity).isKicked())
                return;
        }


        this.Ticks = 0;

    }
    public void stopWalk(Room room) {
        this.isWalking = false;

        entity.findPath = false;
        this.entity.walking = false;
        this.entity.processingPath.clear();

        this.entity.removeStatus(RoomEntityStatus.MOVE);
        this.entity.removeStatus(RoomEntityStatus.GESTURE);

        this.entity.markNeedsUpdate();
    }


    public void walk(Room room, int x, int y) {

        entity.findPath = true;
        if (!this.isWalking) {
            this.isWalking = true;
            room.addUserEvent(this, 0);
        }
    }
    public void walk(Room room) {
        entity.findPath = false;

        if (!this.isWalking) {
            this.isWalking = true;
            room.addUserEvent(this, 0);
        }
    }
}
