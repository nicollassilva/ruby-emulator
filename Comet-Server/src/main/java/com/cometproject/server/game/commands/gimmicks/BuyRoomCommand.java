package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomReloadListener;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.handshake.HomeRoomMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;

public class BuyRoomCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        // Can buy rooms every 30 seconds
        if (client.getPlayer().antiSpam("buyRoom", 30.0)) {
            sendWhisper("Compra não concluída: Você está comprando muitos quartos em pouco tempo.", client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        final IRoomData roomData = room.getData();
        final int roomPrice = roomData.getRoomPrice();
        final int userId = client.getPlayer().getData().getId();

        //Check if the room is on sell
        if (roomPrice == 0) {
            sendWhisper("Esse quarto não está a venda!", client);
            return;
        }

        if (roomData.getOwnerId() == userId) {
            sendWhisper("Não é permitido comprar o próprio quarto. Para cancelar essa venda, anuncie-o novamente com o preço zero.", client);
            return;
        }

        if (room.getGroup() != null) {
            sendWhisper("Não é permitido comprar um quarto que possui grupo.", client);
            return;
        }

        if (room.getData().getRoomBuyer() != client.getPlayer().getData().getId()) {
            sendWhisper("Você não é o usuário permitido a comprar este quarto!", client);
            return;
        }

        //Check if has enough diamonds
        if (client.getPlayer().getData().getCredits() < roomPrice) {
            sendWhisper("Você não possui moedas suficientes para comprar este quarto!", client);
            return;
        }

        if (params.length != 1) {
            sendAlert(Locale.getOrDefault("command.buy_room.confirm","<b>Alerta</b>\rVocê tem a certeza que deseja comprar este quarto?\r\rCustará <b>" + room.getData().getRoomPrice() + "</b> moedas! Digite ':" + Locale.get("command.buy_room.name") + " sim' para confirmar."), client);
            return;
        }

        if (!params[0].equals("sim")) {
            sendAlert(Locale.getOrDefault("command.buy_room.confirm","<b>Alerta</b>\rVocê tem a certeza que deseja comprar este quarto?\r\rCustará <b>" + room.getData().getRoomPrice() + "</b> moedas! Digite ':" + Locale.get("command.buy_room.name") + " sim' para confirmar."), client);
            return;
        }

        final Session roomOwner = NetworkManager.getInstance().getSessions().getByPlayerId(roomData.getOwnerId());

        if (roomOwner == null) {
            sendWhisper("O dono deste quarto está offline!", client);
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

        for (final PlayerEntity playerEntity : room.getEntities().getPlayerEntities()) {
            if(playerEntity.getPlayer().getId() == roomOwner.getPlayer().getId()) continue;

            playerEntity.getPlayer().getSession().send(
                    new WhisperMessageComposer(playerEntity.getId(), "Este quarto foi comprado pelo usuário " + client.getPlayer().getData().getUsername() + "!")
            );
        }

        sendNotif("O seu quarto foi comprado pelo usuário " + client.getPlayer().getData().getUsername() + "!", roomOwner);

        final int roomId = room.getId();

        RoomDao.changeRoomPrice(roomId, 0, 0);
        RoomDao.removeNonOwnerItems(roomId, room.getData().getOwnerId());
        RoomDao.transferItems(roomId, room.getData().getOwnerId(), userId);
        RoomDao.changeRoomOwner(roomId, userId, client.getPlayer().getData().getUsername());

        roomData.setRoomPrice(0);
        roomData.setOwnerId(userId);
        roomData.setOwner(client.getPlayer().getData().getUsername());

        if (roomOwner.getPlayer().getSettings().getHomeRoom() == roomId) {
            client.send(new HomeRoomMessageComposer(roomOwner.getPlayer().getSettings().getHomeRoom(), 0));
            client.getPlayer().getSettings().setHomeRoom(0);
        }

        room.getItems().getItemOwners().keySet().forEach(id -> {
            final Session itemOwner = NetworkManager.getInstance().getSessions().getByPlayerId(id);

            if (itemOwner != null)
                itemOwner.getPlayer().getInventory().send();
        });

        this.sendUpdateDataAndReload(client, roomOwner, room);
    }

    public void sendUpdateDataAndReload(Session buyerSession, Session sellerSession, Room room) {
        final RoomReloadListener reloadListener = new RoomReloadListener(room, (players, newRoom) -> {
            for (final Player player : players) {
                if (player.getEntity() == null) continue;

                player.getSession().send(new RoomForwardMessageComposer(newRoom.getId()));
            }
        });

        RoomManager.getInstance().addReloadListener(buyerSession.getPlayer().getEntity().getRoom().getId(), reloadListener);

        buyerSession.getPlayer().getRooms().add(room.getId());
        sellerSession.getPlayer().getRooms().remove(Integer.valueOf(room.getId()));

        buyerSession.getPlayer().setLastRoomCreated((int) Comet.getTime());
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
