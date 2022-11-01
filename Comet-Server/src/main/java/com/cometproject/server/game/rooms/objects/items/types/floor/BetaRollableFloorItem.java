package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomEntityMovementNode;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.utilities.DistanceCalculator;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.utilities.Direction;

import java.util.HashMap;
import java.util.Map;

public abstract class BetaRollableFloorItem extends RoomItemFloor {
    public static final int KICK_POWER = 5;

    private boolean isRolling = false;
    private boolean takeable = true;
    private boolean playerCollidable = false;
    private RoomEntity kickerEntity;
    private boolean skipNext = false;
    private boolean wasDribbling = false;
    private int rollStage = -1;

    private boolean skip = false;

    private int animationMode = -1;

    public BetaRollableFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    public static void roll(RoomItemFloor item, Position from, Position to, Room room) {
        final BetaRollableFloorItem rollableFloorItem = (BetaRollableFloorItem) item;

        final Map<Integer, Double> items = new HashMap<>();

        items.put(item.getVirtualId(), item.getPosition().getZ());
        room.getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from.copy(), to.copy(), item.getVirtualId(), 0, items));

    }

    public static Position calculatePosition(int x, int y, int playerRotation) {
        return Position.calculatePosition(x, y, playerRotation, false, 1);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (this.skipNext && (this.kickerEntity != null && entity.getId() == this.kickerEntity.getId())) {
            this.kickerEntity = null;
            this.skipNext = false;
            return;
        }


        boolean isOnBall = entity.getWalkingGoal().getX() == this.getPosition().getX() && entity.getWalkingGoal().getY() == this.getPosition().getY();

        this.setRotation(entity.getBodyRotation());

        if (isOnBall && !this.wasDribbling && this.takeable) {
            if (entity instanceof PlayerEntity) {
                this.kickerEntity = entity;
            }

            this.wasDribbling = false;
            this.playerCollidable = false;
            this.rollStage = 0;
            this.animationMode = 6;
            //this.rollBall(entity.getPosition(), entity.getBodyRotation());
            this.rollSingle(entity);
        } else if (isOnBall && this.takeable) {
            if (entity.getPreviousSteps() != 2) {
                this.rollSingle(entity);
            }

            this.wasDribbling = false;
            this.playerCollidable = false;
        } else if (this.takeable) {
            this.rollSingle(entity);
            this.wasDribbling = true;
            this.playerCollidable = false;
        }
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!this.skipNext) {
            this.rollBall(this.getPosition(), Direction.get(entity.getBodyRotation()).invert().num);
        } else {
            this.skipNext = false;
        }
    }

    private void rollBall(Position from, int rotation) {
        if (!DistanceCalculator.tilesTouching(this.getPosition().getX(), this.getPosition().getY(), from.getX(), from.getY())) {
            return;
        }

        this.rotation = rotation;
        this.isRolling = true;
        this.rollStage = 0;

        this.onTickComplete();
    }

    @Override
    public void onTickComplete() {
        if (!this.isRolling || this.rollStage == -1 || this.rollStage >= KICK_POWER) {
            this.isRolling = false;
            this.rollStage = -1;
            this.skipNext = false;
            this.wasDribbling = false;
            this.takeable = true;
            this.playerCollidable = false;
            return;
        }

        // System.out.println(rollStage);
        boolean isStart = this.rollStage == 1;
        boolean isLast = this.rollStage >= KICK_POWER;
//
//        int maxSteps = isStart ? 3 : 2;
//        int stepsTaken = 1;
//
//        Position position = this.getNextPosition();
//
//        for (int i = this.rollStage; i < KICK_POWER; i++) {
//
//
//        }


        this.rollStage++;
        this.takeable = this.rollStage != 2 && this.rollStage != 3;
        this.playerCollidable = this.rollStage != 1 && this.rollStage != 2 && this.rollStage != 3;
        System.out.println(this.takeable);
        // the first roll... let's do some magic.

        int tiles = 1;
        Position position = this.getNextPosition();

        if (!this.isValidRoll(position)) {
            position = this.getNextPosition(position.getFlag(), position.squareBehind(position.getFlag()));
        }

        int count = isStart ? 2 : 1;

        // can we skip some tiles?
        Position nextPosition = this.getNextPosition(position.getFlag(), position.squareInFront(position.getFlag()));
        if (this.rollStage > KICK_POWER) {
            // we hit a snag
            return;
        }

        if (position.getFlag() == -1) {
            position.setFlag(kickerEntity.getBodyRotation());
        }


        this.getItemData().setData("55");
        this.sendUpdate();

        if (this.rollStage != 1 || this.rollStage != 2 || this.rollStage != 3) {
            this.moveTo(position, position.getFlag());
        }
        // System.out.println(tiles);
        this.setTicks(RoomItemFactory.getProcessTime(this.getDelay(this.rollStage)));

    }

    private boolean isValidRoll(int x, int y) {
        return false;
    }

    private boolean isValidRoll(Position position) {
        RoomTile tile = this.getRoom().getMapping().getTile(position.getX(), position.getY());

        if (tile != null) {
            if (tile.canPlaceItemHere() && tile.getMovementNode() == RoomEntityMovementNode.OPEN && tile.getState() == RoomTileState.VALID) {
                if (this.playerCollidable) {
                    if (tile.getEntities().size() != 0) {

                        // Vérif si le joueur est immobile

                        for (RoomEntity entity : tile.getEntities()) {
                            if (entity instanceof PlayerEntity) {
                                PlayerEntity playerEntity = (PlayerEntity) entity;

                                if (playerEntity.getProcessingPath().isEmpty()) {
                                    this.rollStage = 6;
                                    return false;
                                }


                            }
                        }
                        return true;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public Position getNextPosition() {
        return this.getNextPosition(this.getRotation(), this.getPosition().squareInFront(this.getRotation()));
    }

    private Position getNextPosition(int rotation, Position position) {

        if (!this.isValidRoll(position)) {
            //this.rollStage--;
            //rotation = Position.getInvertedRotation(rotation);
            //position = this.getPosition().squareInFront(rotation);

            //System.out.println("invrota: " + rotation);

            //if (!this.isValidRoll(position)) {
            // reset the position back the original
            position = this.getPosition();

            switch (rotation) {
                case Position.NORTH:

                    rotation = Position.SOUTH;
                    break;

                case Position.NORTH_EAST:
                    rotation = Position.SOUTH_EAST;


                    if (!this.isValidRoll(position.squareInFront(rotation))) {
                        rotation = Position.NORTH_WEST;

                    }

                    break;

                case Position.EAST:

                    rotation = Position.WEST;
                    break;

                case Position.SOUTH_EAST:
                    rotation = Position.NORTH_EAST;


                    if (!this.isValidRoll(position.squareInFront(rotation))) {
                        rotation = Position.SOUTH_WEST;


                    }
                    break;

                case Position.SOUTH:
                    rotation = Position.NORTH;

                    break;

                case Position.SOUTH_WEST:
                    rotation = Position.NORTH_WEST;

                    if (!this.isValidRoll(position.squareInFront(rotation))) {
                        rotation = Position.SOUTH_EAST;
                    }
                    break;

                case Position.WEST:
                    rotation = Position.EAST;

                    break;

                case Position.NORTH_WEST:
                    rotation = Position.SOUTH_WEST;


                    if (!this.isValidRoll(position.squareInFront(rotation))) {
                        rotation = Position.NORTH_EAST;
                    }
                    break;
            }

            position = position.squareInFront(rotation);
            //}
        }

        position.setFlag(rotation);
        return position;
    }

    private void rollSingle(RoomEntity entity) {
        if (this.isRolling || !entity.getPosition().touching(this.getPosition())) {
            return;
        }

        if (entity instanceof PlayerEntity) {
            this.kickerEntity = entity;

//            if (kickerEntity.getBodyRotation() % 2 != 0) {
//                return false;
//            }
        }

        this.isRolling = true;

        Position newPosition;
        entity.moveTo(this.getPosition());

        //System.out.println("GoalX: " + entity.getProcessingPath().get(entity.getProcessingPath().size() - 1).x);
        //System.out.println("GoalY: " + entity.getProcessingPath().get(entity.getProcessingPath().size() - 1).y);

        if (entity.getProcessingPath().isEmpty()) {
            this.rollBall(entity.getPosition(), entity.getBodyRotation());

            return;
        }
        //if((this.getPosition().getX() - 1) == entity.getProcessingPath().get(entity.getProcessingPath().size() - 1).x && this.getPosition().getY() == entity.getProcessingPath().get(entity.getProcessingPath().size() - 1).y && this.wasDribbling) {

        //}
        Position position = this.getNextPosition();

        if (this.isValidRoll(this.getNextPosition())) {
            newPosition = calculatePosition(this.getPosition().getX(), this.getPosition().getY(), entity.getBodyRotation());
        } else {
            newPosition = Position.calculatePosition(this.getPosition().getX(), this.getPosition().getY(), entity.getBodyRotation(), true, 1);
            this.setRotation(Direction.get(this.getRotation()).invert().num);
        }

        //if (!this.isValidRoll(newPosition)) {
        //    return;
        ///}

        this.getItemData().setData("11");
        this.moveTo(position, entity.getBodyRotation());
        this.isRolling = false;
        this.wasDribbling = false;
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTriggered) {
        if (isWiredTriggered) return false;

        if (entity instanceof PlayerEntity) {
            this.kickerEntity = entity;
        }

        this.skipNext = true;
        this.rollBall(entity.getPosition(), entity.getBodyRotation());
        return true;
    }

    @Override
    public void onPositionChanged(Position newPosition) {
        this.isRolling = false;
        //this.kickerEntity = null;
        this.skipNext = false;
        this.rollStage = -1;
        this.wasDribbling = false;
    }

    private void moveTo(Position pos, int rotation) {
        RoomTile newTile = this.getRoom().getMapping().getTile(pos);

        if (newTile == null) {
            return;
        }

        pos.setZ(newTile.getStackHeight());

        roll(this, this.getPosition().copy(), pos.copy(), this.getRoom());

        RoomTile tile = this.getRoom().getMapping().getTile(this.getPosition());

        this.setRotation(rotation);

        this.getPosition().setX(pos.getX());
        this.getPosition().setY(pos.getY());

        if (tile != null) {
            tile.reload();
        }

        newTile.reload();

        // tell all other items on the new square that there's a new item. (good method of updating score...)
        for (RoomItemFloor floorItem : this.getRoom().getItems().getItemsOnSquare(pos.getX(), pos.getY())) {
            floorItem.onItemAddedToStack(this);
        }

        this.getPosition().setZ(pos.getZ());
        this.save();
    }

    private double getDelay(int i) {
        if (i == 4) {
            return 0.1;
        } else if (i == 5) {
            return 0.5;
        }

//        System.out.println(i);
        return 0;
    }

    public RoomEntity getPusher() {
        return kickerEntity;
    }
}