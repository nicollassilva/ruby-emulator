package com.cometproject.server.storage.queries.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cometproject.server.storage.SqlHelper;


public class SlotMachineDao {
    public static int insertBet(int playerId, String type, String amount, String timestamp, String result) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO server_bets (`user_id`, `type`, `amount`, `timestamp`, `result`) VALUES(" +
                    "?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, amount);
            preparedStatement.setString(4, timestamp);
            preparedStatement.setString(5, result);

            preparedStatement.execute();

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
}
