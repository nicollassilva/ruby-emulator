package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.google.common.collect.Lists;

import java.util.List;

public class PrivateChatFloorItem extends DefaultFloorItem {

    private final List<PlayerEntity> entities = Lists.newArrayList();

    public PrivateChatFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity) || this.entities.contains(entity)) return;

        entity.setPrivateChatItemId(this.getId());
        this.entities.add((PlayerEntity) entity);

        final PlayerEntity playerEntity = (PlayerEntity) entity;

        playerEntity.getPlayer().getSession().send(
                new NotificationMessageComposer("privatechat", "Você entrou em uma área de chat privado.")
        );
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) return;

        entity.setPrivateChatItemId(0);
        this.entities.remove(entity);

        final PlayerEntity playerEntity = (PlayerEntity) entity;

        playerEntity.getPlayer().getSession().send(
                new NotificationMessageComposer("privatechat", "Você saiu de uma área de chat privado.")
        );
    }

    public void broadcastMessage(MessageComposer msg) {
        for (final PlayerEntity playerEntity : this.entities) {
            playerEntity.getPlayer().getSession().send(msg);
        }
    }
}
