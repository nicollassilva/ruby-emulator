package com.cometproject.server.network.messages.outgoing.user.club;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.components.SubscriptionComponent;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class ClubStatusMessageComposer extends MessageComposer {
    private final SubscriptionComponent subscriptionComponent;
    private int responseType;

    public static int RESPONSE_TYPE_NORMAL = 0;
    public static int RESPONSE_TYPE_LOGIN = 1;
    public static int RESPONSE_TYPE_PURCHASE = 2; // closes the catalog after buying
    public static int RESPONSE_TYPE_DISCOUNT_AVAILABLE = 3;
    public static int RESPONSE_TYPE_CITIZENSHIP_DISCOUNT = 4;

    public ClubStatusMessageComposer(final SubscriptionComponent subscriptionComponent) {
        this.subscriptionComponent = subscriptionComponent;
    }

    public ClubStatusMessageComposer(final SubscriptionComponent subscriptionComponent, int responseType) {
        this.subscriptionComponent = subscriptionComponent;
        this.responseType = responseType;
    }

    @Override
    public short getId() {
        return Composers.ScrSendUserInfoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

        msg.writeString("habbo_club");

        if(!this.subscriptionComponent.isValid()) {
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeBoolean(false);
            msg.writeBoolean(false);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            return;
        }

        int days = 0;
        int minutes = 0;
        int timeRemaining = 0;
        int pastTimeAsHC = this.subscriptionComponent.getStartFromDao();

        if(this.subscriptionComponent.exists()) {
            timeRemaining = this.subscriptionComponent.getTimeLeft();
            days = (int) Math.floor(timeRemaining / 86400.0);
            minutes = (int) Math.ceil(timeRemaining / 60.0);

            if(days < 1 && minutes > 0) {
                days = 1;
            }
        }

        int responseType = ((this.responseType <= RESPONSE_TYPE_LOGIN) && timeRemaining > 0 && SubscriptionComponent.DISCOUNT_ENABLED && days <= SubscriptionComponent.DISCOUNT_DAYS_BEFORE_END) ? RESPONSE_TYPE_DISCOUNT_AVAILABLE : this.responseType;

        msg.writeInt(days); // daysToPeriodEnd
        msg.writeInt(0); // memberPeriods
        msg.writeInt(0); // periodsSubscribedAhead
        msg.writeInt(responseType); // responseType
        msg.writeBoolean(pastTimeAsHC > 0); // hasEverBeenMember
        msg.writeBoolean(true); // isVIP
        msg.writeInt(0); // pastClubDays
        msg.writeInt((int) Math.floor(pastTimeAsHC / 86400.0)); // pastVIPdays
        msg.writeInt(minutes); // minutesTillExpiration
        msg.writeInt(0); // minutesSinceLastModified
    }
}
