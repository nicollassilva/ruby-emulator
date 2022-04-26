package com.cometproject.server.storage.queries.battlepass;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.battlepass.types.BattlePassType;
import com.cometproject.api.game.players.data.components.achievements.IAchievementProgress;
import com.cometproject.api.game.players.data.components.battlepass.IBattlePassProgress;
import com.cometproject.server.game.players.components.types.achievements.AchievementProgress;
import com.cometproject.server.game.players.components.types.battlepass.BattlePassProgress;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerBattlePassDao {
    public static Map<BattlePassType, IBattlePassProgress> getBattlePassProgress(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<BattlePassType, IBattlePassProgress> battlepass = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `homework`, `level`, `progress` FROM `player_battlepass` WHERE `player_id` = ?", sqlConnection);

            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                battlepass.put(BattlePassType.getTypeByName(resultSet.getString("homework")), new BattlePassProgress(resultSet.getInt("level"), resultSet.getInt("progress")));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return battlepass;
    }

    public static void saveProgressBattlePass(int playerId, BattlePassType type, IBattlePassProgress progress) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into player_battlepass (`player_id`, `homework`, `level`, `progress`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE level = ?, progress = ?;", sqlConnection);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setString(2, type.getBattlePassHomework());
            preparedStatement.setInt(3, progress.getLevel());
            preparedStatement.setInt(4, progress.getProgress());
            preparedStatement.setInt(5, progress.getLevel());
            preparedStatement.setInt(6, progress.getProgress());

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
