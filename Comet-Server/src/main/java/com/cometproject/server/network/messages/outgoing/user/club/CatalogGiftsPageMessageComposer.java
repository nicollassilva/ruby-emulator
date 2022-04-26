package com.cometproject.server.network.messages.outgoing.user.club;

import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.players.components.SubscriptionComponent;
import com.cometproject.server.protocol.messages.MessageComposer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;

public class CatalogGiftsPageMessageComposer
        extends MessageComposer {
    private final ICatalogPage catalogPage;
    private final SubscriptionComponent subscriptionComponent;
    private long max;
    private int dayOfMonth;
    private int timeLeft;

    public CatalogGiftsPageMessageComposer(ICatalogPage catalogPage, SubscriptionComponent subscriptionComponent) {
        this.catalogPage = catalogPage;
        this.subscriptionComponent = subscriptionComponent;
    }

    public short getId() {
        return 619;
    }

    public void compose(IComposer msg) {
        this.flushData();
        int size = this.catalogPage.getItems().size();
        msg.writeInt(this.timeLeft);
        msg.writeInt(this.subscriptionComponent.getPresents());
        msg.writeInt(size);
        for (ICatalogItem item : this.catalogPage.getItems().values()) {
            item.composeClubPresents(msg);
        }
        msg.writeInt(size);
        for (ICatalogItem item : this.catalogPage.getItems().values()) {
            item.serializeAvailability(msg);
        }
    }

    private void flushData() {
        LocalDateTime dateTime = LocalDateTime.now();
        Calendar cal = Calendar.getInstance();
        ChronoField chronoField = ChronoField.DAY_OF_MONTH;
        this.max = dateTime.range(chronoField).getMaximum();
        this.dayOfMonth = cal.get(5);
        this.timeLeft = (int)this.max - this.dayOfMonth;
    }
}

