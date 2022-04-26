package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.storage.queries.player.SubscriptionDao;
import org.apache.commons.lang.NumberUtils;

public class BoxSubscriptionVipFloorItem extends RoomItemFloor {

    public BoxSubscriptionVipFloorItem(RoomItemData itemData, Room room) {
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
        int maxHits = 2;
        int timestamp = (int) Comet.getTime();
        long expire = Comet.getTime() + (26500 * 100);

        if (hits < maxHits) {
            hits++;
        } else {
            this.getItemData().setData(hits);
            this.sendUpdate();

            player.getSession().send(new TalkMessageComposer(((PlayerEntity) entity).getPlayer().getEntity().getId(), "Has abierto la caja de suscripción VIP y ya tienes una suscripción VIP vigente.", ChatEmotion.NONE, 34));
            entity.getRoom().getItems().removeItem(this, player.getSession(), false, true);
            //SubscriptionDao.addSubscription(timestamp, expire, player.getId());
            player.getData().setVip(true);
            player.getData().save();
        }

        this.getItemData().setData(hits);
        this.sendUpdate();

        return true;
    }
}
