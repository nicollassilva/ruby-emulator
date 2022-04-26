package com.cometproject.server.game.players;

import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.server.storage.SqlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerOfferPurchase implements IPlayerOfferPurchase {
    public static final Logger log = LogManager.getLogger(PlayerOfferPurchase.class.getName());
    private final int userId;
    private final int offerId;
    private int state;
    private int amount;
    private int lastPurchaseTimestamp;
    private boolean needsUpdate = false;

    public PlayerOfferPurchase(ResultSet set) throws SQLException {
        this.userId = set.getInt("player_id");
        this.offerId = set.getInt("offer_id");
        this.state = set.getInt("state");
        this.amount = set.getInt("amount");
        this.lastPurchaseTimestamp = set.getInt("last_purchase");
    }

    private PlayerOfferPurchase(int userId, int offerId) {
        this.userId = userId;
        this.offerId = offerId;
    }

    public int getOfferId() {
        return this.offerId;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
        this.needsUpdate = true;
        this.update();
    }

    public int getAmount() {
        return this.amount;
    }

    public int getUserId() {
        return this.userId;
    }

    public void incrementAmount(int amount) {
        this.amount += amount;
        this.needsUpdate = true;
        this.update();
    }

    public int getLastPurchaseTimestamp() {
        return this.lastPurchaseTimestamp;
    }

    public void setLastPurchaseTimestamp(int timestamp) {
        this.lastPurchaseTimestamp = timestamp;
        this.needsUpdate = true;
        this.update();
    }

    public void update(int amount, int timestamp) {
        this.amount += amount;
        this.lastPurchaseTimestamp = timestamp;
        this.needsUpdate = true;
        this.update();
    }

    public boolean needsUpdate() {
        return this.needsUpdate;
    }

    public static IPlayerOfferPurchase getOrCreate(IPlayer player, int offerId) {
        IPlayerOfferPurchase purchase = player.getOfferPurchase(offerId);

        if(purchase == null) {
            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;

            try {
                sqlConnection = SqlHelper.getConnection();

                preparedStatement = SqlHelper.prepare("INSERT INTO player_target_offer_purchases (player_id, offer_id) VALUES (?, ?)", sqlConnection);
                preparedStatement.setInt(1, player.getId());
                preparedStatement.setInt(2, offerId);
                preparedStatement.execute();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }

            purchase = new PlayerOfferPurchase(player.getId(), offerId);
            player.addOfferPurchase(purchase);
        }

        return purchase;
    }

    public void update() {
        if(!this.needsUpdate) return;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_target_offer_purchases SET state = ?, amount = ?, last_purchase = ? WHERE player_id = ? AND offer_id = ?", sqlConnection);
            preparedStatement.setInt(1, this.getState());
            preparedStatement.setInt(2, this.getAmount());
            preparedStatement.setInt(3, this.getLastPurchaseTimestamp());
            preparedStatement.setInt(4, this.getUserId());
            preparedStatement.setInt(5, this.getOfferId());
            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        this.needsUpdate = false;
    }
}
