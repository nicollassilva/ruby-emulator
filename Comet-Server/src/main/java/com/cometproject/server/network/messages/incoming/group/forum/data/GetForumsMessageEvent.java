package com.cometproject.server.network.messages.incoming.group.forum.data;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.api.game.groups.types.IGroupData;
import com.cometproject.server.composers.group.forums.GroupForumListMessageComposer;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

public class GetForumsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int mode = msg.readInt();
        final int offset = msg.readInt();
        final int amount = msg.readInt();

        Set<Integer> groupsId = null;
        final List<IGroup> groups = Lists.newArrayList();

        switch (mode) {
            case 0: // most active
                groupsId = GameContext.getCurrent().getGroupService().getActiveForums();
                break;

            case 1: // most viewed
                groupsId = GameContext.getCurrent().getGroupService().getMostViewed();
                break;

            case 2: // my groups
                groupsId = client.getPlayer().getGroups();
                break;
        }

        if(groupsId == null || groupsId.isEmpty()) {
            return;
        }

        for (int groupId : groupsId) {
            final IGroupData groupData = GameContext.getCurrent().getGroupService().getData(groupId);

            if (groupData != null && groupData.hasForum()) {
                final IGroup group = GameContext.getCurrent().getGroupService().getGroup(groupId);

                if (group.getForum() != null)
                    groups.add(group);
            }
        }

        client.send(new GroupForumListMessageComposer(groups, client.getPlayer().getId(), mode, offset));
    }
}
