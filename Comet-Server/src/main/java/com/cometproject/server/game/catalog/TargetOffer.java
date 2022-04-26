package com.cometproject.server.game.catalog;

import com.cometproject.api.game.catalog.ITargetOffer;
import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TargetOffer implements ITargetOffer {
    public static int ACTIVE_TARGET_OFFER_ID = 0;

    private final int id;
    private final int catalogItem;
    private final String identifier;
    private final int priceInCredits;
    private final int priceInActivityPoints;
    private final int activityPointsType;
    private final int purchaseLimit;
    private final int expirationTime;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String icon;
    private final String[] vars;

    public TargetOffer(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.identifier = set.getString("offer_code");
        this.priceInCredits = set.getInt("credits");
        this.priceInActivityPoints = set.getInt("points");
        this.activityPointsType = set.getInt("points_type");
        this.title = set.getString("title");
        this.description = set.getString("description");
        this.imageUrl = set.getString("image");
        this.icon = set.getString("icon");
        this.purchaseLimit = set.getInt("purchase_limit");
        this.expirationTime = set.getInt("end_timestamp");
        this.vars = set.getString("vars").split(";");
        this.catalogItem = set.getInt("catalog_item");
    }

    public void serialize(IComposer message, IPlayerOfferPurchase purchase) {
        message.writeInt(purchase.getState());
        message.writeInt(this.id);
        message.writeString(this.identifier);
        message.writeString(this.identifier);
        message.writeInt(this.priceInCredits);
        message.writeInt(this.priceInActivityPoints);
        message.writeInt(this.activityPointsType);
        message.writeInt(Math.max(this.purchaseLimit - purchase.getAmount(), 0));
        message.writeInt(this.expirationTime - (int) Comet.getTime());
        message.writeString(this.title);
        message.writeString(this.description);
        message.writeString(this.imageUrl);
        message.writeString(this.icon);
        message.writeInt(0);
        message.writeInt(this.vars.length);

        for (String variable : this.vars) {
            message.writeString(variable);
        }
    }

    public int getId() {
        return this.id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getPriceInCredits() {
        return this.priceInCredits;
    }

    public int getPriceInActivityPoints() {
        return this.priceInActivityPoints;
    }

    public int getActivityPointsType() {
        return this.activityPointsType;
    }

    public int getPurchaseLimit() {
        return this.purchaseLimit;
    }

    public int getExpirationTime() {
        return this.expirationTime;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getIcon() {
        return this.icon;
    }

    public String[] getVars() {
        return this.vars;
    }

    public int getCatalogItem() {
        return this.catalogItem;
    }
}
