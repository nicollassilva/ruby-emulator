package com.cometproject.server.game.commands.staff;

import com.cometproject.api.game.players.data.IPlayerData;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.permissions.types.Rank;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class PlayerInfoCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) return;

        final String username = params[0];
        final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        PlayerData playerData;

        if (session == null || session.getPlayer() == null || session.getPlayer().getData() == null) {
            playerData = PlayerDao.getDataByUsername(username);
        } else {
            playerData = session.getPlayer().getData();
        }

        if (playerData == null) return;

        final Rank playerRank = PermissionsManager.getInstance().getRank(playerData.getRank());

        if (playerRank.modTool() && !client.getPlayer().getPermissions().getRank().modTool()) {
            // send player info failed alert
            client.send(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.playerinfo.title", "Player Information") + ": " + username, Locale.getOrDefault("command.playerinfo.staff", "You cannot view the information of a staff member!")));
            return;
        }

        final StringBuilder userInfo = new StringBuilder();

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.id", "ID") + "</b>: " + playerData.getId() + "<br>");
        }

        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.username", "Usuario") + "</b>: " + playerData.getUsername() + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.motto", "Misión") + "</b>: " + playerData.getMotto() + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.gender", "Genero") + "</b>: " + (playerData.getGender().equalsIgnoreCase("m") ? Locale.getOrDefault("command.playerinfo.male", "Homem") : Locale.getOrDefault("command.playerinfo.female", "Mulher")) + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.status", "Estado") + "</b>: " + (session == null ? Locale.getOrDefault("command.playerinfo.offline", "Offline") : Locale.getOrDefault("command.playerinfo.online", "Online")) + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.achievementPoints", "IAchievement Points") + "</b>: " + playerData.getAchievementPoints() + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.vip", "VIP") + "</b>: " + playerData.isVip() + "<br>");

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.rank", "Rango") + "</b>: " + playerData.getRank() + "<br><br>");
        }

        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.currencyBalances", "Currency Balances") + "</b><br>");
        userInfo.append("<i>" + playerData.getCredits() + " " + Locale.getOrDefault("command.playerinfo.credits", "credits") + "</i><br>");

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<i>" + playerData.getVipPoints() + " " + Locale.getOrDefault("command.playerinfo.diamonds", "diamonds") + "</i><br>");
        }

        userInfo.append("<i>" + playerData.getActivityPoints() + " " + Locale.getOrDefault("command.playerinfo.activityPoints", "duckets") + "</i><br>");

        userInfo.append("<i>" + playerData.getSeasonalPoints() + " " + Locale.getOrDefault("command.playerinfo.seasonalPoints", "Calabazas") + "</i><br><br>");

        userInfo.append("<i>" + playerData.getPlayer().getStats().getLevel() + " " + Locale.getOrDefault("command.playerinfo.nivel", "Nivel:") + "</i><br>");
        userInfo.append("<i>" + playerData.getXpPoints() + " " + Locale.getOrDefault("command.playerinfo.xp", "XP") + "</i><br><br>");


        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomInfo", "Room Info") + "</b><br>");

        if (session != null && session.getPlayer().getEntity() != null) {
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomId", "Room ID") + "</b>: " + session.getPlayer().getEntity().getRoom().getData().getId() + "<br>");
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomName", "Room Name") + "</b>: " + session.getPlayer().getEntity().getRoom().getData().getName() + "<br>");
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomOwner", "Room Owner") + "</b>: " + session.getPlayer().getEntity().getRoom().getData().getOwner() + "<br>");
        } else {
            if (session == null)
                userInfo.append("<i>" + Locale.getOrDefault("command.playerinfo.notOnline", "This player is not online!") + "</i>");
            else
                userInfo.append("<i>" + Locale.getOrDefault("command.playerinfo.notInRoom", "This player is not in a room!") + "</i>");
        }

        client.send(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.playerinfo.title", "Player Information") + ": " + username, userInfo.toString()));
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getPermission() {
        return "playerinfo_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "%username%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.playerinfo.description");
    }
}
