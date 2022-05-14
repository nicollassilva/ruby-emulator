package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class HugCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Usuário inválido!"), client);
            return;
        }

        final int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());
        final String huggedPlayer = params[0];
        final RoomEntity entity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByName(huggedPlayer, RoomEntityType.PLAYER);

        if (entity == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (entity.getUsername().equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.getOrDefault("command.hug.himself", "Você não pode abraçar a si mesmo!"), client);
            return;
        }

        final int posX = entity.getPosition().getX();
        final int posY = entity.getPosition().getY();
        final int playerX = client.getPlayer().getEntity().getPosition().getX();
        final int playerY = client.getPlayer().getEntity().getPosition().getY();

        if (!((Math.abs((posX - playerX)) >= 2) || (Math.abs(posY - playerY) >= 2))) {
            if (timeSinceLastUpdate < 30) {
                client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Espere 30 segundos para abraçar novamente.", ChatEmotion.NONE, 1));
                return;
            }

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.hug.message", "abraçou %username%").replace("%username%", huggedPlayer), ChatEmotion.SMILE, 16));

            entity.applyEffect(new PlayerEffect(9));

            client.getPlayer().getEntity().applyEffect(new PlayerEffect(9));
            client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
        } else {
            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.notaround", "Ops! %playername% não está próximo, caminhe até este jogador.").replace("%playername%", entity.getUsername()), 16));
        }
    }

    @Override
    public String getPermission() {
        return "hug_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.hug.description", "Abraça um usuário");
    }
}
