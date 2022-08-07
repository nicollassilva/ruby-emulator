package com.cometproject.server.game.rooms.types.mapping;

import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.RoomObject;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.types.EntityPathfinder;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.types.ItemPathfinder;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.*;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.freeze.FreezeBlockFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.groups.GroupGateFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.pet.breeding.BreedingBoxFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.snowboarding.SnowboardJumpFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredAddonNoItemsAnimateEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonKebBar;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class RoomTile {
    public static final double INVALID_STACK_HEIGHT = 64d; // habbo client constant for invalid tile to place items
    private final RoomMapping mappingInstance;
    private final Position position;
    private final List<RoomItemFloor> items;
    private final Map<Integer, Consumer<RoomEntity>> pendingEvents = new ConcurrentHashMap<>();
    public Set<RoomEntity> entities;
    private RoomEntityMovementNode movementNode;
    private RoomTileStatusType status;
    private RoomTileState state;
    private boolean canStack;
    private long topItem = 0;
    private double stackHeight = 0d;
    private long originalTopItem = 0;
    private double originalHeight = 0d;
    private Position redirect = null;
    private boolean canPlaceItemHere = false;
    private boolean hasItems = false;
    private boolean hasMagicTile = false;
    private boolean hasAdjustableHeight = false;
    private boolean hasGate = false;

    public RoomTile(RoomMapping mappingInstance, Position position) {
        this.mappingInstance = mappingInstance;
        this.position = position;
        this.entities = new ConcurrentHashSet<>();
        this.items = new ArrayList<>();

        this.reload();
    }

    public List<RoomTile> getAdjacentTiles(Position from) {
        final List<RoomTile> roomTiles = Lists.newArrayList();

        for (int rotation : Position.COLLIDE_TILES) {
            final RoomTile tile = this.mappingInstance.getTile(this.getPosition().squareInFront(rotation));

            roomTiles.add(tile);
        }

        roomTiles.sort((left, right) -> {
            if (left == null || right == null || left.getPosition() == null || right.getPosition() == null) return -1;

            final double distanceFromLeft = left.getPosition().distanceTo(from);
            final double distanceFromRight = right.getPosition().distanceTo(from);

            return Double.compare(distanceFromLeft, distanceFromRight);
        });

        return roomTiles;
    }

    public void reload() {
        // reset the tile data
        this.hasItems = false;
        this.redirect = null;
        this.movementNode = RoomEntityMovementNode.OPEN;
        this.status = RoomTileStatusType.NONE;
        this.canStack = true;
        this.hasMagicTile = false;
        this.topItem = 0;
        this.originalHeight = 0d;
        this.originalTopItem = 0;
        this.stackHeight = 0d;
        this.hasAdjustableHeight = false;
        this.hasGate = false;

        if (this.mappingInstance.getModel().getSquareState()[this.getPosition().getX()][this.getPosition().getY()] == null) {
            this.canPlaceItemHere = false;
            this.state = RoomTileState.INVALID;
        } else {
            this.canPlaceItemHere = this.mappingInstance.getModel().getSquareState()[this.getPosition().getX()][this.getPosition().getY()].equals(RoomTileState.VALID);
            this.state = this.mappingInstance.getModel().getSquareState()[this.getPosition().getX()][this.getPosition().getY()];
        }

        // component item is an item that can be used along with an item that overrides the height.
        boolean hasComponentItem = false;

        double highestHeight = 0d;
        long highestItem = 0;

        Double staticOverrideHeight = null;
        Double overrideHeight = null;

        this.items.clear();

        for (final Map.Entry<Long, RoomItemFloor> itemEntry : mappingInstance.getRoom().getItems().getFloorItems().entrySet()) {
            final RoomItemFloor item = itemEntry.getValue();

            if (item == null || item.getDefinition() == null) continue; // it's null!

            if (item.getPosition().getX() == this.position.getX() && item.getPosition().getY() == this.position.getY()) {
                items.add(item);
            } else {
                final List<AffectedTile> affectedTiles = AffectedTile.getAffectedTilesAt(
                        item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation());

                for (final AffectedTile tile : affectedTiles) {
                    if (this.position.getX() == tile.x && this.position.getY() == tile.y) {
                        if (!items.contains(item)) {
                            items.add(item);
                        }
                    }
                }
            }
        }

        for (final RoomItemFloor item : new ArrayList<>(items)) {
            if (item.getDefinition() == null)
                continue;

            if (item instanceof GateFloorItem || item instanceof GroupGateFloorItem) {
                this.hasGate = true;
            }

            this.hasItems = true;
            final double totalHeight = getTotalHeight(item);
            if (totalHeight > highestHeight) {
                highestHeight = totalHeight;
                highestItem = item.getId();
            }

            final boolean isGate = item instanceof GateFloorItem;
            if (item instanceof MagicStackFloorItem) {
                this.hasMagicTile = true;
            }

            if (!item.getDefinition().canWalk() && !isGate) {
                if (highestItem == item.getId())
                    movementNode = RoomEntityMovementNode.CLOSED;
            }

            switch (item.getDefinition().getInteraction().toLowerCase()) {
                case "bed":
                    status = RoomTileStatusType.LAY;
                    movementNode = RoomEntityMovementNode.END_OF_ROUTE;

                    if (item.getRotation() == 2 || item.getRotation() == 6) {
                        this.redirect = new Position(item.getPosition().getX(), this.getPosition().getY());
                    } else if (item.getRotation() == 0 || item.getRotation() == 4) {
                        this.redirect = new Position(this.getPosition().getX(), item.getPosition().getY());
                    }

                    break;

                case "gate":
                    if (!(item instanceof GateFloorItem)) {
                        movementNode = RoomEntityMovementNode.CLOSED;
                    } else {
                        movementNode = ((GateFloorItem) item).isOpen() ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    }
                    break;

                case "onewaygate":
                    movementNode = RoomEntityMovementNode.CLOSED;
                    break;

                case "wf_pyramid":
                    movementNode = item.getItemData().getData().equals("1") ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    break;

                case "freeze_block":
                    if (!(item instanceof FreezeBlockFloorItem)) {
                        movementNode = RoomEntityMovementNode.CLOSED;
                    } else {
                        movementNode = ((FreezeBlockFloorItem) item).isDestroyed() ? RoomEntityMovementNode.OPEN : RoomEntityMovementNode.CLOSED;
                    }
                    break;
            }

            if (item instanceof BreedingBoxFloorItem) {
                movementNode = RoomEntityMovementNode.END_OF_ROUTE;
            }

            if (item instanceof SnowboardJumpFloorItem) {
                hasComponentItem = true;
            }

            if (item.getDefinition().canSit()) {
                status = RoomTileStatusType.SIT;
                movementNode = RoomEntityMovementNode.END_OF_ROUTE;
            }

            if (item.getDefinition().getInteraction().equals("bed")) {
                status = RoomTileStatusType.LAY;
                movementNode = RoomEntityMovementNode.END_OF_ROUTE;
            }

            if (!item.getDefinition().canStack()) {
                this.canStack = false;
            }

            if (item.getOverrideHeight() != -1d) {
                if (item instanceof MagicStackFloorItem) {
                    staticOverrideHeight = item.getOverrideHeight();
                }

                if (overrideHeight != null) {
                    overrideHeight += item.getOverrideHeight() + (hasComponentItem ? 1.0 : 0d);
                } else {
                    overrideHeight = item.getOverrideHeight() + (hasComponentItem ? 1.0 : 0d);
                }
            }
        }

        if (overrideHeight != null) {
            this.hasAdjustableHeight = true;
            this.canStack = true;
            this.stackHeight = staticOverrideHeight != null ? staticOverrideHeight : highestHeight;

            this.originalHeight = highestHeight;
        } else {
            this.stackHeight = highestHeight;
        }

        this.topItem = highestItem;

        if (this.stackHeight == 0d) {
            this.stackHeight = this.mappingInstance.getModel().getSquareHeight()[this.position.getX()][this.position.getY()];
        }

        if (this.originalHeight == 0)
            this.originalHeight = this.stackHeight;
    }

    private static double getTotalHeight(RoomItemFloor item) {
        return item.getPosition().getZ() + (item.getOverrideHeight() != -1d ? item.getOverrideHeight() : item.getDefinition().getHeight());
    }

    public void dispose() {
        this.pendingEvents.clear();
        this.items.clear();
        this.entities.clear();
    }

    public void onEntityEntersTile(RoomEntity entity) {
        if (this.pendingEvents.containsKey(entity.getId())) {
            this.pendingEvents.get(entity.getId()).accept(entity);
            this.pendingEvents.remove(entity.getId());
        }
    }

    public void scheduleEvent(int entityId, Consumer<RoomEntity> event) {
        this.pendingEvents.put(entityId, event);
    }

    public RoomEntityMovementNode getMovementNode() {
        return this.movementNode;
    }

    public double getStackHeight() {
        return this.getStackHeight(null);
    }

    public double getStackHeight(RoomItemFloor itemToStack) {
        final RoomItemFloor topItem = this.getTopItemInstance();

        double stackHeight = INVALID_STACK_HEIGHT;
        if (this.hasMagicTile() || (topItem instanceof AdjustableHeightFloorItem)) {
            stackHeight = itemToStack != null && itemToStack.getId() == this.getTopItem() ? itemToStack.getPosition().getZ() : this.stackHeight;
            // TODO: after reload tile, it does check if top item can stack or not. This check shouldn't be necessary...
        } else if (this.canPlaceItemHere && this.canStack && (topItem == null || topItem.getDefinition().canStack())) {
            stackHeight = itemToStack != null && itemToStack.getId() == this.getTopItem() ? itemToStack.getPosition().getZ() : this.originalHeight;
        }

        return Math.min(stackHeight, INVALID_STACK_HEIGHT);
    }

    private final Comparator<RoomItemFloor> heightByDesc = (a,b) -> Double.compare(getTotalHeight(b), getTotalHeight(a));
    public double getTopHeight(RoomItemFloor exclude) {
        if(items.isEmpty())
            return 0d;

        synchronized (this.items){
            for (final RoomItemFloor item : this.items.stream().sorted(heightByDesc).collect(Collectors.toList())) {
                if(exclude != null && exclude.getId() == item.getId()) {
                    continue;
                }

                if(!item.getDefinition().canStack())
                    return INVALID_STACK_HEIGHT;

                return Math.min(getTotalHeight(item), INVALID_STACK_HEIGHT);
            }

            return 0d;
        }
    }

    public double getWalkHeight() {
        double height = this.stackHeight;

        final RoomItemFloor roomItemFloor = this.mappingInstance.getRoom().getItems().getFloorItem(this.topItem);

        if (roomItemFloor != null && !(roomItemFloor instanceof RollerFloorItem) && (roomItemFloor.getDefinition().canSit() || roomItemFloor instanceof BedFloorItem || roomItemFloor instanceof SnowboardJumpFloorItem)) {
            if (roomItemFloor instanceof SnowboardJumpFloorItem) {
                height += 1.0;
            } else {
                height -= roomItemFloor.getDefinition().getHeight();
            }
        }

        return height;
    }

    public boolean isReachable(RoomEntity entity) {
        List<Square> path = EntityPathfinder.getInstance().makePath(entity, this.position);
        return path != null && path.size() > 0;
    }

    public boolean isReachable(RoomObject object) {
        List<Square> path = ItemPathfinder.getInstance().makePath(object, this.position);
        return path != null && path.size() > 0;
    }

    public RoomEntity getEntity() {
        for (final RoomEntity entity : this.getEntities()) {
            return entity;
        }

        return null;
    }

    public Set<RoomEntity> getEntities() {
        return this.entities;
    }

    public RoomTileStatusType getStatus() {
        return this.status;
    }

    public Position getPosition() {
        return this.position;
    }

    public boolean canStack() {
        return this.canStack;
    }

    public long getTopItem() {
        return this.topItem;
    }

    public void setTopItem(int topItem) {
        this.topItem = topItem;
    }

    public RoomItemFloor getTopItemInstance() {
        return this.mappingInstance.getRoom().getItems().getFloorItem(this.getTopItem());
    }

    public Position getRedirect() {
        return redirect;
    }

    public void setRedirect(Position redirect) {
        this.redirect = redirect;
    }

    public long getOriginalTopItem() {
        return originalTopItem;
    }

    public double getOriginalHeight() {
        return originalHeight;
    }

    public boolean canPlaceItemHere() {
        return canPlaceItemHere;
    }

    public boolean hasItems() {
        return hasItems;
    }

    public int getTileHeight() {
        return this.mappingInstance.getModel().getSquareHeight()[this.position.getX()][this.position.getY()];
    }

    public boolean hasMagicTile() {
        return this.hasMagicTile;
    }

    public void setMagicTile(boolean magicTile) {
        this.hasMagicTile = magicTile;
    }

    public List<RoomItemFloor> getItems() {
        return items;
    }

    public boolean hasAdjustableHeight() {
        return hasAdjustableHeight;
    }

    public RoomTileState getState() {
        return state;
    }

    public boolean hasGate() {
        return this.hasGate;
    }
}
