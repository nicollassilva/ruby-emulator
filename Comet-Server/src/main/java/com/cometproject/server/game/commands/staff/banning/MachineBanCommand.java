package com.cometproject.server.game.commands.staff.banning;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.webhooks.PunishmentWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;


public class MachineBanCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            sendNotif(Locale.getOrDefault("command.params.length", "Ops! Você fez algo errado!"), client);
            return;
        }

        final String username = params[0];
        int length = Integer.parseInt(params[1]);

        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable() || user.getPlayer().getPermissions().getRank().getId() >= client.getPlayer().getPermissions().getRank().getId()) {
            sendNotif(Locale.getOrDefault("command.user.notbannable", "Você não pode banir esse usuário!"), client);
            return;
        }

        final long expire = Comet.getTime() + (length * 3600L);

        final String uniqueId = user.getUniqueId();

        if (BanManager.getInstance().hasBan(uniqueId, BanType.MACHINE)) {
            sendNotif("ID da máquina: " + uniqueId + " já está banido.", client);
            return;
        }

        BanManager.getInstance().banPlayer(BanType.MACHINE, user.getUniqueId(), user.getPlayer().getEntity().getUsername(), length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId(), client.getPlayer().getEntity().getUsername(), (int) Comet.getTime(), user.getPlayer().getId());
        sendNotif("Usuário foi banido pela máquina (" + uniqueId + ")", client);

        user.disconnect("banned");

        PunishmentWebhook.sendMachineBan(client.getPlayer().getData().getUsername(), username, params.length > 2 ? this.merge(params, 2) : "Motivo não citado", length);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s baniu a máquina do usuário '%u'"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%u", username);
    }

    @Override
    public String getPermission() {
        return "machineban_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.ban", "(usuário) (tempo) (razão)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.machineban.description");
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
