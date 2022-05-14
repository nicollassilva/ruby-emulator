package com.cometproject.server.game.commands.staff.bundles;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.composers.catalog.CatalogPublishMessageComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.bundles.RoomBundleManager;
import com.cometproject.server.game.rooms.bundles.types.RoomBundle;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.BundleDao;

public class BundleCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            client.send(new AlertMessageComposer(Locale.getOrDefault("command.bundle.create", "Utilize ':bundle create [nome]' para criar o bundle.")));
            return;
        }

        final String commandName = params[0];

        switch (commandName) {
            case "create": {
                final String alias = params[1];

                final Room room = client.getPlayer().getEntity().getRoom();
                final RoomBundle bundle = RoomBundle.create(room, alias);

                BundleDao.saveBundle(bundle);

                final boolean updateCatalog = RoomBundleManager.getInstance().getBundle(alias) != null;

                RoomBundleManager.getInstance().addBundle(bundle);

                if (updateCatalog) {
                    CatalogManager.getInstance().loadItemsAndPages();
                    NetworkManager.getInstance().getSessions().broadcast(new CatalogPublishMessageComposer(true));
                }

                if(!CometExternalSettings.enableStaffMessengerLogs) return;

                this.logDesc = "%s executou o comando bundle no quarto '%b' com o nome %n"
                        .replace("%s", client.getPlayer().getData().getUsername())
                        .replace("%b", client.getPlayer().getEntity().getRoom().getData().getName())
                        .replace("%n", alias);

                break;
            }

            case "destroy": {

                break;
            }
        }
    }

    @Override
    public String getPermission() {
        return "bundle_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.bundle.description");
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return true;
    }
}
