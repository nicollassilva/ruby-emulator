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
            sendNotif(Locale.getOrDefault("command.smoke.buy", "Quer comprar 1g de maconha? Diga :fumar sim"), client);
            return;
        }

        int timeSinceLastUpdate = ((int) Comet.getTime() - client.getPlayer().getLastCommandRoleplay());

        // client.getPlayer().getData().decreaseActivityPoints(10);
        sendNotif("Comprou 1g da verdinha", client);

        if(timeSinceLastUpdate >= 30) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat1", "* Faz um baseado *"), ChatEmotion.NONE, 43));
            CometThreadManager.getInstance().executeSchedule(() -> {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat2", "* Dê um puxão da articulação *"), ChatEmotion.NONE, 43));
                client.getPlayer().getEntity().applyEffect(new PlayerEffect(53));
            }, 2, TimeUnit.SECONDS);

            CometThreadManager.getInstance().executeSchedule(() -> {
                switch (RandomUtil.getRandomInt(1, 4)) {
                    case 1: {
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat3", "* Estou voando pelo céu >:D *"), ChatEmotion.NONE, 43));

                        break;
                    }
                    case 2: {
                        client.getPlayer().getEntity().applyEffect(new PlayerEffect(70));
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat3", "* Isso era uma boa erva *"), ChatEmotion.NONE, 43));
                        break;
                    }
                    case 3: {
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat4", "* Eu vi pandas cor de rosa *"), ChatEmotion.NONE, 43));
                        break;
                    }
                    default: {
                        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat5", "* É a melhor erva que já usei *"), ChatEmotion.NONE, 43));
                    }
                }
            }, 4, TimeUnit.SECONDS);

            CometThreadManager.getInstance().executeSchedule(() -> {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.smoke.chat6", "* Preciso de mais *"), ChatEmotion.NONE, 43));
                client.getPlayer().getEntity().applyEffect(new PlayerEffect(0));
            }, 6, TimeUnit.SECONDS);

            client.getPlayer().setLastCommandRoleplay(timeSinceLastUpdate);
        } else {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), "Você deve esperar 30 segundos para executar esse comando novamente.", ChatEmotion.NONE, 1));
            return;
        }
    }

    @Override
    public String getPermission() {
        return "smoke_command";
    }

    @Override
    public String getParameter() {
        return "sim";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.smoke.description", "Fuma um baseado");
    }
}
