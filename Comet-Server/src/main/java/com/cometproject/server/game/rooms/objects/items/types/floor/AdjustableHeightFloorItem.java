package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import org.apache.commons.lang.StringUtils;


public class AdjustableHeightFloorItem extends RoomItemFloor {
    private final Double[] variations;

    public AdjustableHeightFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        this.variations = this.getDefinition().getVariableHeights();
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            final PlayerEntity pEntity = (PlayerEntity) entity;

            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId())
                    && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
            if (floorItem.getId() != this.getId() && floorItem.getPosition().getZ() >= this.getPosition().getZ())
                return false;
        }

        if(this.getDefinition().getVariableHeights() == null)
            return false;

        this.toggleInteract(true);
        this.sendUpdate();

        final double currentHeight = this.getOverrideHeight() + this.getPosition().getZ();

        for (final RoomEntity entityOnItem : this.getEntitiesOnItem()) {
            if (entityOnItem.hasStatus(RoomEntityStatus.SIT)) {
                entityOnItem.removeStatus(RoomEntityStatus.SIT);
            }

            entityOnItem.setPosition(new Position(entityOnItem.getPosition().getX(), entityOnItem.getPosition().getY(), currentHeight));
            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entityOnItem));
        }

        this.saveData();
        return true;
    }

    @Override
    public double getOverrideHeight() throws NumberFormatException {
        try {
            if(this.getItemData().getData().isEmpty() || !StringUtils.isNumeric(this.getItemData().getData())) {
                return this.variations[0];
            }

            final int currentHeight = Integer.parseInt(this.getItemData().getData());

            if(this.variations[currentHeight] != null) {
                return this.variations[currentHeight];
            }
        } catch (NumberFormatException e) {
            return 0D;
        }

        return 0D;
    }
}
