package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class KillCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Usuário inválido!"), client);
            return;
        }

        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());

        String killedPlayer = params[0];

        RoomEntity entity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByName(killedPlayer, RoomEntityType.PLAYER);

        if (entity == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (entity.getUsername().equals(client.getPlayer().getData().getUsername())) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), " *" + Locale.getOrDefault("command.kill.self.message", "cometeu suicídio.") + " *", ChatEmotion.NONE, 0));
            client.getPlayer().getEntity().applyEffect(new PlayerEffect(133));
            return;
        }

        int posX = entity.getPosition().getX();
        int posY = entity.getPosition().getY();
        int playerX = client.getPlayer().getEntity().getPosition().getX();
        int playerY = client.getPlayer().getEntity().getPosition().getY();

            if (!((Math.abs((posX - playerX)) >= 2) || (Math.abs(posY - playerY) >= 2))) {
                if(timeSinceLastUpdate >= 30) {
                    client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), ("* " + Locale.getOrDefault("command.kill.message", "matou %username%") + " *").replace("%username%", killedPlayer), ChatEmotion.ANGRY, 0));
                    entity.applyEffect(new PlayerEffect(133));
                    client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
                } else {
                    client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Você deve aguardar 30 segundos para executar o comando novamente.", ChatEmotion.NONE, 1));
                    return;
                }
            } else {
                client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.notaround", "Ops! %playername% não está próximo, caminhe até este jogador.").replace("%playername%", entity.getUsername()), 16));
        }
    }

    @Override
    public String getPermission() {
        return "kill_command";
    }

    @Override
    public String getParameter() {
        return "(usuário)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.kill.description", "Mata um usuário");
    }
}
