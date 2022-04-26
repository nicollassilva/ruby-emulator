package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.room.action.ShoutMessageEvent;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.RandomUtil;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class RobCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.user.invalid", "Invalid username!"), client);
            return;
        }
        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());

        String robbedPlayer = params[0];

        Session robbedSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(robbedPlayer);

        if(robbedSession == null) {
            client.getPlayer().getSession().send(new TalkMessageComposer(-1, "El usuario en este momento no está conectado", ChatEmotion.NONE, 1));
            return;
        }

        if(robbedSession.getPlayer().getEntity() == null) {
            client.getPlayer().getSession().send(new TalkMessageComposer(-1, "Este usuario no está en la sala", ChatEmotion.NONE, 1));
            return;
        }

        if(timeSinceLastUpdate >= 30) {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "¡¡¡¡ESTO ES UN ASALTO LEVANTA LAS MANOS!!!", ChatEmotion.ANGRY, client.getPlayer().getSettings().getBubbleId()));
            client.getPlayer().getEntity().applyEffect(new PlayerEffect(10, 5));

            CometThreadManager.getInstance().executeSchedule(() -> {
                robbedSession.send(new TalkMessageComposer(robbedSession.getPlayer().getEntity().getId(), "¡¡NOO ME ROBES POR FAVOOOOR!!!", ChatEmotion.SHOCKED, robbedSession.getPlayer().getSettings().getBubbleId()));
                robbedSession.getPlayer().getEntity().applyEffect(new PlayerEffect(5, 5));
            }, 2, TimeUnit.SECONDS);
            client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
        } else {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Debes esperar 30 segundos para volver a ejecutar otro comando roleplay", ChatEmotion.NONE, 1));
            return;
        }
    }

    @Override
    public String getPermission() {
        return "rob_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuario)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.rob.description", "Roba a un usuario");
    }
}
