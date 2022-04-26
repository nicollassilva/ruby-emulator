package com.cometproject.server.network.flash_external_interface_protocol.incoming.common;

import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.network.messages.outgoing.moderation.ModToolMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.storage.SqlHelper;
import com.cometproject.server.storage.queries.config.ConfigDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OperationFUEvent extends IncomingExternalInterfaceMessage<OperationFUEvent.JSONOperationFUEvent> {
    public OperationFUEvent() {
        super(JSONOperationFUEvent.class);
    }

    @Override
    public void handle(Session client, JSONOperationFUEvent message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = SqlHelper.getConnection();
            preparedStatement = connection.prepareStatement(message.query);
            preparedStatement.execute();
            BanManager.getInstance().loadBans();
            CatalogManager.getInstance().loadItemsAndPages();
            CatalogManager.getInstance().loadGiftBoxes();
            NavigatorManager.getInstance().loadCategories();
            NavigatorManager.getInstance().loadPublicRooms();
            NavigatorManager.getInstance().loadStaffPicks();
            PermissionsManager.getInstance().loadRankPermissions();
            PermissionsManager.getInstance().loadPerks();
            PermissionsManager.getInstance().loadCommands();
            PermissionsManager.getInstance().loadOverrideCommands();
            ConfigDao.getAll();
            ConfigDao.getExternalConfig();
            ModerationManager.getInstance().loadPresets();
            ModerationManager.getInstance().getModerators().forEach((session -> session.send(new ModToolMessageComposer())));
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(connection);
            SqlHelper.closeSilently(preparedStatement);
        }
    }

    static class JSONOperationFUEvent {
        String query;
    }
}
