package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.objects.items.types.floor.football.FootballGateFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.utilities.validator.ClothingValidationManager;
import com.cometproject.server.game.utilities.validator.FigureGender;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class SaveFootballGateMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }

        if (!room.getRights().hasRights(client.getPlayer().getEntity().getPlayerId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            client.disconnect();
            return;
        }

        final int itemId = msg.readInt();

        if (room.getItems().getFloorItem(itemId) == null || !(room.getItems().getFloorItem(itemId) instanceof FootballGateFloorItem))
            return;

        final FootballGateFloorItem floorItem = ((FootballGateFloorItem) room.getItems().getFloorItem(itemId));
        final String gender = msg.readString().toUpperCase();
        final String figure = ClothingValidationManager.validateLook(msg.readString(), FigureGender.fromString(gender), true);

        floorItem.setFigure(gender, figure.contains(";") ? figure.split(";")[gender.equals("M") ? 0 : 1] : figure);
        floorItem.saveData();

        floorItem.sendUpdate();
    }
}
