package com.cometproject.server.network.messages.incoming.group.settings;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.api.game.groups.types.components.membership.GroupAccessLevel;
import com.cometproject.api.game.groups.types.components.membership.IGroupMember;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class ModifyGroupTitleMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        final String title = msg.readString();
        final String description = msg.readString();

        if (!client.getPlayer().getGroups().contains(groupId))
            return;

        final IGroup group = GameContext.getCurrent().getGroupService().getGroup(groupId);

        if (group == null)
            return;

        final IGroupMember groupMember = group.getMembers().getAll().get(client.getPlayer().getId());

        if (groupMember.getAccessLevel() != GroupAccessLevel.OWNER)
            return;

        if(title.length() > 29 || description.length() > 120) {
            client.send(new NotificationMessageComposer("generic", "Não foi possível salvar dados do grupo: Limite de caracteres excedidos."));
            return;
        }

        group.getData().setTitle(title);
        group.getData().setDescription(description);

        GameContext.getCurrent().getGroupService().saveGroupData(group.getData());

        if (RoomManager.getInstance().isActive(group.getData().getRoomId())) {
            final Room room = RoomManager.getInstance().get(group.getData().getRoomId());

            room.getEntities().broadcastMessage(new RoomDataMessageComposer(room.getData()));
        }
    }
}
