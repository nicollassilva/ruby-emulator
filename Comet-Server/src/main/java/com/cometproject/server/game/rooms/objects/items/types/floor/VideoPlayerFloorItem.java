package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.common.YoutubeTVComposer;

public class VideoPlayerFloorItem extends DefaultFloorItem {
    public VideoPlayerFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void composeItemData(IComposer msg) {
        msg.writeInt(0);
        msg.writeInt(1);
        msg.writeInt(2);
        msg.writeString("THUMBNAIL_URL");
        msg.writeString(("/youtubeimager.php?video=%video%").replace("%video%", this.getItemData().getData() + ""));
        msg.writeString("videoId");
        msg.writeString(this.getItemData().getData());
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        YoutubeTVComposer msg = new YoutubeTVComposer(this.getItemData().getData() == null ? "" : this.getItemData().getData(),
                playerEntity.hasRights() || playerEntity.getPlayer().getPermissions().getRank().roomFullControl() ? this.getVirtualId() : 0);
        playerEntity.getPlayer().getSession().send(new JavascriptCallbackMessageComposer(msg));
        return true;
    }
}
