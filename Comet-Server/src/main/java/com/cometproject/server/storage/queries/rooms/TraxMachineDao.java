package com.cometproject.server.storage.queries.rooms;

import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.items.music.TraxMachineSong;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TraxMachineDao {
    public static void loadSongs() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM player_songs", sqlConnection);

            try {
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    RoomManager.getInstance().setTraxMachineSongFromUserId(resultSet.getInt("user_id"), new TraxMachineSong(resultSet));
                }
            } catch (Exception e) {
                Comet.getServer().getLogger().warn("Failed to load a TraxMachine song.");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static TraxMachineSong saveSong(ISession client, String data) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO player_songs (room_id, user_id, data, timestamp) VALUES (?, ?, ?, ?)", sqlConnection, true);
            preparedStatement.setInt(1, client.getPlayer().getEntity().getRoom().getData().getId());
            preparedStatement.setInt(2, client.getPlayer().getData().getId());
            preparedStatement.setString(3, data);
            preparedStatement.setInt(4, (int) Comet.getTime());

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                final TraxMachineSong song = new TraxMachineSong(
                        resultSet.getInt(1),
                        client.getPlayer().getEntity().getRoom().getData().getId(),
                        client.getPlayer().getData().getId(),
                        data,
                        (int) Comet.getTime()
                );

                RoomManager.getInstance().setTraxMachineSongFromUserId(client.getPlayer().getData().getId(), song);

                return song;
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static TraxMachineSong updateSong(ISession client, TraxMachineSong song, String data) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_songs SET data = ? WHERE user_id = ? AND room_id = ?", sqlConnection);
            preparedStatement.setString(1, data);
            preparedStatement.setInt(2, client.getPlayer().getData().getId());
            preparedStatement.setInt(3, client.getPlayer().getEntity().getRoom().getData().getId());
            preparedStatement.executeUpdate();

            song.setData(data);
            RoomManager.getInstance().setTraxMachineSongFromUserId(client.getPlayer().getData().getId(), song);

            return song;
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }
}
