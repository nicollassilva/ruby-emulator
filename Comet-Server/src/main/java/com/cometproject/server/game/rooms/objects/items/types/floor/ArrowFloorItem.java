package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.events.types.RollerFloorItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOnFurni;
import com.cometproject.server.game.rooms.objects.items.types.state.FloorItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class ArrowFloorItem extends RoomItemFloor {
    private final Set<Integer> skippedEntities = Sets.newConcurrentHashSet();

    public ArrowFloorItem (RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        final Position sqInfront = this.getPosition().squareInFront(this.getRotation());

        if (!this.getRoom().getMapping().isValidPosition(sqInfront)) {
            return;
        }

        final List<RoomEntity> entities = this.getRoom().getEntities().getEntitiesAt(this.getPosition());

        for (final RoomEntity entity2 : entities) {
            if (entity2.getPosition().getX() != this.getPosition().getX() && entity2.getPosition().getY() != this.getPosition().getY()) {
                continue;
            }

            if (this.skippedEntities.contains(entity2.getId())) {
                continue;
            }

            if (entity2.getPositionToSet() != null) {
                continue;
            }

            if (entity2.isWalking()) {
                continue;
            }

            final double toHeight = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY()).getWalkHeight();

            final RoomTile oldTile = this.getRoom().getMapping().getTile(entity.getPosition().getX(), entity.getPosition().getY());
            final RoomTile newTile = this.getRoom().getMapping().getTile(sqInfront.getX(), sqInfront.getY());

            if (sqInfront.getX() == this.getRoom().getModel().getDoorX() && sqInfront.getY() == this.getRoom().getModel().getDoorY()) {
                entity2.leaveRoom(false, false, true);
                continue;
            }

            if (this.getRoom().getMapping().isValidStep(entity.getId(), entity.getPosition(), sqInfront, true, false, false, true, true) || !this.getRoom().getEntities().positionHasEntity(sqInfront)) {
                entity2.moveTo(sqInfront.getX(), sqInfront.getY());
                entity2.updateAndSetPosition(new Position(sqInfront.getX(), sqInfront.getY(), toHeight));
                break;
            }

            if (oldTile != null) {
                oldTile.getEntities().remove(entity);
            }

            if (newTile != null) {
                newTile.getEntities().add(entity);
            }

            this.setTicks(RoomItemFactory.getProcessTime(1));
            entity2.moveTo(sqInfront.getX(), sqInfront.getY());
            //entity2.updateAndSetPosition(new Position(sqInfront.getX(), sqInfront.getY(), toHeight));

            entity2.unIdle();
            entity2.resetAfkTimer();
            entity.setPosition(new Position(sqInfront.getX(), sqInfront.getY(), toHeight));

            //movedEntities.add(entity2);
        }
    }
}
