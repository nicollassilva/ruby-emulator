package com.cometproject.server.storage.queries.rooms;

import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.objects.IRoomItemData;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.storage.SqlHelper;
import com.cometproject.server.storage.queue.items.containers.PlaceWallItemContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;


public class RoomItemDao {

    private static final Logger log = LogManager.getLogger(RoomItemDao.class.getName());

    public static void deleteItem(long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM items WHERE id = ?", sqlConnection);
            preparedStatement.setLong(1, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void removeItemFromRoom(long itemId, int userId, String finalState) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET room_id = 0, user_id = ?, x = 0, y = 0, z = 0, wall_pos = '', extra_data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, finalState);
            preparedStatement.setLong(3, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void removeItemBatch(final Set<IRoomItemData> itemsToStore) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("UPDATE items SET room_id = 0, user_id = ?, x = 0, y = 0, z = 0, wall_pos = '', extra_data = ? WHERE id = ?", sqlConnection);
            for (IRoomItemData roomItem : itemsToStore) {
                preparedStatement.setInt(1, roomItem.getOwnerId());
                preparedStatement.setString(2, roomItem.getData());
                preparedStatement.setLong(3, roomItem.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveItemDataBatch(final Set<IRoomItemData> itemsToStore) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            // sqlConnection.setAutoCommit(false); ??

            preparedStatement = SqlHelper.prepare("UPDATE items SET extra_data = ? WHERE id = ?", sqlConnection);

            for (IRoomItemData roomItem : itemsToStore) {
                preparedStatement.setString(1, (roomItem instanceof RoomItemFloor) ? ((RoomItemFloor) roomItem).getDataObject() : roomItem.getData());
                preparedStatement.setLong(2, roomItem.getId());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            //    sqlConnection.commit(); ??
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveItemBatch(final Set<IRoomItemData> items) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            //  sqlConnection.setAutoCommit(false); ??

            preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ?, extra_data = ? WHERE id = ?", sqlConnection);

            for (IRoomItemData roomItem : items) {

                preparedStatement.setInt(1, roomItem.getPosition().getX());
                preparedStatement.setInt(2, roomItem.getPosition().getY());
                preparedStatement.setDouble(3, roomItem.getPosition().getZ());
                preparedStatement.setInt(4, roomItem.getRotation());
                preparedStatement.setString(5, (roomItem instanceof RoomItemFloor) ? ((RoomItemFloor) roomItem).getDataObject() : roomItem.getData());
                preparedStatement.setLong(6, roomItem.getId());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            // sqlConnection.commit();  ??
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void placeWallItemBatch(Set<PlaceWallItemContainer> wallItems) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("UPDATE items SET room_id = ?, wall_pos = ?, extra_data = ? WHERE id = ?", sqlConnection);
            for (PlaceWallItemContainer wallItem : wallItems) {
                preparedStatement.setInt(1, wallItem.getRoomId());
                preparedStatement.setString(2, wallItem.getWallPos());
                preparedStatement.setString(3, wallItem.getData());
                preparedStatement.setLong(4, wallItem.getItemId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updateItem(PlayerItem roomItem) {
        Connection sqlConnection = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            try (PreparedStatement statement = SqlHelper.prepare("UPDATE items SET user_id = ?, wall_pos = ?, x = ?, y = ?, z = ?, rot = ? WHERE id = ?", sqlConnection)) {
                statement.setInt(1, -1);
                statement.setString(2, "");
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setDouble(5, 0);
                statement.setInt(6, 0);
                statement.setInt(7, (int) roomItem.getId());
                statement.execute();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updateItem(PlayerItem roomItem, IPlayer player) {
        Connection sqlConnection = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            try (PreparedStatement statement = SqlHelper.prepare("UPDATE items SET user_id = ?, wall_pos = ?, x = ?, y = ?, z = ?, rot = ? WHERE id = ?", sqlConnection)) {
                statement.setInt(1, player.getId());
                statement.setString(2, "");
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setDouble(5, 0);
                statement.setInt(6, 0);
                statement.setInt(7, (int) roomItem.getId());
                statement.execute();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void placeFloorItem(long roomId, int x, int y, double height, int rot, String data, long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ?, room_id = ?, extra_data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setDouble(3, height);
            preparedStatement.setInt(4, rot);
            preparedStatement.setLong(5, roomId);
            preparedStatement.setString(6, data);
            preparedStatement.setLong(7, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
