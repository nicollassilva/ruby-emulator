package com.cometproject.server.storage.queries.moderation;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.moderation.types.Ban;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.storage.SqlHelper;
import com.cometproject.server.storage.queries.player.PlayerDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class BanDao {
    public static Map<String, Ban> getActiveBans() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, Ban> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM bans WHERE expire = 0 OR expire > " + Comet.getTime(), sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getString("data"), new Ban(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }

    public static int createBan(BanType type, long length, long expire, String data, String userBanned, int addedBy, String userAddedBan, int banTime, String reason, int userId) {

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into bans (`type`, `expire`, `data`, `user_banned`, `reason`, `added_by`, `user_addedban`, `ban_time`, `user_id`, `ip`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setString(1, type.toString().toLowerCase());
            preparedStatement.setLong(2, length == 0 ? 0 : expire);
            preparedStatement.setString(3, data);
            preparedStatement.setString(4, userBanned);
            preparedStatement.setString(5, reason);
            preparedStatement.setInt(6, addedBy);
            preparedStatement.setString(7, userAddedBan);
            preparedStatement.setInt(8, banTime);
            preparedStatement.setInt(9, userId);
            preparedStatement.setString(10, PlayerDao.getIpAddress(userId));

            SqlHelper.executeStatementSilently(preparedStatement, false);
            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return 0;
    }

    public static void deleteBan(String data) {

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM bans WHERE data = ?", sqlConnection, true);

            preparedStatement.setString(1, data);

            SqlHelper.executeStatementSilently(preparedStatement, false);
            resultSet = preparedStatement.getGeneratedKeys();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

    }
}
