package com.cometproject.server.network.messages.incoming.group.settings;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.server.composers.group.GroupInformationMessageComposer;
import com.cometproject.server.composers.group.ManageGroupMessageComposer;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.groups.GroupFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.SendFloorItemMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.storage.api.StorageContext;


public class GroupUpdateColoursMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().antiSpam(this.getClass().getName(), 0.5)) {
            return;
        }

        final int groupId = msg.readInt();

        final IGroup group = GameContext.getCurrent().getGroupService().getGroup(groupId);

        if (group == null || client.getPlayer().getId() != group.getData().getOwnerId())
            return;

        final int colourA = msg.readInt();
        final int colourB = msg.readInt();

        group.getData().setColourA(colourA);
        group.getData().setColourB(colourB);

        StorageContext.getCurrentContext().getGroupRepository().saveGroupData(group.getData());

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            final Room room = client.getPlayer().getEntity().getRoom();

            for (final RoomItemFloor roomItemFloor : room.getItems().getByInteraction("group_%")) {
                if (roomItemFloor instanceof GroupFloorItem) {
                    room.getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(roomItemFloor.getVirtualId(), 0));
                    room.getEntities().broadcastMessage(new SendFloorItemMessageComposer(roomItemFloor));
                }
            }

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new RoomDataMessageComposer(room.getData()));

            client.send(new GroupInformationMessageComposer(group, GameContext.getCurrent().getRoomService().getRoomData(group.getData().getRoomId()), false,
                    client.getPlayer().getId() == group.getData().getOwnerId(), group.getMembers().getAdministrators().contains(client.getPlayer().getId()),
                    group.getMembers().getAll().containsKey(client.getPlayer().getId()) ? 1 : group.getMembers().getMembershipRequests().contains(client.getPlayer().getId()) ? 2 : 0, client.getPlayer().getData().getFavouriteGroup()));
        }

    }
}
