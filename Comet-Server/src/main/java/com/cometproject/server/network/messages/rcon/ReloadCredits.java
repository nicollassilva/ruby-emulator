package com.cometproject.server.network.messages.rcon;

import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.rcon.utils.RCONMessage;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.google.gson.Gson;

public class ReloadCredits extends RCONMessage<ReloadCredits.ReloadCreditsJSON> {
    public ReloadCredits() {
        super(ReloadCreditsJSON.class);
    }

    @Override
    public void handle(Gson gson, ReloadCreditsJSON object) {
        Session client = NetworkManager.getInstance().getSessions().fromPlayer(object.user_id);

        if (client == null || client.getPlayer() == null) {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }

        if (client.getPlayer() != null) {
            PlayerDao.reloadPlayerCurrencies(client.getPlayer());
            client.getPlayer().sendBalance();
        }
    }

    static class ReloadCreditsJSON {
        public int user_id;
    }
}
