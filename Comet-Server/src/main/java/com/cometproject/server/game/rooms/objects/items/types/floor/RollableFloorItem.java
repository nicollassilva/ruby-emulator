package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiPuckFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomEntityMovementNode;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.utilities.DistanceCalculator;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.utilities.Direction;

import java.util.HashMap;
import java.util.Map;

public abstract class RollableFloorItem extends RoomItemFloor {
    public static final int KICK_POWER = 6;
    private boolean isRolling;
    private RoomEntity kickerEntity;
    private boolean skipNext;
    private boolean wasDribbling;
    private int rollStage;
    private final boolean skip;

    public RollableFloorItem(final RoomItemData itemData, final Room room) {
        super(itemData, room);
        this.isRolling = false;
        this.skipNext = false;
        this.wasDribbling = false;
        this.rollStage = -1;
        this.skip = false;
    }

    public static void roll(final RoomItemFloor item, final Position from, final Position to, final Room room) {
        final Map<Integer, Double> items = new HashMap<>();
        items.put(item.getVirtualId(), item.getPosition().getZ());
        room.getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from.copy(), to.copy(), item.getVirtualId(), 0, items));
    }

    public static Position calculatePosition(final int x, final int y, final int playerRotation) {
        return Position.calculatePosition(x, y, playerRotation, false, 1);
    }

    @Override
    public void onEntityStepOn(final RoomEntity entity) {
        if (this.skipNext && this.kickerEntity != null && entity.getId() == this.kickerEntity.getId()) {
            this.kickerEntity = null;
            this.skipNext = false;
            return;
        }

        if (entity instanceof PlayerEntity && this instanceof BanzaiPuckFloorItem) {
            this.getItemData().setData(((PlayerEntity) entity).getGameTeam().getTeamId() + 1 + "");
            this.sendUpdate();
        }

        final boolean isOnBall = entity.getWalkingGoal().getX() == this.getPosition().getX() && entity.getWalkingGoal().getY() == this.getPosition().getY();
        this.setRotation(entity.getBodyRotation());

        if (isOnBall && !this.wasDribbling && this.getRoom().getGame().shootEnabled()) {
            if (entity instanceof PlayerEntity) {
                this.kickerEntity = entity;
            }

            this.wasDribbling = false;
            this.rollStage = 0;
            this.rollBall(entity.getPosition(), entity.getBodyRotation());
        } else if (isOnBall) {
            if (entity.getPreviousSteps() != 2) {
                this.rollSingle(entity);
            }

            this.wasDribbling = false;
        } else {
            this.rollSingle(entity);
            this.wasDribbling = true;
        }
    }

    @Override
    public void onEntityStepOff(final RoomEntity entity) {
        if (!this.skipNext) {
            this.rollBall(this.getPosition(), Direction.get(entity.getBodyRotation()).invert().num);
        } else {
            this.skipNext = false;
        }
    }

    private void rollBall(final Position from, final int rotation) {
        if (!DistanceCalculator.tilesTouching(this.getPosition().getX(), this.getPosition().getY(), from.getX(), from.getY())) {
            return;
        }

        this.setRotation(rotation);
        this.isRolling = true;
        this.rollStage = 0;
        this.onTickComplete();
    }

    public void onTickComplete() {
        if (!this.isRolling || this.rollStage == -1 || this.rollStage >= KICK_POWER) {
            this.isRolling = false;
            this.rollStage = -1;
            this.skipNext = false;
            this.wasDribbling = false;
            return;
        }

        ++this.rollStage;
        final boolean isStart = this.rollStage == 1;

        if (isStart) {
            int tiles = 1;
            Position position = this.getNextPosition();

            if (!this.isValidRoll(position)) {
                position = this.getNextPosition(position.getFlag(), position.squareBehind(position.getFlag()));
            }

            for (int count = isStart ? 2 : 1, i = 0; i < count && this.rollStage + i < KICK_POWER; ++i) {
                final Position nextPosition = this.getNextPosition(position.getFlag(), position.squareInFront(position.getFlag()));
                if (!this.isValidRoll(nextPosition)) {
                    break;
                }
                if (nextPosition.getFlag() != this.getRotation()) {
                    break;
                }
                if (this.rollStage + i > KICK_POWER) {
                    break;
                }
                tiles = i;
                position = nextPosition;
            }

            if (position.getFlag() == -1) {
                position.setFlag(this.kickerEntity.getBodyRotation());
            }

            final double distanceMoved = position.distanceTo(this.getPosition());
            this.rollStage += (int) distanceMoved;
            this.getItemData().setData("55");
            this.sendUpdate();
            this.moveTo(position, position.getFlag());
            this.setTicks(RoomItemFactory.getProcessTime(tiles * 0.5));
        } else {
            final Position nextPosition2 = this.getNextPosition();
            Position newPosition;

            if (this.isValidRoll(nextPosition2)) {
                newPosition = nextPosition2;
            } else {
                newPosition = this.getNextPosition();
            }

            if (!this.isValidRoll(newPosition)) {
                return;
            }

            if (newPosition.getFlag() == -1) {
                newPosition.setFlag(this.kickerEntity.getBodyRotation());
            }

            this.getItemData().setData((6 - (this.rollStage - 1) == 0) ? 3 : ((6 - (this.rollStage - 1)) * 11));
            this.sendUpdate();
            this.moveTo(newPosition, newPosition.getFlag());
            this.setTicks(RoomItemFactory.getProcessTime(this.getDelay(this.rollStage)));
        }
    }

    private boolean isValidRoll(final int x, final int y) {
        return false;
    }

    private boolean isValidRoll(final Position position) {
        final RoomTile tile = this.getRoom().getMapping().getTile(position.getX(), position.getY());
        return tile != null && tile.canPlaceItemHere() && tile.getMovementNode() == RoomEntityMovementNode.OPEN && tile.getState() == RoomTileState.VALID && tile.getEntities().size() == 0;
    }

    public Position getNextPosition() {
        return this.getNextPosition(this.getRotation(), this.getPosition().squareInFront(this.getRotation()));
    }

    private Position getNextPosition(int rotation, Position position) {
        if (!this.isValidRoll(position)) {
            rotation = Position.getInvertedRotation(rotation);
            position = this.getPosition().squareInFront(rotation);
            if (!this.isValidRoll(position)) {
                position = this.getPosition();
                switch (rotation) {
                    case 0: {
                        rotation = 4;
                        break;
                    }
                    case 1: {
                        rotation = 3;
                        if (!this.isValidRoll(position.squareInFront(rotation))) {
                            rotation = 5;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        rotation = 6;
                        break;
                    }
                    case 3: {
                        rotation = 5;
                        if (this.isValidRoll(position.squareInFront(rotation))) {
                            break;
                        }
                        rotation = 1;
                        if (!this.isValidRoll(position.squareInFront(rotation))) {
                            rotation = 1;
                            break;
                        }
                        break;
                    }
                    case 4: {
                        rotation = 0;
                        break;
                    }
                    case 5: {
                        rotation = 7;
                        if (!this.isValidRoll(position.squareInFront(rotation))) {
                            rotation = 3;
                            break;
                        }
                        break;
                    }
                    case 6: {
                        rotation = 2;
                        break;
                    }
                    case 7: {
                        rotation = 5;
                        if (!this.isValidRoll(position.squareInFront(rotation))) {
                            rotation = 3;
                            break;
                        }
                        break;
                    }
                }
                position = position.squareInFront(rotation);
            }
        }
        position.setFlag(rotation);
        return position;
    }

    private void rollSingle(final RoomEntity entity) {
        if (this.isRolling || !entity.getPosition().touching(this.getPosition())) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            this.kickerEntity = entity;
        }
        this.isRolling = true;
        entity.moveTo(this.getPosition());
        Position newPosition;
        if (this.isValidRoll(this.getNextPosition())) {
            newPosition = calculatePosition(this.getPosition().getX(), this.getPosition().getY(), entity.getBodyRotation());
        } else {
            newPosition = Position.calculatePosition(this.getPosition().getX(), this.getPosition().getY(), entity.getBodyRotation(), true, 1);
            this.setRotation(Direction.get(this.getRotation()).invert().num);
        }
        if (!this.isValidRoll(newPosition)) {
            return;
        }
        this.getItemData().setData("11");
        this.moveTo(newPosition, entity.getBodyRotation());
        this.isRolling = false;
        this.wasDribbling = false;
    }

    @Override
    public boolean onInteract(final RoomEntity entity, final int requestData, final boolean isWiredTriggered) {
        if (isWiredTriggered) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            this.kickerEntity = entity;
        }
        this.skipNext = true;
        this.rollSingle(entity);
        return true;
    }

    @Override
    public void onPositionChanged(final Position newPosition) {
        this.isRolling = false;
        this.kickerEntity = null;
        this.skipNext = false;
        this.rollStage = -1;
        this.wasDribbling = false;
    }

    private void moveTo(final Position pos, final int rotation) {
        final RoomTile newTile = this.getRoom().getMapping().getTile(pos);
        if (newTile == null) {
            return;
        }
        pos.setZ(newTile.getStackHeight());
        roll(this, this.getPosition().copy(), pos.copy(), this.getRoom());
        final RoomTile tile = this.getRoom().getMapping().getTile(this.getPosition());
        this.setRotation(rotation);
        this.getPosition().setX(pos.getX());
        this.getPosition().setY(pos.getY());
        if (tile != null) {
            tile.reload();
        }
        newTile.reload();
        for (final RoomItemFloor floorItem : this.getRoom().getItems().getItemsOnSquare(pos.getX(), pos.getY())) {
            floorItem.onItemAddedToStack(this);
        }
        this.getPosition().setZ(pos.getZ());
        this.getRoom().getItemProcess().saveItem(this);
    }

    private double getDelay(final int i) {
        if (i == 5) {
            return 0.5;
        }
        if (i == 6) {
            return 1.0;
        }
        return 0.5;
    }

    public RoomEntity getPusher() {
        return this.kickerEntity;
    }
}