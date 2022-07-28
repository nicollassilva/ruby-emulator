package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.filter.FilterResult;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastInviteFriends());
        if (timeSinceLastUpdate <= 300) {
            sendWhisper("Aguarde 5 minutos para que possa utilizar este comando novamente!", client);
            return;
        }

        final int timeMutedExpire = client.getPlayer().getData().getTimeMuted() - (int) Comet.getTime();

        if (client.getPlayer().getData().getTimeMuted() != 0) {
            if (client.getPlayer().getData().getTimeMuted() > (int) Comet.getTime()) {
                client.getPlayer().getSession().send(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.mute.muted", "You are muted for violating the rules! Your mute will expire in %timeleft% seconds").replace("%timeleft%", timeMutedExpire + "")));
                return;
            }
        }

        if (params.length != 1) {
            sendWhisper("Digite a mensagem que quer enviar para os seus amigos!", client);
            return;
        }

        String message = params[0];

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                filterResult.sendLogToStaffs(client, "<CommandInvitation>");
                client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                return;
            } else if (filterResult.wasModified()) {
                message = filterResult.getMessage();
            }
        }

        //Check if has friends before
        if (!client.getPlayer().getMessenger().getFriends().isEmpty()) {
            List<Integer> friends = new ArrayList<>();
            client.getPlayer().getMessenger().getFriends().keySet().forEach((key) -> {
                //Check if this user accepts invites and is online
                if (client.getPlayer().getMessenger().getFriendById(key).isOnline() && !PlayerDao.userIgnoreInvitation(key))
                    friends.add(key);
            });

            client.getPlayer().setLastInviteFriends(timeSinceLastUpdate);

            sendWhisper("Você acabou de mandar uma mensagem para todos os seus amigos online!", client);

            client.getPlayer().getMessenger().broadcast(friends, new NotificationMessageComposer("amigos", message));
        }
    }

    @Override
    public String getPermission() {
        return "invite_friends_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.message", "%mensagem%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.invite_friends.description");
    }
}
