package com.cometproject.server.network.messages.incoming.landing;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.landing.ConcurrentUsersCompetitionMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class ConcurrentUsersCompetitionClaimReward implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if(client.getPlayer().getSettings().verifyGoal())
            return;

        int current = NetworkManager.getInstance().getSessions().getUsersOnlineCount();
        int goal = CometSettings.cantityUsers;
        // TODO: DECLARE THE REWARDS

        if(current < goal) {
            client.send(new ConcurrentUsersCompetitionMessageComposer(1, current ,goal));
            return;
        }

        client.getPlayer().getSettings().setClaimedGoal(true);
        client.getPlayer().getInventory().addBadge(CometSettings.rewardConcurrentUsers, true, true);
        client.send(new ConcurrentUsersCompetitionMessageComposer(3, current ,goal));
        PlayerDao.saveClaimedReward(true, client.getPlayer().getId());
    }
}
