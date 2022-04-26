package com.cometproject.server.game.catalog.marketplace;

import com.cometproject.api.game.catalog.marketplace.IMarketPlaceOffer;
import com.cometproject.api.game.catalog.marketplace.generic.IMarketPlaceState;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.ItemType;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.SqlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class MarketPlaceOffer implements IMarketPlaceOffer {
    private static final Logger log = LogManager.getLogger(CatalogManager.class.getName());

    public int avarage;
    public int count;
    private int offerId;
    private final FurnitureDefinition baseItem;
    private final long itemId;
    private int price;
    private int limitedStack;
    private int limitedNumber;
    private long timestamp = Comet.getTime();
    private int soldTimestamp = 0;
    private IMarketPlaceState state = MarketPlaceState.OPEN;
    private boolean needsUpdate = false;

    public MarketPlaceOffer(final ResultSet set, final boolean privateOffer) throws Exception {
        this.offerId = set.getInt("id");
        this.price = set.getInt("price");
        this.price = set.getInt("price");
        this.timestamp = set.getInt("timestamp");
        this.soldTimestamp = set.getInt("sold_timestamp");
        this.baseItem = ItemManager.getInstance().getDefinition(set.getInt("base_item_id"));
        this.state = MarketPlaceState.getType(set.getInt("state"));
        this.itemId = set.getInt("item_id");

        if (!set.getString("ltd_data").split(":")[1].equals("0")) {
            this.limitedNumber = Integer.parseInt(set.getString("ltd_data").split(":")[0]);
            this.limitedStack = Integer.parseInt(set.getString("ltd_data").split(":")[1]);
        }

        if (!privateOffer) {
            this.avarage = set.getInt("avg");
            this.count = set.getInt("number");
            this.price = set.getInt("minPrice");
        }
    }

    public MarketPlaceOffer(final PlayerItem item, final int price, final Session client) {
        this.price = price;
        this.baseItem = ItemManager.getInstance().getDefinition(item.getDefinition().getId());
        this.itemId = item.getId();

        if (item.getLimitedEditionItem() != null && item.getLimitedEditionItem().getLimitedRare() > 0) {
            this.limitedNumber = item.getLimitedEditionItem().getLimitedRare();
            this.limitedStack = item.getLimitedEditionItem().getLimitedRareTotal();
        }

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = sqlConnection.prepareStatement("INSERT INTO marketplace_items (item_id, user_id, price, timestamp, state) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, (int) item.getId());
            preparedStatement.setInt(2, client.getPlayer().getId());
            preparedStatement.setInt(3, this.price);
            preparedStatement.setInt(4, (int) this.timestamp);
            preparedStatement.setString(5, this.state.getState() + "");

            preparedStatement.execute();

            try (final ResultSet id = preparedStatement.getGeneratedKeys()) {
                while (id.next()) {
                    this.offerId = id.getInt(1);
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void insert(final MarketPlaceOffer offer, final Session client) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO marketplace_items VALUES (?, ?, ?, ?, ?, ?)", sqlConnection);

            preparedStatement.setInt(1, offer.getItemId());
            preparedStatement.setInt(2, client.getPlayer().getId());
            preparedStatement.setInt(3, offer.getPrice());
            preparedStatement.setInt(4, offer.getTimestamp());
            preparedStatement.setInt(5, offer.getSoldTimestamp());
            preparedStatement.setString(6, offer.getState().getState() + "");
            preparedStatement.execute();

            try (final ResultSet id = preparedStatement.getGeneratedKeys()) {
                while (id.next()) {
                    offer.setOfferId(id.getInt(1));
                }
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public int getOfferId() {
        return this.offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getItemId() {
        return this.baseItem.getSpriteId();
    }

    public int getPrice() {
        return this.price;
    }

    public IMarketPlaceState getState() {
        return this.state;
    }

    public void setState(IMarketPlaceState state) {
        this.state = state;
    }

    public int getTimestamp() {
        return (int) this.timestamp;
    }

    public int getCount() {
        return this.count;
    }

    public int getSoldTimestamp() {
        return this.soldTimestamp;
    }

    public void setSoldTimestamp(int soldTimestamp) {
        this.soldTimestamp = soldTimestamp;
    }

    public int getLimitedStack() {
        return this.limitedStack;
    }

    public int getLimitedNumber() {
        return this.limitedNumber;
    }

    public int getSoldItemId() {
        return (int) this.itemId;
    }

    public void needsUpdate(boolean value) {
        this.needsUpdate = value;
    }

    public int getType() {
        if (this.limitedStack > 0) {
            return 3;
        }

        String itemType = this.baseItem.getType();

        if (itemType.equals("")) {
            return 1;
        }

        return itemType.equals(ItemType.WALL.getType()) ? 2 : 1;
    }

    public int getAvarage() {
        return this.avarage;
    }

    public void updateOffer() {
        if (this.needsUpdate) {
            this.needsUpdate = false;

            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;

            try {
                sqlConnection = SqlHelper.getConnection();

                preparedStatement = SqlHelper.prepare("UPDATE marketplace_items SET state = ?, sold_timestamp = ? WHERE id = ?", sqlConnection);

                preparedStatement.setInt(1, this.state.getState());
                preparedStatement.setInt(2, this.soldTimestamp);
                preparedStatement.setInt(3, this.offerId);
                preparedStatement.execute();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }
    }
}
