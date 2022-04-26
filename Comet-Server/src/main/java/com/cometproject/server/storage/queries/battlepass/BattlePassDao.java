package com.cometproject.server.storage.queries.battlepass;

import com.cometproject.api.game.achievements.types.AchievementCategory;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.battlepass.types.BattlePassCategory;
import com.cometproject.api.game.battlepass.types.BattlePassType;
import com.cometproject.api.game.battlepass.types.IBattlePassHomework;
import com.cometproject.server.game.battlepass.BattlePassGroup;
import com.cometproject.server.game.battlepass.types.BattlePass;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BattlePassDao {
    public static int getHomeworks(Map<BattlePassType, IBattlePassHomework> battlePassHomeworks) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM battlepass WHERE enabled = '1' ORDER by group_name ASC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                count++;

                final BattlePassType groupName = BattlePassType.getTypeByName(resultSet.getString("group_name"));

                if (groupName == null) continue;

                if (!battlePassHomeworks.containsKey(groupName)) {
                    battlePassHomeworks.put(groupName, new BattlePassGroup(resultSet.getInt("id"), new HashMap<>(), resultSet.getString("group_name"), BattlePassCategory.valueOf(resultSet.getString("category").toUpperCase())));
                }

                if (!battlePassHomeworks.get(groupName).getBattlepass().containsKey(resultSet.getInt("level"))) {
                    battlePassHomeworks.get(groupName).getBattlepass().put(resultSet.getInt("level"), create(resultSet));
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return count;
    }

    private static BattlePass create(ResultSet resultSet) throws SQLException {
        return new BattlePass(resultSet.getInt("level"), resultSet.getInt("reward"), resultSet.getInt("xp_requirement"));
    }
}
