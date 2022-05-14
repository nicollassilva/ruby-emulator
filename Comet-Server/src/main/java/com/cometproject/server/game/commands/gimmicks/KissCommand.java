package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.PlayerAvatarActions;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class KissCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.kiss.none", "Quem você quer beijar?"), client);
            return;
        }

        if (client.getPlayer().getEntity().isRoomMuted() || client.getPlayer().getEntity().getRoom().getRights().hasMute(client.getPlayer().getId())) {
            sendNotif(Locale.getOrDefault("command.user.muted", "Você está mutado."), client);
            return;
        }

        int timeSinceLastUpdated = (int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay();
        String kissedPlayer = params[0];
        Session kissedSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(kissedPlayer);

        if (kissedSession == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (kissedSession.getPlayer().getEntity() == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (kissedSession.getPlayer().getData().getUsername().equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.getOrDefault("command.kiss.himself", "Você não pode beijar a si mesmo!"), client);
            return;
        }

        int posX = kissedSession.getPlayer().getEntity().getPosition().getX();
        int posY = kissedSession.getPlayer().getEntity().getPosition().getY();
        int playerX = client.getPlayer().getEntity().getPosition().getX();
        int playerY = client.getPlayer().getEntity().getPosition().getY();

        if (!((Math.abs((posX - playerX)) >= 2) || (Math.abs(posY - playerY) >= 2))) {
            if(timeSinceLastUpdated >= 30) {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "* " + client.getPlayer().getData().getUsername() + " " + Locale.getOrDefault("command.kiss.word", "beijou") + " " + kissedSession.getPlayer().getData().getUsername() + " *", ChatEmotion.NONE, 16));
                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new ActionMessageComposer(client.getPlayer().getEntity().getId(), PlayerAvatarActions.EXPRESSION_BLOW_A_KISS.getValue()));
                client.getPlayer().getEntity().applyEffect(new PlayerEffect(9));
                kissedSession.getPlayer().getEntity().applyEffect(new PlayerEffect(9));
            } else {
                client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Você deve esperar 30 segundos para executar esse comando novamente.", ChatEmotion.NONE, 1));
                return;
            }
        } else {
            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.notaround", "Ops! %playername% não está próximo, caminhe até este jogador.").replace("%playername%", kissedSession.getPlayer().getData().getUsername()), 16));
        }
    }

    @Override
    public String getPermission() {
        return "kiss_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.kiss.description", "Beija um usuário");
    }
}
