package com.cometproject.server.storage.queries.gamecenter;

import com.cometproject.server.game.players.data.GamePlayer;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GameDao {
    public static List<GamePlayer> getLeaderBoard(int gameId, Boolean isCurrentWeek, List<GamePlayer> gamePlayersList) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM gamecenter_leaderboard WHERE game_id = ? AND (timestamp >= ? AND timestamp <= ?)", sqlConnection);
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, (int) getMondayOfCurrentWeek().toEpochDay());
            preparedStatement.setInt(3, (int) getSundayOfCurrentWeek().toEpochDay());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                gamePlayersList.add(
                        new GamePlayer(
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getString("figure"),
                                resultSet.getString("gender"),
                                resultSet.getInt("points"),
                                resultSet.getInt("game_id")
                        ));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return new ArrayList<>();
    }

    public static LocalDate getMondayOfCurrentWeek() {
        // Go backward to get Monday
        LocalDate monday = LocalDate.now();
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }

        return monday;
    }

    public static LocalDate getSundayOfCurrentWeek() {
        // Go forward to get Sunday
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        return sunday;
    }
}
