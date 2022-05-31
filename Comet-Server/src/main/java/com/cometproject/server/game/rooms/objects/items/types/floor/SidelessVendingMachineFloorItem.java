package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.utilities.DistanceCalculator;
import com.cometproject.server.utilities.RandomUtil;


public class SidelessVendingMachineFloorItem extends RoomItemFloor {
    private RoomEntity vendingEntity;
    private int state = -1;

    public SidelessVendingMachineFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        final PlayerEntity playerEntity = ((PlayerEntity) entity);

        if (isWiredTrigger || entity == null) return false;

        if(DistanceCalculator.calculate(entity.getPosition(), this.getPosition()) > 1) {
            if(!playerEntity.getPlayer().getEntity().isFreeze() && !playerEntity.hasAttribute("interacttpencours") && !playerEntity.hasAttribute("tptpencours")) {
                final Position sqInFront = this.getPosition().squareInFront(this.getRotation());

                entity.moveTo(sqInFront.getX(), sqInFront.getY());

                try {
                    this.getRoom().getMapping().getTile(sqInFront.getX(), sqInFront.getY()).scheduleEvent(entity.getId(), (e) -> onInteract(e, requestData, false));
                } catch (Exception ignored) {
                }
            }

            return false;
        }

        int rotation = Position.calculateRotation(entity.getPosition().getX(), entity.getPosition().getY(), this.getPosition().getX(), this.getPosition().getY(), false);

        if (!entity.hasStatus(RoomEntityStatus.SIT) && !entity.hasStatus(RoomEntityStatus.LAY)) {
            entity.setBodyRotation(rotation);
            entity.setHeadRotation(rotation);

            entity.markNeedsUpdate();
        }

        this.vendingEntity = entity;

        this.state = 0;
        this.setTicks(RoomItemFactory.getProcessTime(1));
        return true;
    }

    @Override
    public void onTickComplete() {
        switch (this.state) {
            case 0: {
                this.getItemData().setData("1");
                this.sendUpdate();

                this.state = 1;
                this.setTicks(RoomItemFactory.getProcessTime(0.5));
                break;
            }

            case 1: {
                if (this.getDefinition().getVendingIds().length != 0) {
                    int vendingId = Integer.parseInt(this.getDefinition().getVendingIds()[RandomUtil.getRandomInt(0, this.getDefinition().getVendingIds().length - 1)].trim());
                    vendingEntity.carryItem(vendingId);
                }

                this.state = 2;
                this.setTicks(RoomItemFactory.getProcessTime(0.5));
                break;
            }

            case 2: {
                this.getItemData().setData("0");
                this.sendUpdate();

                this.state = 0;
                this.vendingEntity = null;
                break;
            }
        }
    }

    @Override
    public void onPlaced() {
        this.getItemData().setData("0");
    }
}
