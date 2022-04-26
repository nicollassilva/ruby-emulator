package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.RandomUtil;

import java.util.concurrent.TimeUnit;

public class SmokeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.smoke.buy", "¿Quieres comprar 1 gramo de marihuana? dí :fumar yes"), client);
            return;
        }

        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());

        // client.getPlayer().getData().decreaseActivityPoints(10);
        sendNotif("Haz comprado 1 gramo de marihuana!", client);

        if(timeSinceLastUpdate >= 30) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat1", "* Hace un porro *"), ChatEmotion.NONE, 0));
            CometThreadManager.getInstance().executeSchedule(() -> {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat2", "* Echa un jalón del porro *"), ChatEmotion.NONE, 0));
                client.getPlayer().getEntity().applyEffect(new PlayerEffect(53));
            }, 2, TimeUnit.SECONDS);

            CometThreadManager.getInstance().executeSchedule(() -> {
                switch (RandomUtil.getRandomInt(1, 4)) {
                    case 1: {
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat3", "* Estoy volando por el cielo >:D *"), ChatEmotion.NONE, 0));

                        break;
                    }
                    case 2: {
                        client.getPlayer().getEntity().applyEffect(new PlayerEffect(70));
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat3", "* Esa fue una buena hierba *"), ChatEmotion.NONE, 0));
                        break;
                    }
                    case 3: {
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat4", "* He llegado a ver pandas de color rosa *"), ChatEmotion.NONE, 0));
                        break;
                    }
                    default: {
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat5", "* Es la marihuana más buena que he consumido *"), ChatEmotion.NONE, 0));
                    }
                }
            }, 4, TimeUnit.SECONDS);

            CometThreadManager.getInstance().executeSchedule(() -> {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat6", "* Necesito más *"), ChatEmotion.NONE, 0));
                client.getPlayer().getEntity().applyEffect(new PlayerEffect(0));
            }, 6, TimeUnit.SECONDS);

            client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
        } else {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Debes esperar 30 segundos para volver a ejecutar otro comando roleplay", ChatEmotion.NONE, 1));
            return;
        }
    }

    @Override
    public String getPermission() {
        return "smoke_command";
    }

    @Override
    public String getParameter() {
        return "yes";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.smoke.description", "Fuma un porro");
    }
}
