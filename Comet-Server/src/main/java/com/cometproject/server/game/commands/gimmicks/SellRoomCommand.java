package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;

public class SellRoomCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendWhisper("Oops, você precisa digitar a quantidade de moedas para vender este quarto.", client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room.getData().getOwnerId() != client.getPlayer().getId()) {
            sendWhisper("Oops, você não é o dono desde quarto!", client);
            return;
        }

        if (room.getGroup() != null) {
            sendWhisper("Oops, não pode vender este quarto porque tem um grupo.", client);
            return;
        }

        int value;

        try {
            value = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            sendWhisper("Oops, aconteceu um erro ao converter este valor, tente novamente.", client);
            return;
        }

        if (value < 0) {
            sendWhisper("Oops, valor inválido. Tente novamente.", client);
            return;
        }

        boolean isSelling = value > 0;

        RoomDao.changeRoomPrice(room.getId(), value);
        room.getData().setRoomPrice(value);

        for (final PlayerEntity playerEntity : room.getEntities().getPlayerEntities()) {
            playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), "Este quarto " + (!isSelling ? "já não está à venda!" : "está à venda por " + value + " créditos!")));
        }
    }

    @Override
    public String getPermission() {
        return "sell_room_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.amount", "(valor)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.sell_room.description", "Vender o quarto");
    }
}
