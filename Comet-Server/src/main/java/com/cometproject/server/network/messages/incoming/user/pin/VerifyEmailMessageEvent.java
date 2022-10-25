package com.cometproject.server.network.messages.incoming.user.pin;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.handshake.HomeRoomMessageComposer;
import com.cometproject.server.network.messages.outgoing.messenger.InstantChatMessageComposer;
import com.cometproject.server.network.messages.outgoing.moderation.ModToolMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.SMSVerificationCompleteMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class VerifyEmailMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client == null || client.getPlayer() == null || client.getPlayer().getData() == null || !client.getPlayer().getPermissions().getRank().modTool())
            return;

        final String email = msg.readString();

        if(email.equalsIgnoreCase(client.getPlayer().getSettings().getPersonalPin().toLowerCase())) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.success", "Acabas de introducir tu pin correctamente, ¡disfruta de tu sesión!"));
            client.getPlayer().getSettings().setPinSucces();
            client.send(new ModToolMessageComposer());
            client.send(new SMSVerificationCompleteMessageComposer(2,2));
            client.send(new HomeRoomMessageComposer(client.getPlayer().getSettings().getHomeRoom(), client.getPlayer().getSettings().getHomeRoom()));

            return;
        }

        if(client.getPlayer().getSettings().getPinTries() >= 2) {
            for (final Session player : ModerationManager.getInstance().getLogChatUsers()) {
                player.send(new InstantChatMessageComposer(
                        Locale.getOrDefault("pin.code.failed_attemps", "%user% ha fallado tres veces seguidas su pin y ha sido desconectado.")
                                .replace("%user%", client.getPlayer().getData().getUsername()
                                ), Integer.MAX_VALUE - 1)
                );
            }

            client.disconnect();
        }

        else {
            client.getPlayer().getSettings().incrementPinTries();
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.error", "Vaya... parece que este no es tu pin."));
            client.send(new EmailVerificationWindowMessageComposer(1,1));
        }
    }
}
