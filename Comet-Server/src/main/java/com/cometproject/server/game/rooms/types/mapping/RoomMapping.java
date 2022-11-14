package com.cometproject.server.game.rooms.types.mapping;

import com.cometproject.api.game.rooms.RoomDiagonalType;
import com.cometproject.api.game.rooms.models.IRoomModel;
import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.RoomFloorObject;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.OneWayGateFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.pet.breeding.BreedingBoxFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.utilities.RandomUtil;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class RoomMapping {
    private final Room room;

    private RoomTile[][] tiles;


    public RoomMapping(Room roomInstance) {
        this.room = roomInstance;
    }

    public void init() {
        if (this.getModel() == null) {
            return;
        }

        int sizeX = this.getModel().getSizeX();
        int sizeY = this.getModel().getSizeY();

        this.tiles = new RoomTile[sizeX][sizeY];

        //System.out.print("\n");
        for (int x = 0; x < sizeX; x++) {
            final RoomTile[] xArray = new RoomTile[sizeY];

            for (int y = 0; y < sizeY; y++) {
                final RoomTile instance = new RoomTile(this, new Position(x, y, 0d));
                instance.reload();

                xArray[y] = instance;

                //System.out.printf("[%s, %s] ", x, y);
            }

            this.tiles[x] = xArray;
            //System.out.print("\n");
        }
    }

    public void dispose() {
        for (RoomTile[] roomTiles : tiles) {
            for (final RoomTile tile : roomTiles) {
                if (tile != null) {
                    tile.dispose();
                }
            }
        }
    }

    public void tick() {
        for (final RoomTile[] roomTiles : tiles) {
            for (final RoomTile roomTile : roomTiles) {
                final List<RoomEntity> entitiesToRemove = new ArrayList<>();

                try {
                    for (final RoomEntity entity : roomTile.getEntities()) {
                        if (entity instanceof PlayerEntity) {
                            if (((PlayerEntity) entity).getPlayer() == null) {
                                entitiesToRemove.add(entity);
                            } else if (!((PlayerEntity) entity).getPlayer().getEntity().getPosition().equals(roomTile.getPosition()) && !((PlayerEntity) entity).getPlayer().getEntity().getPositionToSet().copy().equals(roomTile.getPosition())) {
                                entitiesToRemove.add(entity);

                                if (entity.getTile() != null && this.getRoom().getMapping().isValidPosition(entity.getTile().getPosition())) {
                                    entity.getTile().getEntities().add(entity);
                                }
                            }
                        }
                    }

                    for (final RoomEntity entityToRemove : entitiesToRemove) {
                        roomTile.getEntities().remove(entityToRemove);
                    }
                } catch (Exception ignored) {

                }

                entitiesToRemove.clear();
            }
        }
    }

    public void updateTile(int x, int y) {
        if (x < 0 || y < 0) {
            return;
        }

        if (this.tiles.length > x) {
            if (tiles[x].length > y)
                this.tiles[x][y].reload();
        }
    }

    public RoomTile getTile(Position position) {
        if (position == null) return null;

        return this.getTile(position.getX(), position.getY());
    }

    public RoomTile getTile(int x, int y) {
        if (x < 0 || y < 0) return null;

        if (x >= this.tiles.length || (this.tiles[x] == null || y >= this.tiles[x].length)) return null;

        return this.tiles[x][y];
    }

    public RoomTile getRandomReachableTile(RoomFloorObject roomFloorObject) {
        for (int tries = 0; tries < this.getModel().getSizeX() * this.getModel().getSizeY(); tries++) {
            final int randomX = RandomUtil.getRandomInt(0, this.getModel().getSizeX() - 1);
            final int randomY = RandomUtil.getRandomInt(0, this.getModel().getSizeY() - 1);

            final RoomTile tile = this.getTile(randomX, randomY);

            if (tile.isReachable(roomFloorObject)) {
                return tile;
            }
        }

        return null;
    }

    public boolean positionHasUser(Integer entityId, Position position) {
        int entitySize = 0;
        boolean hasMe = false;

        if (entityId == null || entityId == -1)
            return false;

        for (final RoomEntity entity : this.room.getEntities().getEntitiesAt(position)) {
            entitySize++;

            if (entity.getMountedEntity() != null) {
                if (entity.getMountedEntity().getId() == entityId) {
                    return false;
                }
            }

            if (entity instanceof PetEntity && entity.getTile().getTopItemInstance() instanceof BreedingBoxFloorItem) {
                return false;
            }

            // TODO: clean up this
            if (entityId != 0 && entity.getId() == entityId) {
                hasMe = true;
            }
        }

        return !(hasMe && entitySize == 1) && entitySize > 0;
    }

    public boolean positionHasUser(Position position) {
        final List<RoomEntity> entities = this.room.getEntities().getEntitiesAt(position);

        for (final RoomEntity entity : entities) {
            if (!entity.canWalkOn()) {
                return true;
            }


            if (entity instanceof BotEntity || entity instanceof PlayerEntity || entity instanceof PetEntity) {
                return true;


            }
        }

        return false;
    }

    public boolean isValidEntityStep(RoomEntity entity, Position currentPosition, Position toPosition, boolean isFinalMove, boolean isRetry, boolean generating) {
        var clickThrough = entity.getEntityType() != RoomEntityType.PLAYER || (((PlayerEntity) entity).getPlayer().isClickThrough());

        return isValidStep(entity.getId(), currentPosition, toPosition, isFinalMove, false, isRetry, false, false, entity.isOverriden(), clickThrough, true, generating);
    }

    public boolean isValidEntityStep(RoomEntity entity, Position currentPosition, Position toPosition, boolean isFinalMove) {
        return isValidStep(entity.getId(), currentPosition, toPosition, isFinalMove, false, true, false, false, entity.isOverriden(), false, false, false);
    }

    public boolean isValidStep(Position from, Position to, boolean lastStep) {
        return isValidStep(null, from, to, lastStep, false, false);
    }

    public boolean isValidStep(Position from, Position to, boolean lastStep, boolean isFloorItem) {
        return isValidStep(null, from, to, lastStep, isFloorItem, false);
    }

    public boolean isValidStep(@Nullable Integer entity, Position from, Position to, boolean lastStep, boolean isFloorItem, boolean isRetry) {
        return isValidStep(entity, from, to, lastStep, isFloorItem, isRetry, false);
    }

    public boolean isValidStep(@Nullable Integer entity,
                               Position from,
                               Position to,
                               boolean lastStep,
                               boolean isFloorItem,
                               boolean isRetry,
                               boolean ignoreHeight) {
        return isValidStep(entity, from, to, lastStep, isFloorItem, isRetry, ignoreHeight, false);
    }

    public boolean isValidStep(
            @Nullable Integer entity,
            Position from,
            Position to,
            boolean lastStep,
            boolean isFloorItem,
            boolean isRetry,
            boolean ignoreHeight,
            boolean isItemOnRoller
    ) {
        return isValidStep(entity, from, to, lastStep, isFloorItem, isRetry, ignoreHeight, isItemOnRoller, false, false, false, false);
    }

    public boolean isValidStep(
            Integer entity,
            Position from,
            Position to,
            boolean lastStep,
            boolean isFloorItem,
            boolean isRetry,
            boolean ignoreHeight,
            boolean isItemOnRoller,
            boolean isOverriding,
            boolean isClickThrough,
            boolean checkDiag,
            boolean generating
    ) {
        if (from.getX() == to.getX() && from.getY() == to.getY()) {
            return true;
        }


        if (!isValidPosition(to) || (this.getModel().getSquareState()[to.getX()][to.getY()] == RoomTileState.INVALID)) {
            return false;
        }

        final boolean isAtDoor = this.getModel().getDoorX() == from.getX() && this.getModel().getDoorY() == from.getY();

        if (to.getX() == this.getModel().getDoorX() && to.getY() == this.getModel().getDoorY() && !lastStep) {
            return false;
        }

        int entityId;

        if (entity == null) {
            entityId = -1;
        } else if (isFloorItem) {
            entityId = 0;
        } else {
            entityId = entity;
        }

        if (isFloorItem) {
            if (this.getTile(to).hasGate()) {
                return false;
            }
        }


        if (checkDiag && this.getRoom().getData().getRoomDiagonalType().equals(RoomDiagonalType.STRICT)) {

            var xLen = tiles.length;
            var yLen = tiles[0].length;
            int xValue = to.getX() - from.getX();
            int yValue = to.getY() - from.getY();

            if (xValue == -1 && yValue == -1) {
                if (xLen <= to.getX() + 1 || yLen <= to.getY() + 1) {
                    return false;
                }

                var sqState = tiles[to.getX() + 1][to.getY() + 1];
                if (sqState.getState() != RoomTileState.VALID || sqState.getMovementNode() != RoomEntityMovementNode.OPEN)
                    return false;
            } else if (xValue == 1 && yValue == -1) {
                if (xLen <= to.getX() - 1 || yLen <= to.getY() + 1) {
                    return false;
                }

                var sqState = tiles[to.getX() - 1][to.getY() + 1];

                if (sqState.getState() != RoomTileState.VALID || sqState.getMovementNode() != RoomEntityMovementNode.OPEN)
                    return false;
            } else if (xValue == 1 && yValue == 1) {
                if (xLen <= to.getX() - 1 || yLen <= to.getY() - 1) {
                    return false;
                }

                var sqState = tiles[to.getX() - 1][to.getY() - 1];

                if (sqState.getState() != RoomTileState.VALID || sqState.getMovementNode() != RoomEntityMovementNode.OPEN)
                    return false;
            } else if (xValue == -1 && yValue == 1) {
                if (xLen <= to.getX() + 1 || yLen <= to.getY() - 1) {
                    return false;
                }

                var sqState = tiles[to.getX() + 1][to.getY() - 1];

                if (sqState.getState() != RoomTileState.VALID || sqState.getMovementNode() != RoomEntityMovementNode.OPEN)
                    return false;
            }
        }

        if (!this.getRoom().getData().getRoomDiagonalType().equals(RoomDiagonalType.ENABLED)) {

            final int rotation = Position.calculateRotation(from, to);

            // Get all tiles at passing corners
            RoomTile left = null;
            RoomTile right = null;

            switch (rotation) {
                case 1 -> {
                    left = this.getTile(from.squareInFront(rotation + 1));
                    right = this.getTile(to.squareBehind(rotation + 1));
                }
                case 3 -> {
                    left = this.getTile(to.squareBehind(rotation + 1));
                    right = this.getTile(to.squareBehind(rotation - 1));
                }
                case 5 -> {
                    left = this.getTile(from.squareInFront(rotation - 1));
                    right = this.getTile(to.squareBehind(rotation - 1));
                }
                case 7 -> {
                    left = this.getTile(to.squareBehind(rotation - 1));
                    right = this.getTile(from.squareInFront(rotation - 1));
                }
            }

            if (left != null && right != null) {
                if (left.getMovementNode() != RoomEntityMovementNode.OPEN && right.getState() == RoomTileState.INVALID)
                    return false;

                if (right.getMovementNode() != RoomEntityMovementNode.OPEN && left.getState() == RoomTileState.INVALID)
                    return false;

                if (left.getMovementNode() != RoomEntityMovementNode.OPEN && right.getMovementNode() != RoomEntityMovementNode.OPEN)
                    return false;
            }

        }

        if (isOverriding)
            return true;

        final boolean positionHasUser = positionHasUser(entityId, to);

        if (positionHasUser) {

            if (lastStep && !isAtDoor && !room.getData().getAllowWalkthrough())
                return isClickThrough && generating;

            if (!isRetry && !room.getData().getAllowWalkthrough())
                return  generating;

            if ((!room.getData().getAllowWalkthrough() || isFloorItem) && !isAtDoor)
                return  generating;
        }

        final RoomTile tile = tiles[to.getX()][to.getY()];

        if (tile == null)
            return false;

        if (tile.getTopItemInstance() instanceof OneWayGateFloorItem) {
            final OneWayGateFloorItem item = (OneWayGateFloorItem) tile.getTopItemInstance();

            if (entity != null && item.getInteractingEntity() != null && item.getInteractingEntity().getId() == entity)
                return true;
        }

        if ((tile.getMovementNode() == RoomEntityMovementNode.CLOSED || (tile.getMovementNode() == RoomEntityMovementNode.END_OF_ROUTE && !lastStep)) && !isItemOnRoller)
            return isClickThrough && generating;

        if (ignoreHeight || (isClickThrough && generating))
            return true;


        if (isAtDoor)
            return true;

        final double fromHeight = this.getStepHeight(from);
        final double toHeight = this.getStepHeight(to);


        if (fromHeight > toHeight) {
            if (entity != null)
                return true;

            if (fromHeight - toHeight >= 3)
                return false;
        }

        return !(fromHeight < toHeight && (toHeight - fromHeight) > 1.2);
    }

    public double getStepHeight(Position position) {
        if (this.tiles.length <= position.getX() || this.tiles[position.getX()].length <= position.getY())
            return 0.0;

        final RoomTile instance = this.tiles[position.getX()][position.getY()];

        if (!isValidPosition(instance.getPosition()))
            return 0.0;

        if (instance.getStatus() == null)
            return 0.0;

        return instance.getWalkHeight();
    }

    public List<Position> tilesWithFurniture() {
        final List<Position> tilesWithFurniture = Lists.newArrayList();

        for (int x = 0; x < this.tiles.length; x++) {
            for (int y = 0; y < this.tiles[x].length; y++) {
                if (this.tiles[x][y].hasItems()) tilesWithFurniture.add(new Position(x, y));
            }
        }

        return tilesWithFurniture;
    }

    public boolean isValidPosition(Position position) {
        return ((position.getX() >= 0) && (position.getY() >= 0) && (position.getX() < this.getModel().getSizeX()) && (position.getY() < this.getModel().getSizeY()));
    }

    public final Room getRoom() {
        return this.room;
    }

    public IRoomModel getModel() {
        return this.room.getModel();
    }

    @Override
    public String toString() {
        final StringBuilder mapString = new StringBuilder();

        for (final RoomTile[] tile : this.tiles) {
            for (final RoomTile roomTile : tile) {
                if (roomTile.getMovementNode() == RoomEntityMovementNode.CLOSED) {
                    mapString.append(" ");
                } else {
                    mapString.append("X");
                }
            }

            mapString.append("\n");
        }

        return mapString.toString();
    }

    public String visualiseEntityGrid() {
        final StringBuilder builder = new StringBuilder();

        for (final RoomTile[] tile : this.tiles) {
            for (final RoomTile roomTile : tile) {
//                if (this.tiles[y][x].getItems().size() != 0) {
//                    builder.append("O");
                /*} else */
                if (roomTile.getEntities().size() != 0) {
                    builder.append("E");
                } else {
                    builder.append("[]");
                }

            }

            builder.append("\n");
        }

        return builder.toString();
    }

    public RoomTile[][] getTiles() {
        return this.tiles;
    }
}
