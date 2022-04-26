package com.cometproject.server.game.commands.development;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.misc.Prefix;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.chat.PrefixDao;

import java.util.ArrayList;
import java.util.Map;

public class PrefixCommand extends ChatCommand {
    private static ArrayList<String> prefixes;
    @Override
    public void execute(Session client, String[] params) {
        String prefix = params[0];

        prefixes = PrefixDao.getAll();

        /*if(prefixes == null) {
            return;
        }*/

        if(prefix == null) {
            return;
        }

        boolean prefixesFilter = prefixes.stream().anyMatch(prefix::equalsIgnoreCase);

        if(prefixesFilter) {
            client.send(new NotificationMessageComposer("generic", "El prefijo: " + prefix + " está prohibido en nuestro filtro de prefijos"));
            return;
        } else if (prefix.length() < 1 || prefix.length() > 5) {
            client.send(new NotificationMessageComposer("generic", "Los prefijos tienen longitud desde 1 a 5 carácteres, y tu prefijo excede el máximo o no tiene nada"));
            return;
        }

            client.getPlayer().getData().setTag(prefix);
            client.getPlayer().getData().save();
            isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "prefix_command";
    }

    @Override
    public String getParameter() {
        return "(prefijo)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.prefix.name", "Añade un prefijo a tu nombre");
    }
}
