package com.cometproject.server.network.messages.incoming.user.details;

import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.game.players.components.types.settings.VolumeData;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox.ChangeVolumeComposer;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class UpdateAudioSettingsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null)
            return;

        final int systemVolume = msg.readInt();
        final int furniVolume = msg.readInt();
        final int traxVolume = msg.readInt();

        if (client.getPlayer().getSettings().getVolumes().getSystemVolume() == systemVolume
                && client.getPlayer().getSettings().getVolumes().getFurniVolume() == furniVolume
                && client.getPlayer().getSettings().getVolumes().getTraxVolume() == traxVolume) {
            return;
        }

        client.getPlayer().getSettings().getVolumes().setSystemVolume(systemVolume);
        client.getPlayer().getSettings().getVolumes().setFurniVolume(furniVolume);
        client.getPlayer().getSettings().getVolumes().setTraxVolume(traxVolume);

        PlayerDao.saveVolume(JsonUtil.getInstance().toJson(new VolumeData(systemVolume, furniVolume, traxVolume)), client.getPlayer().getId());
        client.send(new JavascriptCallbackMessageComposer(new ChangeVolumeComposer(traxVolume)));
    }
}
