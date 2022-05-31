package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import org.apache.commons.lang.StringUtils;


public class WiredCustomForwardRoom extends WiredActionItem {
    private static final long DELAY = 60 * 1000L; // 1 minuto

    public WiredCustomForwardRoom(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        if (this.getWiredData() == null || this.getWiredData().getText() == null) {
            return;
        }

        if (!StringUtils.isNumeric(this.getWiredData().getText()) || this.getWiredData().getText().isEmpty()) {
            return;
        }

        int roomId = Integer.parseInt(this.getWiredData().getText());

        if (playerEntity.getPlayer().getEntity().getRoom().getId() == roomId)
            return;

        final long diff = System.currentTimeMillis() - playerEntity.getPlayer().getLastForwardRoomRequest();
        if (DELAY > diff) {
            return;
        }

        playerEntity.getPlayer().setLastForwardRoomRequest(System.currentTimeMillis());
        if (playerEntity.getPlayer().getPermissions().getRank().modTool()) {
            playerEntity.getPlayer().bypassRoomAuth(true);
        }

        playerEntity.getPlayer().getSession().send(new RoomForwardMessageComposer(roomId));
    }
}
