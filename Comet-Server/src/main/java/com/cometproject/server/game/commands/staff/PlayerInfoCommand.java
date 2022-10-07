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
            userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.id", "ID")).append("</b>: ").append(playerData.getId()).append("<br>");
        }

        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.username", "Usuario")).append("</b>: ").append(playerData.getUsername()).append("<br>");
        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.motto", "Misión")).append("</b>: ").append(playerData.getMotto()).append("<br>");
        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.gender", "Genero")).append("</b>: ").append(playerData.getGender().equalsIgnoreCase("m") ? Locale.getOrDefault("command.playerinfo.male", "Homem") : Locale.getOrDefault("command.playerinfo.female", "Mulher")).append("<br>");
        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.status", "Estado")).append("</b>: ").append(session == null ? Locale.getOrDefault("command.playerinfo.offline", "Offline") : Locale.getOrDefault("command.playerinfo.online", "Online")).append("<br>");
        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.achievementPoints", "IAchievement Points")).append("</b>: ").append(playerData.getAchievementPoints()).append("<br>");
        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.vip", "VIP")).append("</b>: ").append(playerData.isVip()).append("<br>");

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.rank", "Rango")).append("</b>: ").append(playerData.getRank()).append("<br><br>");
        }

        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.currencyBalances", "Currency Balances")).append("</b><br>");
        userInfo.append("<i>").append(playerData.getCredits()).append(" ").append(Locale.getOrDefault("command.playerinfo.credits", "credits")).append("</i><br>");

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<i>").append(playerData.getVipPoints()).append(" ").append(Locale.getOrDefault("command.playerinfo.diamonds", "diamonds")).append("</i><br>");
        }

        userInfo.append("<i>").append(playerData.getActivityPoints()).append(" ").append(Locale.getOrDefault("command.playerinfo.activityPoints", "duckets")).append("</i><br>");

        userInfo.append("<i>").append(playerData.getSeasonalPoints()).append(" ").append(Locale.getOrDefault("command.playerinfo.seasonalPoints", "Calabazas")).append("</i><br><br>");

        userInfo.append("<i>").append(playerData.getPlayer().getStats().getLevel()).append(" ").append(Locale.getOrDefault("command.playerinfo.nivel", "Nivel:")).append("</i><br>");
        userInfo.append("<i>").append(playerData.getXpPoints()).append(" ").append(Locale.getOrDefault("command.playerinfo.xp", "XP")).append("</i><br><br>");


        userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.roomInfo", "Room Info")).append("</b><br>");

        if (session != null && session.getPlayer().getEntity() != null) {
            userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.roomId", "Room ID")).append("</b>: ").append(session.getPlayer().getEntity().getRoom().getData().getId()).append("<br>");
            userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.roomName", "Room Name")).append("</b>: ").append(session.getPlayer().getEntity().getRoom().getData().getName()).append("<br>");
            userInfo.append("<b>").append(Locale.getOrDefault("command.playerinfo.roomOwner", "Room Owner")).append("</b>: ").append(session.getPlayer().getEntity().getRoom().getData().getOwner()).append("<br>");
        } else {
            if (session == null)
                userInfo.append("<i>").append(Locale.getOrDefault("command.playerinfo.notOnline", "This player is not online!")).append("</i>");
            else
                userInfo.append("<i>").append(Locale.getOrDefault("command.playerinfo.notInRoom", "This player is not in a room!")).append("</i>");
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
