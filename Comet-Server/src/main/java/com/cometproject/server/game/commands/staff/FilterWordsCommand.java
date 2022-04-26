package com.cometproject.server.game.commands.staff;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FilterWordsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(params.length < 1) {
            client.getPlayer().getSession().send(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("command.filter_words.incorrect_param", "Parámetro incorrecto"), ChatEmotion.NONE, 34));
            return;
        }

        saveWordfilter(params[0], "**********");
        isExecuted(client);
        RoomManager.getInstance().getFilter().loadFilter();
    }

    public static void saveWordfilter(String word, String replacement) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO wordfilter (word, replacement) VALUES(?, ?);", sqlConnection);

            preparedStatement.setString(1, word);
            preparedStatement.setString(2, replacement);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    @Override
    public String getPermission() {
        return "addfilter_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.filter_words.parameters", "%word%");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.filter_words.description", "Añade una palabra al filtro");
    }
}
