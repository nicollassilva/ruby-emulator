package com.cometproject.server.storage.queries.achievements;

import com.cometproject.api.game.achievements.types.AchievementCategory;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.talenttrack.ITalentTrackLevel;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.server.game.achievements.AchievementGroup;
import com.cometproject.server.game.achievements.TalentTrackLevel;
import com.cometproject.server.game.achievements.types.Achievement;
import com.cometproject.server.storage.SqlHelper;
import gnu.trove.map.hash.THashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AchievementDao {

    public static int getAchievements(Map<AchievementType, IAchievementGroup> achievementGroups, Map<Integer, Achievement> achievements) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM achievements WHERE enabled = '1' ORDER by group_name ASC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                count++;

                final AchievementType groupName = AchievementType.getTypeByName(resultSet.getString("group_name"));

                if (groupName == null) continue;

                if (!achievementGroups.containsKey(groupName)) {
                    achievementGroups.put(groupName, new AchievementGroup(resultSet.getInt("id"), new HashMap<>(), resultSet.getString("group_name"), AchievementCategory.valueOf(resultSet.getString("category").toUpperCase())));
                }

                if(!achievements.containsKey(resultSet.getInt("id"))) {
                    achievements.put(resultSet.getInt("id"), new Achievement(resultSet));
                }

                if (!achievementGroups.get(groupName).getAchievements().containsKey(resultSet.getInt("level"))) {
                    achievementGroups.get(groupName).getAchievements().put(resultSet.getInt("level"), create(resultSet));
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

    private static Achievement create(ResultSet resultSet) throws SQLException {
        return new Achievement(resultSet);
    }

    public static int getTalentTracks(THashMap<TalentTrackType, LinkedHashMap<Integer, ITalentTrackLevel>> talentTrackLevels) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM achievements_talents ORDER BY level ASC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                count++;

                TalentTrackLevel level = new TalentTrackLevel(resultSet);

                if (!talentTrackLevels.containsKey(level.getType())) {
                    talentTrackLevels.put(level.getType(), new LinkedHashMap<>());
                }

                talentTrackLevels.get(level.getType()).put(level.getLevel(), level);
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

}
