package com.cometproject.server.network.messages.outgoing.user.profile;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.api.game.groups.types.IGroupData;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.messages.outgoing.user.details.UserObjectMessageComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.ArrayList;
import java.util.List;


public class LoadProfileMessageComposer extends MessageComposer {
    private final IPlayer player;
    private final boolean isMyFriend;
    private final boolean requestSent;

    public LoadProfileMessageComposer(IPlayer player, boolean isMyFriend, boolean hasSentRequest) {
        this.player = player;
        this.isMyFriend = isMyFriend;
        this.requestSent = hasSentRequest;
    }

    @Override
    public short getId() {
        return Composers.ProfileInformationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(player.getData().getId());
        msg.writeString(player.getData().getUsername());
        msg.writeString(player.getData().getFigure());
        msg.writeString(player.getData().getMotto());

        boolean isTimestamp = false;
        int timestamp = 0;

        try {
            timestamp = Integer.parseInt(player.getData().getRegDate());
            isTimestamp = true;
        } catch (Exception ignored) {
        }

        msg.writeString(isTimestamp ? UserObjectMessageComposer.getDate(timestamp) : player.getData().getRegDate());
        msg.writeInt(player.getData().getAchievementPoints());
        msg.writeInt(player.getStats().getFriendCount());
        msg.writeBoolean(isMyFriend);
        msg.writeBoolean(requestSent);
        msg.writeBoolean(PlayerManager.getInstance().isOnline(player.getData().getId()) && !player.getSettings().getHideOnline());

        final List<IGroupData> groups = new ArrayList<>();

        if (this.player.getGroups() != null) {
            for (final int groupId : this.player.getGroups()) {
                final IGroup group = GameContext.getCurrent().getGroupService().getGroup(groupId);

                if (group != null && group.getData() != null) {
                    groups.add(group.getData());
                }
            }
        }

        msg.writeInt(groups.size());

        for (final IGroupData group : groups) {
            if (group == null) continue;

            msg.writeInt(group.getId());
            msg.writeString(group.getTitle());
            msg.writeString(group.getBadge());
            msg.writeString(group.getColourA());
            msg.writeString(group.getColourB());
            msg.writeBoolean(player.getData().getFavouriteGroup() == group.getId());
            msg.writeInt(-1);
            msg.writeBoolean(group.hasForum());
        }

        groups.clear();

        msg.writeInt(PlayerManager.getInstance().isOnline(player.getData().getId()) ? (player.getSettings().getHideOnline() ? -1 : (int) Comet.getTime() - player.getData().getLastVisit()) : (int) Comet.getTime() - player.getData().getLastVisit());
        msg.writeBoolean(true);
    }
}
