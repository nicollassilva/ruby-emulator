package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.ItemsComponent;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;

import java.util.stream.Collectors;

public class BuyRoomCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        Room room = client.getPlayer().getEntity().getRoom();
        IRoomData roomData = room.getData();
        int roomPrice = roomData.getRoomPrice();
        int userId = client.getPlayer().getData().getId();

        //Check if the room is on sell
        if (roomPrice == 0) {
            sendWhisper("Oops, este quarto não está à venda!", client);
            return;
        }

        if (roomData.getId() == userId) {
            sendWhisper("Oops, não pode comprar o seu próprio quarto! Se quiser o remover da venda, digite :sellroom 0", client);
            return;
        }

        if (room.getGroup() != null) {
            sendWhisper("Oops, não pode comprar este quarto porque tem um grupo.", client);
            return;
        }

        //Check if has enough credits
        if (client.getPlayer().getData().getCredits() < roomPrice) {
            sendWhisper("Oops, não tem créditos suficientes!", client);
            return;
        }

        Session roomOwner = NetworkManager.getInstance().getSessions().getByPlayerId(roomData.getOwnerId());
        if (roomOwner == null) {
            sendWhisper("Oops, o dono deste quarto está offline!", client);
            return;
        }

        //Decrement from the buyer the credits
        client.getPlayer().getData().decreaseCredits(roomPrice);
        client.getPlayer().getData().save();
        client.getPlayer().sendBalance();

        //Increment to the room owner the credits
        roomOwner.getPlayer().getData().increaseCredits(roomPrice);
        roomOwner.getPlayer().getData().save();
        roomOwner.getPlayer().sendBalance();

        ItemsComponent roomItems = room.getItems();

        roomItems.getFloorItems().values().forEach(item -> {
            if (item != null) {
                if (item.getItemData().getOwnerId() != roomOwner.getPlayer().getData().getId()) {
                    Session owner = NetworkManager.getInstance().getSessions().getByPlayerId(item.getItemData().getOwnerId());
                    room.getItems().removeItem(item, owner);
                } else {
                    item.getItemData().setOwnerId(userId);
                }
            }
        });

        roomItems.getWallItems().values().forEach(item -> {
            if (item != null) {
                if (item.getItemData().getOwnerId() != roomOwner.getPlayer().getData().getId()) {
                    Session owner = NetworkManager.getInstance().getSessions().getByPlayerId(item.getItemData().getOwnerId());
                    room.getItems().removeItem(item, item.getItemData().getOwnerId(), owner);
                } else {
                    item.getItemData().setOwnerId(userId);
                }
            }
        });

        for (final PlayerEntity playerEntity : room.getEntities().getPlayerEntities().stream().filter(player -> player.getId() != roomOwner.getPlayer().getData().getId()).collect(Collectors.toList())) {
            playerEntity.getPlayer().getSession().send(new WhisperMessageComposer(playerEntity.getId(), "Este quarto foi comprado pelo usuário " + client.getPlayer().getData().getUsername() + "!"));
        }

        sendNotif("O seu quarto foi comprado pelo usuário " + client.getPlayer().getData().getUsername() + "!", roomOwner);

        RoomDao.changeRoomPrice(room.getId(), 0);
        roomData.setRoomPrice(0);

        room.reload();
    }

    @Override
    public String getPermission() {
        return "buy_room_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.amount", "(valor)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.buy_room.description", "Comprar o quarto");
    }
}
