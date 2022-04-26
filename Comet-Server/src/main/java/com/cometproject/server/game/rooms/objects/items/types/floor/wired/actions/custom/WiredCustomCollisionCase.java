package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

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
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.utilities.RandomUtil;

import java.util.ArrayList;
import java.util.List;


public class WiredCustomCollisionCase extends WiredActionItem {
    private int targetId = -1;

    /**
     * Wired action to chase room entity
     *
     * @param itemData the item data
     * @param room     the room
     */
    public WiredCustomCollisionCase(RoomItemData itemData, Room room) {
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
            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) continue;

            List<RoomEntity> entities = floorItem.getEntitiesOnItem();
            for(RoomEntity entity : entities) {
                //if(entity instanceof PlayerEntity) {

                    if (isCollided(entity, floorItem)) {
                        WiredTriggerCollision.executeTriggers(entity, floorItem);
                    }
                //}
            }
        }

    }

    public boolean isCollided(RoomEntity entity, RoomItemFloor floorItem) {

        int maxX = 0;
        int maxY = 0;

        if(floorItem.getRotation() == 0 || floorItem.getRotation() == 4) {
            maxX = floorItem.getPosition().getX() + floorItem.getDefinition().getWidth();
            maxY = floorItem.getPosition().getY() + floorItem.getDefinition().getLength();
        } else {
            maxX = floorItem.getPosition().getX() + floorItem.getDefinition().getLength();
            maxY = floorItem.getPosition().getY() + floorItem.getDefinition().getWidth();
        }

        for(int x = floorItem.getPosition().getX(); x < maxX; ++x) {
            for(int y = floorItem.getPosition().getY(); y < maxY; ++y) {
                if (entity.getPosition().equals(new Position(x,y))) {
                    return true;
                }
            }
        }


        return false;
    }
}

