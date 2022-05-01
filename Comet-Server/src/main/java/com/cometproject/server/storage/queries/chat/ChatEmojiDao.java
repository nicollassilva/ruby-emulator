package com.cometproject.server.storage.queries.chat;
import com.cometproject.server.game.rooms.types.misc.ChatEmoji;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatEmojiDao {
    public static ArrayList<ChatEmoji> getEmojiList() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        final ArrayList<ChatEmoji> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT id,min_rank,strings FROM emojis", sqlConnection);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final int min_rank = resultSet.getInt("min_rank");
                final ArrayList<String> keys = new ArrayList<>(Arrays.asList(resultSet.getString("strings").split("&")));

                data.add(new ChatEmoji(id, min_rank, keys));
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
}
