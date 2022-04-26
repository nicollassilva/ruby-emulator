package com.cometproject.server.game.rooms.objects.items.types.floor.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.JukeboxComposer;

public class YoutubeJukeboxFloorItem extends DefaultFloorItem {
    public YoutubeJukeboxFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public void onToggled(PlayerEntity playerEntity) {
        playerEntity.getPlayer().getSession().send(new JavascriptCallbackMessageComposer(new JukeboxComposer(this.getRoom().getYoutubeJukebox())));
    }
}
