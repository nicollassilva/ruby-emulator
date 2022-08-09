package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.RoomDiagonalType;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Pathfinder;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.types.ItemPathfinder;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerCollision;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom.WiredTriggerCollisionPlayer;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.utilities.RandomUtil;
import com.cometproject.server.utilities.comparators.PositionComparator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WiredActionChase extends WiredActionItem {
    private static final int[] MOVE_DIR = new int[]{0,1,2,3};
    public static final int CHASE_RADIUS = 4;
    public static final double MOVEMENT_RANDOM_CHANCE = 35;
    private int targetId = -1;

    /**
     * Wired action to chase room entity
     *
     * @param itemData the item data
     * @param room     the room
     */
    public WiredActionChase(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 8;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (this.getWiredData().getSelectedIds().size() == 0) return;

        for (long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);
            if (floorItem == null)
                continue;

            final List<PlayerEntity> entities = getNearestPlayerEntitiesInRadius(floorItem, CHASE_RADIUS);
            boolean collision = false;
            for (final PlayerEntity entity : entities) {
                if (isCollided(floorItem, entity)) { // call colision trigger and skip for dont waste resources
                    WiredTriggerCollision.executeTriggers(entity, floorItem);
                    collision = true;
                }
            }

            if(collision){
                continue;
            }

            final List<Square> nearestEntityPath = getNearestEntityPath(floorItem, entities);
            if (nearestEntityPath.size() == 0) {
                moveFloorItemRandomly(floorItem);
                continue;
            }

            final Position currentPosition = floorItem.getPosition().copy();
            final Square nextSquare = nearestEntityPath.get(0);
            final Position nextPosition = new Position(nextSquare.x, nextSquare.y);
            floorItem.setMoveDirection(Position.calculateRotation(currentPosition, nextPosition));
            if (this.getRoom().getItems().moveFloorItemWired(floorItem, nextPosition, floorItem.getRotation(), true, true)) {
                this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, nextPosition, this.getVirtualId(), 0, floorItem.getVirtualId()));
            } else {
                moveFloorItemRandomly(floorItem);
            }
        }
    }

    private List<Square> getNearestEntityPath(RoomItemFloor item, List<PlayerEntity> entities) {
        if (entities.size() == 0) return new ArrayList<>();
        for (final PlayerEntity entity : entities) {
            targetId = entity.getId();
            final List<Square> path = ItemPathfinder.getInstance().makePath(item, entity.getPosition(), RoomDiagonalType.DISABLED.getKey(), false);
            if (path.size() > 0) {
                return path;
            }
        }

        return new ArrayList<>();
    }

    private List<PlayerEntity> getNearestPlayerEntitiesInRadius(RoomItemFloor item, int radius) {
        final List<PlayerEntity> entities = new ArrayList<>();
        for (int x = item.getPosition().getX() - radius; x < item.getPosition().getX() + radius; x++) {
            for (int y = item.getPosition().getY() - radius; y < item.getPosition().getY() + radius; y++) {
                for (final RoomEntity entity : this.getRoom().getEntities().getEntitiesAt(new Position(x, y))) {
                    if (entity instanceof PlayerEntity) {
                        entities.add((PlayerEntity) entity);
                        break;
                    }
                }
            }
        }

        final PositionComparator positionComparator = new PositionComparator(this);
        entities.sort(positionComparator);
        return entities;
    }

    public int getTargetId() {
        return targetId;
    }


    private void moveFloorItemRandomly(RoomItemFloor item) {
        /* TODO: isso ainda nao está pronto. na versao final, ele ira andar em uma determinada direção
         e randomicamente alterar a direção. mas até agora está ok
         */

        final Position currentPosition = item.getPosition().copy();
        final Position newPosition = WiredActionMoveRotate.getRandomPosition(1, currentPosition, this.getRoom());
        if (this.getRoom().getItems().moveFloorItemWired(item, newPosition, item.getRotation(), true, true)) {
            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, item.getVirtualId()));
        }
    }

    private static boolean isCollided(RoomItemFloor item, RoomEntity entity){
        return item.getPosition().equals(entity.getPosition()) || item.getPosition().distanceTo(entity.getPosition()) == 1;
    }
}
