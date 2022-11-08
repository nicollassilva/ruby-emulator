package com.cometproject.server.game.commands.vip;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.types.EntityPathfinder;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.List;


public class PullCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Usuário inválido!"), client);
            return;
        }

        if (client.getPlayer().getEntity().isRoomMuted() || client.getPlayer().getEntity().getRoom().getRights().hasMute(client.getPlayer().getId())) {
            sendNotif(Locale.getOrDefault("command.user.muted", "Você está mutado."), client);
            return;
        }

        final String username = params[0];
        final Session pulledSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (pulledSession == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (pulledSession.getPlayer().getEntity() == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (username.equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.get("command.pull.playerhimself"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        final PlayerEntity pulledEntity = pulledSession.getPlayer().getEntity();

        if (pulledEntity.isOverriden()) {
            return;
        }

        if (pulledEntity.getPosition().distanceTo(client.getPlayer().getEntity().getPosition()) != 2) {
            client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.notaround", "Ops! %playername% não está próximo, caminhe até este jogador.").replace("%playername%", pulledEntity.getUsername()), 34));
            return;
        }

        final Position squareInFront = client.getPlayer().getEntity().getPosition().squareInFront(client.getPlayer().getEntity().getBodyRotation());

        if (room.getModel().getDoorX() == squareInFront.getX() && room.getModel().getDoorY() == squareInFront.getY()) {
            return;
        }

        pulledEntity.setWalkingGoal(squareInFront.getX(), squareInFront.getY());

        final List<Square> path = EntityPathfinder.getInstance().makePath(pulledEntity, pulledEntity.getWalkingGoal());
        pulledEntity.unIdle();

        if (pulledEntity.getWalkingPath() != null)
            pulledEntity.getWalkingPath().clear();

        pulledEntity.walkToPath(path);

        room.getEntities().broadcastMessage(
                new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.get("command.pull.message").replace("%playername%", pulledEntity.getUsername()), ChatEmotion.NONE, 0)
        );
    }


    @Override
    public String getPermission() {
        return "pull_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.pull.description", "Puxa um usuário para sua frente");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
