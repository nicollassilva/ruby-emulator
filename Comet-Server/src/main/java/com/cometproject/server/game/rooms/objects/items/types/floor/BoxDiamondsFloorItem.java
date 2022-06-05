package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.utilities.RandomUtil;
import org.apache.commons.lang.math.NumberUtils;

public class BoxDiamondsFloorItem extends RoomItemFloor {

    public BoxDiamondsFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (!NumberUtils.isNumber(this.getItemData().getData()))
            this.getItemData().setData("0");
    }

    @Override
    public boolean onInteract(RoomEntity entity, int state, boolean isWiredTrigger) {
        if (isWiredTrigger || !(entity instanceof PlayerEntity)) {
            return false;
        }

        final Player player = ((PlayerEntity) entity).getPlayer();

        int hits = Integer.parseInt(this.getItemData().getData());
        int maxHits = 3;

        if (hits < maxHits) {
            hits++;
        } else {
            // we're open!
            this.getItemData().setData(hits);
            this.sendUpdate();

            int result = RandomUtil.getRandomInt(0, 10);

            ((PlayerEntity) entity).getPlayer().getSession().send(new TalkMessageComposer(((PlayerEntity) entity).getPlayer().getEntity().getId(), "Has abierto la caja de diamantes y has obtenido " + result + " diamantes", ChatEmotion.NONE, 34));
            entity.getRoom().getItems().removeItem(this, player.getSession(), false, true);
            ((PlayerEntity) entity).getPlayer().getData().increaseVipPoints(result);
            ((PlayerEntity) entity).getPlayer().getData().save();
            ((PlayerEntity) entity).getPlayer().sendBalance();
        }

        this.getItemData().setData(hits);
        this.sendUpdate();

        return true;
    }
}
