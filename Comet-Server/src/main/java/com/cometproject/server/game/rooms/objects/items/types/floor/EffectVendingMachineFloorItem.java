package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.utilities.RandomUtil;


public class EffectVendingMachineFloorItem extends RoomItemFloor {
    private RoomEntity vendingEntity;
    private int state = -1;

    public EffectVendingMachineFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        final PlayerEntity playerEntity = ((PlayerEntity) entity);

        if (isWiredTrigger || entity == null) return false;

        final Position posInFront = this.getPosition().squareInFront(this.getRotation());

        if (!posInFront.equals(entity.getPosition())) {
            if(!playerEntity.getPlayer().getEntity().isFreeze() && !playerEntity.hasAttribute("interacttpencours") && !playerEntity.hasAttribute("tptpencours")) {
                entity.moveTo(posInFront.getX(), posInFront.getY());
            }

            try {
                this.getRoom().getMapping().getTile(posInFront.getX(), posInFront.getY()).scheduleEvent(entity.getId(), (e) -> onInteract(e, requestData, false));
            } catch (Exception ignored) {
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
                if (this.getDefinition().getEffectId() != 0) {
                    vendingEntity.applyEffect(new PlayerEffect(this.getDefinition().getEffectId(), 0));
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
