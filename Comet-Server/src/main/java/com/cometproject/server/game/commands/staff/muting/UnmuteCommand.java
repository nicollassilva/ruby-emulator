package com.cometproject.server.game.commands.staff.muting;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class UnmuteCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.unmute.none", "Quem você quer desmutar?"), client);
            return;
        }

        final int playerId = PlayerManager.getInstance().getPlayerIdByUsername(params[0]);
        final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if (session == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        final int timeMuted = 0;

        session.send(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.unmute.unmuted", "Você foi desmutado.")));

        if (session.getPlayer().getData().getTimeMuted() > (int) Comet.getTime()) {
            PlayerDao.addTimeMute(playerId, timeMuted);
            session.getPlayer().getData().setTimeMuted(timeMuted);
            isExecuted(client);
        } else {
            final PlayerEntity entity = session.getPlayer().getEntity();

            if (entity != null && entity.isRoomMuted()) {
                entity.setRoomMuted(false);
            }

            isExecuted(client);
        }

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s desmutou '%u'"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%u", params[0]);
    }

    @Override
    public String getPermission() {
        return "unmute_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "%username%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.unmute.description");
    }

    @Override
    public boolean bypassFilter() {
        return true;
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return true;
    }
}
