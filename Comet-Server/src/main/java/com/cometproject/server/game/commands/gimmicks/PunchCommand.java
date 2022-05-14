package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class PunchCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.punch.none", "Em quem você quer dar um soco?"), client);
            return;
        }

        if (client.getPlayer().getEntity().isRoomMuted() || client.getPlayer().getEntity().getRoom().getRights().hasMute(client.getPlayer().getId())) {
            sendNotif(Locale.getOrDefault("command.user.muted", "Você está mutado."), client);
            return;
        }

        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());

        String punchedPlayer = params[0];
        Session punchedSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(punchedPlayer);

        if (punchedSession == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (punchedSession.getPlayer().getEntity() == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (punchedSession.getPlayer().getData().getUsername().equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.getOrDefault("command.punch.himself", "Você não pode dar um soco em si mesmo!"), client);
            return;
        }

        int posX = punchedSession.getPlayer().getEntity().getPosition().getX();
        int posY = punchedSession.getPlayer().getEntity().getPosition().getY();
        int playerX = client.getPlayer().getEntity().getPosition().getX();
        int playerY = client.getPlayer().getEntity().getPosition().getY();

            if (!((Math.abs((posX - playerX)) >= 2) || (Math.abs(posY - playerY) >= 2))) {
                if(timeSinceLastUpdate >= 30) {
                    client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "* " + client.getPlayer().getData().getUsername() + " " + Locale.getOrDefault("command.punch.word", "socou") + " " + punchedSession.getPlayer().getData().getUsername() + " *", 1));
                    punchedSession.getPlayer().getEntity().applyEffect(new PlayerEffect(157));
                    client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
                } else {
                    client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Você deve esperar 30 segundos para executar esse comando novamente.", ChatEmotion.NONE, 1));
                    return;
                }
            } else {
                client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.notaround", "Ops! %playername% não está próximo, caminhe até este jogador.").replace("%playername%", punchedSession.getPlayer().getData().getUsername()), 34));
            }
    }

    @Override
    public String getPermission() {
        return "punch_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.punch.description", "Dá um soco em um usuário");
    }
}
