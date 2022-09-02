package com.cometproject.server.game.commands.user;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.stats.CometStats;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.CometServer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.GameCycle;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.utilities.CometRuntime;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;


public class AboutCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] message) {
        final StringBuilder about = new StringBuilder();
        final NumberFormat format = NumberFormat.getInstance();
        final CometStats cometStats = Comet.getStats();

        final boolean aboutDetailed = client.getPlayer().getPermissions().getRank().aboutDetailed();
        final boolean aboutStats = client.getPlayer().getPermissions().getRank().aboutStats();

        if (CometSettings.aboutShowRoomsActive || CometSettings.aboutShowUptime || aboutDetailed) {
            about.append(Locale.getOrDefault("command.about.server_status", "<b>Server Status</b><br>"));

            if (CometSettings.aboutShowPlayersOnline || aboutDetailed)
                about.append(Locale.getOrDefault("command.about.users_online", "Users online: ")).append(format.format(cometStats.getPlayers())).append("<br>");

            if (CometSettings.aboutShowRoomsActive || aboutDetailed)
                about.append(Locale.getOrDefault("command.about.active_rooms", "Active rooms: ")).append(format.format(cometStats.getRooms())).append("<br>");

            if (CometSettings.aboutShowUptime || aboutDetailed)
                about.append(Locale.getOrDefault("command.about.uptime", "Uptime: ")).append(cometStats.getUptime()).append("<br>");

            about.append("<br>" + Locale.getOrDefault("command.about.credits_title", "<b>Agradecimentos:</b>") + "<br>Leon (Criador)<br>Djinn<br>Snaiker");

            about.append(Locale.getOrDefault("command.about.client_version", "Client version: ")).append(CometServer.CLIENT_VERSION).append("<br>");
        }

        // This will be visible to developers on the manager, no need to display it to the end-user.
        if (client.getPlayer().getPermissions().getRank().aboutDetailed()) {
            about.append(Locale.getOrDefault("command.about.server_info", "<br><b>Server Info</b><br>"));
            about.append(Locale.getOrDefault("command.about.allocated_memory", "Allocated memory: ") + format.format(cometStats.getAllocatedMemory()) + "MB<br>");
            about.append(Locale.getOrDefault("command.about.used_memory", "Used memory: ") + format.format(cometStats.getUsedMemory()) + "MB<br>");

            about.append(Locale.getOrDefault("command.about.proccess_id", "Process ID: ") + CometRuntime.processId + "<br>");
            about.append(Locale.getOrDefault("command.about.os", "OS: ") + cometStats.getOperatingSystem() + "<br>");
            about.append(Locale.getOrDefault("command.about.cpu_cores", "CPU cores:  ") + cometStats.getCpuCores() + "<br>");
            about.append(Locale.getOrDefault("command.about.threads", "Threads:  ") + ManagementFactory.getThreadMXBean().getThreadCount() + "<br>");
        }

        if (aboutStats) {
            about.append(Locale.getOrDefault("command.about.hotel_status", "<br><br><b>Hotel Stats</b><br>"));
            about.append(Locale.getOrDefault("command.about.online_record", "Online record: ")).append(GameCycle.getInstance().getOnlineRecord()).append("<br>");
            about.append(Locale.getOrDefault("command.about.last_reboot_record", "Record since last reboot: ")).append(GameCycle.getInstance().getCurrentOnlineRecord()).append("<br><br>");
        }


        client.send(new AdvancedAlertMessageComposer(
                Locale.getOrDefault("command.about.widget_title", "Comet Server"),
                about.toString(),
                Locale.getOrDefault("command.about.link_name", "www.cometproject.com"),
                Locale.getOrDefault("command.about.link_location", "https://www.cometproject.com"), CometSettings.aboutImg
        ));
    }

    @Override
    public String getPermission() {
        return "about_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.about.description", "Revisa la información del servidor.");
    }
}