package com.cometproject.server.game.catalog.marketplace;

import com.cometproject.api.game.catalog.marketplace.IMarketPlaceOffer;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.webhooks.MarketPlaceWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.incoming.catalog.marketplace.MarketPlaceItemOfferedEvent;
import com.cometproject.server.network.messages.incoming.catalog.marketplace.MarketPlaceItemSoldEvent;
import com.cometproject.server.network.messages.incoming.catalog.marketplace.RequestOffersEvent;
import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceBuyErrorComposer;
import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceCancelSaleComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.PurchaseErrorMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.RemoveObjectFromInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.SqlHelper;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import gnu.trove.set.hash.THashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.guava.Sets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MarketPlace {
    private static final Logger log = LogManager.getLogger(CatalogManager.class.getName());

    public static THashSet<IMarketPlaceOffer> getOwnOffers(final IPlayer player) {
        final THashSet<IMarketPlaceOffer> offers = new THashSet<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT furniture.type AS type, items.base_item AS base_item_id, items.limited_data AS ltd_data, marketplace_items.* FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN furniture ON items.base_item = furniture.id WHERE marketplace_items.user_id = ?", sqlConnection);
            preparedStatement.setInt(1, player.getId());

            try (final ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    offers.add(new MarketPlaceOffer(set, true));
                }
            } catch (Exception e) {
                log.warn("MarketPlace ERROR: getOwnOffers");
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return offers;
    }

    public static void takeBackItem(Session client, int offerId) {
        IMarketPlaceOffer offer = client.getPlayer().getInventory().getOffer(offerId);

        if(offer == null) {
            return;
        }

        takeBackItem(client, offer);
    }

    private static void takeBackItem(Session client, IMarketPlaceOffer offer) {
        if(offer == null || !client.getPlayer().getInventory().getMarketplaceItems().contains(offer)) {
            return;
        }

        RequestOffersEvent.cachedResults.clear();

        Connection sqlConnection = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            try (PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT user_id FROM marketplace_items WHERE ID = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setInt(1, offer.getOfferId());

                try (ResultSet ownerSet = preparedStatement.executeQuery()) {
                    ownerSet.last();

                    if(ownerSet.getRow() == 0) {
                        return;
                    }

                    try (PreparedStatement statement = SqlHelper.prepare("DELETE FROM marketplace_items WHERE id = ? AND state != 2", sqlConnection)) {
                        statement.setInt(1, offer.getOfferId());
                        int count = statement.executeUpdate();

                        if(count != 0) {
                            client.getPlayer().getInventory().removeMarketplaceOffer(offer);

                            try (PreparedStatement updateItems = SqlHelper.prepare("UPDATE items SET user_id = ? WHERE id = ? LIMIT 1", sqlConnection)) {
                                updateItems.setInt(1, client.getPlayer().getId());
                                updateItems.setInt(2, offer.getSoldItemId());
                                updateItems.execute();

                                try (PreparedStatement selectItem = SqlHelper.prepare("SELECT i.*, ltd.limited_id, ltd.limited_total FROM items i LEFT JOIN items_limited_edition ltd ON ltd.item_id = i.id WHERE i.id = ?", sqlConnection)) {
                                    selectItem.setInt(1, offer.getSoldItemId());

                                    try (ResultSet set = selectItem.executeQuery()) {
                                        while (set.next()) {
                                            PlayerItem playerItem = new InventoryItem(set);

                                            RoomItemDao.updateItem(playerItem, client.getPlayer());

                                            final Set<PlayerItem> unseenItem = Sets.newHashSet();
                                            unseenItem.add(client.getPlayer().getInventory().add(set.getInt("id"), set.getInt("base_item"), "", null, playerItem.getLimitedEditionItem()));

                                            client.send(new MarketplaceCancelSaleComposer(offer, true));
                                            client.send(new UnseenItemsMessageComposer(unseenItem));
                                            client.send(new UpdateInventoryMessageComposer());

                                            unseenItem.clear();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static List<MarketPlaceOffer> getOffers(int minPrice, int maxPrice, String search, int sort) {
        final List<MarketPlaceOffer> offers = new ArrayList<>(10);

        RequestOffersEvent.cachedResults.clear();

        String query = "SELECT B.* FROM marketplace_items a INNER JOIN (SELECT b.base_item AS base_item_id, b.limited_data AS ltd_data, marketplace_items.*, AVG(price) as avg, MIN(marketplace_items.price) as minPrice, MAX(marketplace_items.price) as maxPrice, COUNT(*) as number, (SELECT COUNT(*) FROM marketplace_items c INNER JOIN items as items_b ON c.item_id = items_b.id WHERE state = 2 AND items_b.base_item = base_item_id AND DATE(from_unixtime(sold_timestamp)) = CURDATE()) as sold_count_today FROM marketplace_items INNER JOIN items b ON marketplace_items.item_id = b.id INNER JOIN furniture bi ON b.base_item = bi.id INNER JOIN catalog_items ci ON bi.id = ci.item_ids WHERE price = (SELECT MIN(e.price) FROM marketplace_items e, items d WHERE e.item_id = d.id AND d.base_item = b.base_item AND e.state = 1 AND e.timestamp > ? GROUP BY d.base_item) AND state = 1 AND timestamp > ?";

        if (minPrice > 0) {
            query += " AND CEIL(price + (price / 100)) >= " + minPrice;
        }

        if (maxPrice > 0 && maxPrice > minPrice) {
            query += " AND CEIL(price + (price / 100)) <= " + maxPrice;
        }

        if (search.length() > 0) {
            query += " AND bi.public_name LIKE ? OR ci.catalog_name LIKE ?";
        }

        query += " GROUP BY base_item_id, ltd_data) AS B ON a.id = B.id";

        switch (sort) {
            case 6:
                query += " ORDER BY number ASC";
                break;
            case 5:
                query += " ORDER BY number DESC";
                break;
            case 4:
                query += " ORDER BY sold_count_today ASC";
                break;
            case 3:
                query += " ORDER BY sold_count_today DESC";
                break;
            case 2:
                query += " ORDER BY minPrice ASC";
                break;
            default:
            case 1:
                query += " ORDER BY minPrice DESC";
                break;
        }

        query += " LIMIT 250";

        Connection sqlConnection = null;
        PreparedStatement preparedStatement;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare(query, sqlConnection);

            int marketTime = (int) (Comet.getTime() - 172800);

            preparedStatement.setInt(1, marketTime);
            preparedStatement.setInt(2, marketTime);

            if(search.length() > 0) {
                preparedStatement.setString(3, "%" + search + "%");
                preparedStatement.setString(4, "%" + search + "%");
            }

            try (final ResultSet set = preparedStatement.executeQuery()) {
                while (set.next()) {
                    offers.add(new MarketPlaceOffer(set, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }

        return offers;
    }

    public static void serializeItemInfo(int itemId, IComposer message) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = sqlConnection.prepareStatement("SELECT avg(marketplace_items.price) as price, COUNT(*) as sold, (datediff(NOW(), DATE(from_unixtime(marketplace_items.timestamp)))) as day FROM marketplace_items INNER JOIN items ON items.id = marketplace_items.item_id INNER JOIN furniture ON items.base_item = furniture.id WHERE marketplace_items.state = 2 AND furniture.sprite_id = ? AND DATE(from_unixtime(marketplace_items.timestamp)) >= NOW() - INTERVAL 30 DAY GROUP BY DATE(from_unixtime(marketplace_items.timestamp))", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setInt(1, itemId);

            message.writeInt(avarageLastXDays(itemId, 7));
            message.writeInt(itemsOnSale(itemId));
            message.writeInt(30);

            try (final ResultSet set = preparedStatement.executeQuery()) {
                set.last();
                message.writeInt(set.getRow());
                set.beforeFirst();

                while (set.next()) {
                    message.writeInt(-set.getInt("day"));
                    message.writeInt(MarketPlace.calculateCommision(set.getInt("price")));
                    message.writeInt(set.getInt("sold"));
                }
            }

            message.writeInt(1);
            message.writeInt(itemId);
        } catch (SQLException e) {
            log.error("Erro serializeItemInfo", e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int itemsOnSale(int baseItemId) {
        int number = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = sqlConnection.prepareStatement("SELECT COUNT(*) as number, AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN furniture ON items.base_item = furniture.id WHERE state = 1 AND timestamp >= ? AND furniture.sprite_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int marketTime = (int) (Comet.getTime() - 172800);

            preparedStatement.setInt(1, marketTime);
            preparedStatement.setInt(2, baseItemId);

            try (ResultSet set = preparedStatement.executeQuery()) {
                set.first();
                number = set.getInt("number");
            }
        } catch (SQLException e) {
            log.error("Erro itemsOnSale");
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }

        return number;
    }

    public static int avarageLastXDays(int baseItemId, int days) {
        int avg = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = sqlConnection.prepareStatement("SELECT AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN furniture ON items.base_item = furniture.id WHERE state = 2 AND DATE(from_unixtime(timestamp)) >= NOW() - INTERVAL ? DAY AND furniture.sprite_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            preparedStatement.setInt(1, days);
            preparedStatement.setInt(2, baseItemId);

            try (ResultSet set = preparedStatement.executeQuery()) {
                set.first();
                avg = set.getInt("avg");
            }
        } catch (SQLException e) {
            log.error("Erro avarageLastXDays");
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }

        return calculateCommision(avg);
    }

    public static void buyItem(int offerId, Session client) {
        RequestOffersEvent.cachedResults.clear();

        Connection sqlConnection = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            try (PreparedStatement statement = sqlConnection.prepareStatement("SELECT * FROM marketplace_items WHERE id = ? LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                statement.setInt(1, offerId);

                try (ResultSet set = statement.executeQuery()) {
                    if(set.next()) {
                        try (PreparedStatement itemStatement = sqlConnection.prepareStatement("SELECT i.*, ltd.limited_id, ltd.limited_total FROM items i LEFT JOIN items_limited_edition ltd ON ltd.item_id = i.id WHERE i.id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                            itemStatement.setInt(1, set.getInt("item_id"));

                            try (ResultSet itemSet = itemStatement.executeQuery()) {
                                itemSet.first();

                                if (itemSet.getRow() > 0) {
                                    final int price = MarketPlace.calculateCommision(set.getInt("price"));

                                    if(set.getInt("user_id") == client.getPlayer().getId()) {
                                        client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("marketplace.buy.own.item", "Você não pode comprar seu próprio item")));
                                        return;
                                    }

                                    if (set.getInt("state") != 1) {
                                        sendErrorMessage(client, itemSet.getInt("base_item"), offerId);
                                        return;
                                    }

                                    if(price > client.getPlayer().getData().getVipPoints()) {
                                        client.send(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.NOT_ENOUGH_CREDITS, 0, offerId, price));
                                        return;
                                    }

                                    try (PreparedStatement updateOffer = sqlConnection.prepareStatement("UPDATE marketplace_items SET state = 2, sold_timestamp = ? WHERE id = ?")) {
                                        updateOffer.setInt(1, (int) Comet.getTime());
                                        updateOffer.setInt(2, offerId);
                                        updateOffer.execute();
                                    }

                                    final PlayerData sellerPlayer = PlayerManager.getInstance().getDataByPlayerId(set.getInt("user_id"));
                                    final PlayerItem item = new InventoryItem(itemSet);

                                    final MarketPlaceItemSoldEvent event = new MarketPlaceItemSoldEvent(sellerPlayer.getPlayer(), client.getPlayer(), item, set.getInt("price"));
                                    event.price = calculateCommision(event.price);

                                    RoomItemDao.updateItem(item, client.getPlayer());

                                    client.getPlayer().getInventory().addItem(item);
                                    client.getPlayer().getData().decreaseVipPoints(event.price);
                                    client.getPlayer().getData().save();

                                    if(sellerPlayer.getPlayer() != null && sellerPlayer.getPlayer().isOnline()) {
                                        sellerPlayer.getPlayer().getInventory().getOffer(offerId).setState(MarketPlaceState.SOLD);
                                    }

                                    final Set<PlayerItem> unseenItem = Sets.newHashSet();
                                    unseenItem.add(client.getPlayer().getInventory().add(itemSet.getInt("id"), itemSet.getInt("base_item"), "", null, item.getLimitedEditionItem()));

                                    client.send(client.getPlayer().composeCurrenciesBalance());
                                    client.send(new UnseenItemsMessageComposer(unseenItem));
                                    client.send(new UpdateInventoryMessageComposer());
                                    client.send(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.REFRESH, 0, offerId, price));
                                    client.send(new NotificationMessageComposer(Locale.getOrDefault("image.sendnotif", "generic"), "Item comprado com sucesso!"));

                                    MarketPlaceWebhook.send(item, client.getPlayer().getData(), sellerPlayer, event.price);

                                    unseenItem.clear();
                                }
                            }
                        }
                    } else {
                        client.send(new PurchaseErrorMessageComposer(0));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Erro buyItem", e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void sendErrorMessage(Session client, int baseItemId, int offerId) {
        Connection sqlConnection = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            try (PreparedStatement statement = sqlConnection.prepareStatement("SELECT marketplace_items.*, COUNT( * ) AS count FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN furniture ON items.item_id = furniture.id WHERE furniture.sprite_id = ( SELECT furniture.sprite_id FROM furniture WHERE furniture.id = ? LIMIT 1) ORDER BY price ASC LIMIT 1")) {
                statement.setInt(1, baseItemId);

                try (ResultSet countSet = statement.executeQuery()) {
                    countSet.last();
                    if (countSet.getRow() == 0) {
                        client.send(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.SOLD_OUT, 0, offerId, 0));
                    } else {
                        countSet.first();
                        client.send(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.UPDATES, countSet.getInt("count"), countSet.getInt("id"), MarketPlace.calculateCommision(countSet.getInt("price"))));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Erro sendErrorMessage");
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static boolean sellItem(Session client, PlayerItem item, int price) {
        if (item == null || client == null)
            return false;

        FurnitureDefinition baseItem = ItemManager.getInstance().getDefinition(item.getDefinition().getId());

        if (!baseItem.canMarket() || price < 0)
            return false;

        MarketPlaceItemOfferedEvent event = new MarketPlaceItemOfferedEvent(client, item, price);

        RequestOffersEvent.cachedResults.clear();

        client.send(new RemoveObjectFromInventoryMessageComposer((int) event.item.getId()));
        client.send(new UpdateInventoryMessageComposer());

        MarketPlaceOffer offer = new MarketPlaceOffer(event.item, event.price, client);
        client.getPlayer().getInventory().addMarketplaceOffer(offer);
        client.getPlayer().getInventory().getInventoryItems().remove(event.item.getId());
        RoomItemDao.updateItem(item);

        return true;
    }

    public static void getCredits(Session client) {
        int credits = 0;

        THashSet<IMarketPlaceOffer> offers = new THashSet<>();
        offers.addAll(client.getPlayer().getInventory().getMarketplaceItems());

        for (IMarketPlaceOffer offer : offers) {
            if (offer.getState().equals(MarketPlaceState.SOLD)) {
                client.getPlayer().getInventory().removeMarketplaceOffer(offer);
                credits += offer.getPrice();
                removeUser(offer);
            }
        }

        client.getPlayer().getData().increaseVipPoints(credits);
        client.getPlayer().getData().save();
        client.send(client.getPlayer().composeCurrenciesBalance());
    }

    private static void removeUser(IMarketPlaceOffer offer) {
        Connection sqlConnection = null;
        PreparedStatement statement;

        try {
            sqlConnection = SqlHelper.getConnection();
            statement = SqlHelper.prepare("UPDATE marketplace_items SET user_id = ? WHERE id = ?", sqlConnection);

            statement.setInt(1, -1);
            statement.setInt(2, offer.getOfferId());
            statement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int calculateCommision(int price) {
        return price + (int) Math.ceil(price / 100.0);
    }
}
