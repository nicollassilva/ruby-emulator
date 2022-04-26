package com.cometproject.server.composers.catalog.data;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class CatalogOfferConfigMessageComposer extends MessageComposer {
    public static int MAXIMUM_ALLOWED_ITEMS = 100;
    public static int DISCOUNT_BATCH_SIZE = 6;
    public static int DISCOUNT_AMOUNT_PER_BATCH = 1;
    public static int MINIMUM_DISCOUNTS_FOR_BONUS = 1;
    public static int[] ADDITIONAL_DISCOUNT_THRESHOLDS = new int[]{40, 99};

    @Override
    public short getId() {
        return Composers.CatalogItemDiscountMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(MAXIMUM_ALLOWED_ITEMS);
        msg.writeInt(DISCOUNT_BATCH_SIZE);
        msg.writeInt(DISCOUNT_AMOUNT_PER_BATCH);
        msg.writeInt(MINIMUM_DISCOUNTS_FOR_BONUS);
        msg.writeInt(ADDITIONAL_DISCOUNT_THRESHOLDS.length);

        for(int threshold : ADDITIONAL_DISCOUNT_THRESHOLDS) {
            msg.writeInt(threshold);
        }
    }
}
