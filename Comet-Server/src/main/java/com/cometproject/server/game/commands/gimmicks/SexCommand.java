package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.tasks.CometThreadManager;

import java.util.concurrent.TimeUnit;

public class SexCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.sex.none", "Who do you want to rape?"), client);
            return;
        }

        if (client.getPlayer().getEntity().isRoomMuted() || client.getPlayer().getEntity().getRoom().getRights().hasMute(client.getPlayer().getId())) {
                sendNotif(Locale.getOrDefault("command.user.muted", "Estás muteado!"), client);
            return;
        }

        String sexedPlayer = params[0];
        Session sexedSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(sexedPlayer);
        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());

        if (sexedSession == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "El usuario está desconectado!"), client);
            return;
        }

        if (sexedSession.getPlayer().getEntity() == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "El usuario no está en la sala."), client);
            return;
        }

        if (sexedSession.getPlayer().getData().getUsername().equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.getOrDefault("command.sex.himself", "No puedes tener sexo contigo mismo!"), client);
            return;
        }

        int posX = sexedSession.getPlayer().getEntity().getPosition().getX();
        int posY = sexedSession.getPlayer().getEntity().getPosition().getY();
        int playerX = client.getPlayer().getEntity().getPosition().getX();
        int playerY = client.getPlayer().getEntity().getPosition().getY();
            if (!((Math.abs((posX - playerX)) >= 2) || (Math.abs(posY - playerY) >= 2))) {
                if(timeSinceLastUpdate >= 30) {

                    client.getPlayer().getEntity().applyEffect(new PlayerEffect(544));

                    if (client.getPlayer().getEntity().hasStatus(RoomEntityStatus.SIT)) {
                        client.getPlayer().getEntity().removeStatus(RoomEntityStatus.SIT);
                        client.getPlayer().getEntity().addStatus(RoomEntityStatus.LAY, "0.5");
                        client.getPlayer().getEntity().markNeedsUpdate();
                        isExecuted(client);
                    } else {
                        client.getPlayer().getEntity().addStatus(RoomEntityStatus.LAY, "0.5");
                        client.getPlayer().getEntity().markNeedsUpdate();
                        isExecuted(client);
                    }

                    sexedSession.getPlayer().getEntity().applyEffect(new PlayerEffect(502));

                    if (sexedSession.getPlayer().getEntity().hasStatus(RoomEntityStatus.LAY)) {
                        sexedSession.getPlayer().getEntity().removeStatus(RoomEntityStatus.LAY);
                        sexedSession.getPlayer().getEntity().addStatus(RoomEntityStatus.SIT, "0.5");
                        sexedSession.getPlayer().getEntity().markNeedsUpdate();
                    } else {
                        sexedSession.getPlayer().getEntity().addStatus(RoomEntityStatus.SIT, "0.5");
                        sexedSession.getPlayer().getEntity().markNeedsUpdate();
                    }

                    Room room = client.getPlayer().getEntity().getRoom();
                    room.getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "* " + Locale.getOrDefault("command.sex.word.1", " Gets horny and starts humping %username%").replace("%username%", sexedSession.getPlayer().getData().getUsername()) + " *", ChatEmotion.NONE, 16));
                    room.getEntities().broadcastMessage(new TalkMessageComposer(sexedSession.getPlayer().getEntity().getId(), " * " + Locale.getOrDefault("command.sex.word.2", " Give it to me harder baby").replace("%username%", client.getPlayer().getData().getUsername()) + " *", ChatEmotion.NONE, 16));
                    client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
                } else {
                    client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Debes esperar 30 segundos para volver a ejecutar otro comando roleplay", ChatEmotion.NONE, 1));
                    return;
                }
            } else {
                client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.notaround", "Oops! %playername% is not near, walk to this player.").replace("%playername%", sexedSession.getPlayer().getData().getUsername()), 16));
            }
    }

    @Override
    public String getPermission() {
        return "sex_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuario)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.sex.description", "Ten sexo con un usuario");
    }
}
