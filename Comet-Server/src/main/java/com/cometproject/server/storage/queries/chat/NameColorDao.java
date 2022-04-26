package com.cometproject.server.storage.queries.chat;

import com.cometproject.server.game.rooms.types.misc.NameColor;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NameColorDao {
    public static ArrayList<NameColor> getNameColorList() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<NameColor> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT id,name,min_rank,color_code FROM name_colors", sqlConnection);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final String name = resultSet.getString("name");
                final int min_rank = resultSet.getInt("min_rank");
                final String color_code = resultSet.getString("color_code");
                data.add(new NameColor(id, name, min_rank, color_code));
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
