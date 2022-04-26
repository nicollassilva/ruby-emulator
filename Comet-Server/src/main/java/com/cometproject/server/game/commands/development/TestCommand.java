package com.cometproject.server.game.commands.development;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class TestCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        /*client.send(new ConfirmableAlertMessageComposer(client.getPlayer().getData().getUsername(), 1, false));
        client.getPlayer().setShadow(1);*/
        //client.send(new SeasonalCalendarMessageComposer(Integer.parseInt(params[0])));
        /*client.send(new UpdateActivityPointsMessageComposer(6000, 6000, 0));
        client.send(new ConfirmableAlertMessageComposer(client.getPlayer().getData().getUsername(), 1, false));*/
        //client.getPlayer().getData().increaseAchievementPoints(100);

        /*if(params[0].equals("body")){
            client.getPlayer().getEntity().setBodyRotating(true);
        } else {
            client.getPlayer().getEntity().setHeadRotating(true);
        }*/
        //WebSocketSessionManager.getInstance().sendMessage(client.getPlayer().getSession().getWsChannel(), new BattlePassWebPacket("sendBattlePass", client.getPlayer().getData().getFigure(), client.getPlayer().getData().getUsername(), client.getPlayer().getStats().getLevel(), client.getPlayer().getData().getXpPoints()));

        /*if(client.getPlayer().getRP().hasAttribute("death")){
            client.getPlayer().sendBubble("", "Tracked attribute");
            client.getPlayer().getRP().removeAttribute("death");
        } else {
            client.getPlayer().getRP().setAttribute("death", true);
        }*/
        int level = Integer.parseInt(params[0]);
        client.getPlayer().getData().increaseLevel(level);
        client.getPlayer().getData().save();
        client.getPlayer().getSession().send(new NotificationMessageComposer("generic", "Tienes el nivel " + client.getPlayer().getData().getLevel()));
    }

    @Override
    public String getPermission() {
        return "commands_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.test.description");
    }
}
