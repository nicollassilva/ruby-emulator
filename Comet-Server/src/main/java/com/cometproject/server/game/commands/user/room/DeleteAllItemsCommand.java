package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;

public class DeleteAllItemsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();
        final IRoomData roomData = room.getData();

        //Check if is the room owner
        if (roomData.getOwnerId() != client.getPlayer().getData().getId()) {
            sendWhisper("Oops, você não é o dono deste quarto e não pode utilizar o comando aqui!", client);
            return;
        }

        final String yes = Locale.getOrDefault("command.empty.yes", "yes");

        if (params.length != 1) {
            sendAlert(Locale.getOrDefault("command.delete_all_items.confirm", "<b>Alerta!</b>\rTem a certeza? Você irá apagar todos os seus mobis do quarto.\r\rSe você tem a certeza digite  <b>:" + Locale.get("command.delete_all_items.name") + " " + yes + "</b>"), client);
            return;
        }

        if (!params[0].equals(yes)) {
            sendAlert(Locale.getOrDefault("command.delete_all_items.confirm", "<b>Alerta!</b>\rTem a certeza? Você irá apagar todos os seus mobis do quarto.\r\rSe você tem a certeza digite  <b>:" + Locale.get("command.delete_all_items.name") + " " + yes + "</b>"), client);
            return;
        }

        RoomDao.removeItems(room.getId(), room.getData().getOwnerId());

        room.reload();
    }

    @Override
    public String getPermission() {
        return "delete_all_items_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.empty.yes", "yes");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.delete_all_items.description", "Apaga todos os seus items do seu quarto diretamente.");
    }
}