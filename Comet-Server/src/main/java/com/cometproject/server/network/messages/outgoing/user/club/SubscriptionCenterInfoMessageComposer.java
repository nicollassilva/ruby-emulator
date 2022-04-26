package com.cometproject.server.network.messages.outgoing.user.club;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.components.SubscriptionComponent;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SubscriptionCenterInfoMessageComposer extends MessageComposer {
    private final SubscriptionComponent subscriptionComponent;

    public SubscriptionCenterInfoMessageComposer(final SubscriptionComponent subscriptionComponent) {
        this.subscriptionComponent = subscriptionComponent;
    }

    @Override
    public short getId() {
        return Composers.ClubCenterDataMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        int timeLeft = subscriptionComponent.getExpire() - (int) Comet.getTime();
        long timeLeftLong = (subscriptionComponent.getExpire() - (int) Comet.getTime()) * 1000L;
        int days = (timeLeft / 86400);

        msg.writeInt(days); // días seguidos suscrito
        msg.writeString(new SimpleDateFormat("dd-MM-yyyy").format(new Date(subscriptionComponent.getStart() * 1000L))); // fecha de suscripción
        msg.writeDouble( 50.0 * 0.1);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);

        msg.writeInt(0); // créditos gastados
        msg.writeInt(0); // premio a recibir
        msg.writeInt(0); // créditos gastados?
        msg.writeInt(0); // siguiente paga en minutos
    }
}