package com.cometproject.server.composers.group.forums;

import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.protocol.headers.Composers;

import java.util.Iterator;
import java.util.List;

public class GroupForumListMessageComposer extends MessageComposer {

    private final int mode;
    private final int index;
    private final List<IGroup> groups;
    private final int playerId;

    public GroupForumListMessageComposer(final int mode, final List<IGroup> groups, final int playerId) {
        this.mode = mode;
        this.groups = groups;
        this.playerId = playerId;
        this.index = 1;
    }

    public GroupForumListMessageComposer(final List<IGroup> groups, final int playerId, int mode, int index) {
        this.mode = mode;
        this.groups = groups;
        this.playerId = playerId;
        this.index = index;
    }

    @Override
    public short getId() {
        return Composers.ForumsListDataMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.mode);
        msg.writeInt(this.groups.size());
        msg.writeInt(this.index);

        Iterator<IGroup> it = this.groups.iterator();
        int count = Math.min(this.groups.size(), 20);

        msg.writeInt(count);

        for (int i = 0; i < index; i++) {
            if (!it.hasNext())
                break;

            it.next();
        }

        for (int i = 0; i < count; i++) {
            if (!it.hasNext())
                break;

            final IGroup group = it.next();

            if(group == null)
                continue;

            group.getForum().composeData(msg, group.getData());
        }
    }
}
