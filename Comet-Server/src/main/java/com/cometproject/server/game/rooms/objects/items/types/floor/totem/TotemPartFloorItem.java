package com.cometproject.server.game.rooms.objects.items.types.floor.totem;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums.TotemColor;
import com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums.TotemPlanetType;
import com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums.TotemType;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import org.apache.commons.lang.StringUtils;

public abstract class TotemPartFloorItem extends RoomItemFloor {
    public TotemPartFloorItem(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        if (!StringUtils.isNumeric(this.getItemData().getData())) {
            this.getItemData().setData("0");
        }
    }

    public TotemType getTotemType() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getItemData().getData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }

        if (extraData < 3) {
            return TotemType.fromInt(extraData + 1);
        }

        return TotemType.fromInt((int)Math.ceil((extraData - 2) / 4.0f));
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        //make sure that is completed the totem
        if (this.isComplete()) {
            if (!(this instanceof final TotemPlanetFloorItem totemPlanetFloorItem))
                return false;

            final TotemPlanetType totemPlanetType = totemPlanetFloorItem.getPlanetType();

            final TotemHeadFloorItem totemHeadFloorItem = (TotemHeadFloorItem) this.getTotemPart(true);
            if (totemHeadFloorItem == null)
                return false;

            final TotemType totemHeadType = totemHeadFloorItem.getTotemType();

            final TotemBodyFloorItem totemBodyFloorItem = (TotemBodyFloorItem) this.getTotemPart(false);
            if (totemBodyFloorItem == null)
                return false;

            final TotemType totemBodyType = totemBodyFloorItem.getTotemType();
            final TotemColor totemBodyColorType = totemBodyFloorItem.getTotemColor();

            // find combinations then give the effect depending on it!

            int effectId = 0;

            if (totemPlanetType == TotemPlanetType.SUN && totemHeadType == TotemType.BIRD && totemBodyType == TotemType.BIRD && totemBodyColorType == TotemColor.RED) {
                effectId = 25;
            }
            else if (totemPlanetType == TotemPlanetType.EARTH && totemHeadType == TotemType.TROLL && totemBodyType == TotemType.TROLL && totemBodyColorType == TotemColor.YELLOW) {
                effectId = 23;
            }
            else if (totemPlanetType == TotemPlanetType.EARTH && totemHeadType == TotemType.SNAKE && totemBodyType == TotemType.BIRD && totemBodyColorType == TotemColor.YELLOW) {
                effectId = 26;
            }
            else if (totemPlanetType == TotemPlanetType.MOON && totemHeadType == TotemType.SNAKE && totemBodyType == TotemType.SNAKE && totemBodyColorType == TotemColor.BLUE) {
                effectId = 24;
            }

            //check if founded the right combination
            if (effectId > 0) {
                //some checks to avoid any error in-game
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                if (playerEntity.getCurrentEffect() != null) {
                    if (playerEntity.getGameTeam() != null && playerEntity.getGameTeam() != GameTeam.NONE) {
                        return false;
                    }

                    if (entity.getCurrentEffect().isItemEffect()) {
                        return false;
                    }
                }

                entity.applyEffect(new PlayerEffect(effectId, 0));
            }
        } else {
            this.toggleInteract(true);

            this.sendUpdate();
            this.saveData();
        }

        return true;
    }

    protected boolean isComplete() {
        boolean hasHead = (this instanceof TotemHeadFloorItem);
        boolean hasBody = (this instanceof TotemBodyFloorItem);
        boolean hasPlanet = (this instanceof TotemPlanetFloorItem);

        for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
            if (floorItem instanceof TotemHeadFloorItem) hasHead = true;
            if (floorItem instanceof TotemBodyFloorItem) hasBody = true;
            if (floorItem instanceof TotemPlanetFloorItem) hasPlanet = true;
        }

        return hasHead && hasBody && hasPlanet;
    }

    private RoomItemFloor getTotemPart(final boolean isHead) {
        for (final RoomItemFloor floorItem : this.getItemsOnStack()) {
            if ((floorItem instanceof TotemHeadFloorItem && isHead) || (floorItem instanceof TotemBodyFloorItem && !isHead))
                return floorItem;
        }

        return null;
    }
}
