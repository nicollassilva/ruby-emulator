package com.cometproject.server.game.gamecenter.games.battleball.tasks;

import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.game.gamecenter.games.battleball.room.BattleBallRoom;
import com.cometproject.server.game.gamecenter.games.battleball.util.RoomStatus;
import com.cometproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BattleBallEndTask {

    public static void exec(BattleBallRoom room) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        //OutgoingMessageManager outgoingEventManager = new OutgoingMessageManager();

        System.out.println("FINITOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

        if(room.players.isEmpty()) {
            System.out.println("ROOM IS EMPTY!: " + room.map.getEntities().playerCount());
            room.status = RoomStatus.CLOSE;
            room.deleteRoom();
            return;
        }

        for(Session client : room.players.values()) {

            client.getPlayer().getSession().send(new HotelViewMessageComposer());
            BattleBall.PLAYERS.get(client.getPlayer().getId()).players.remove(client.getPlayer().getId());
            BattleBall.PLAYERS.remove(client.getPlayer().getId());
            System.out.println(client.getPlayer().getEntity().getUsername() + " left battle ball game!");
            client.getPlayer().bypassRoomAuth(false);
            // CONFIGURAR client.getPlayer().getEntity().setFreeze(true);

            /*Class<? extends OutgoingMessage> classMessage = outgoingEventManager.getMessages().get(Outgoing.BattleBallEndMessage);
            OutgoingMessage message = null;
            message = classMessage.getDeclaredConstructor().newInstance();
            message.client = client.getPlayer().getData().getWebsocketSession();
            message.data = new JSONObject();

            message.compose(); CONFIGURAR */

        }

        room.getRoom().removeAttribute("bb_game");

        room.status = RoomStatus.CLOSE;

        room.map.getGame().getInstance().onGameEnds();
        room.map.getGame().stop();

        room.deleteRoom();
    }

}
