package com.cometproject.server.game.gamecenter.games.battleball.player;

import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.game.gamecenter.games.battleball.room.BattleBallRoom;
import com.cometproject.server.game.gamecenter.games.battleball.room.RoomQueue;
import com.cometproject.server.game.gamecenter.games.battleball.thread.BattleBallThread;
import com.cometproject.server.game.gamecenter.games.battleball.util.RoomStatus;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BattleBallPlayerQueue {

    private static int roomCounter;
    private static final Map<Integer, RoomQueue> roomQueue = new ConcurrentHashMap<>(100);
    private static final Logger log = LogManager.getLogger(PlayerManager.class.getName());

    public static void addPlayerInQueue(Session client) {
        PlayerData playerData = client.getPlayer().getData();

        RoomQueue pickedRoom = null;

        if(roomQueue.isEmpty()) {
            pickedRoom = new RoomQueue(new BattleBallRoom(roomCounter++));

            roomQueue.put(pickedRoom.room.roomId, pickedRoom);
        } else {
            for(final RoomQueue room : roomQueue.values()) {
                if(room.players.size() < BattleBall.GAME_MAX_PLAYERS) {
                    pickedRoom = room;
                    break;
                }
            }

            if(pickedRoom == null) {
                pickedRoom = new RoomQueue(new BattleBallRoom(roomCounter++));

                roomQueue.put(pickedRoom.room.roomId, pickedRoom);
            }
        }

        if(pickedRoom.players.isEmpty()) {
            pickedRoom.room.owner = playerData.getUsername();
        }

        log.info(playerData.getUsername() + " joined a Battle Ball game!");

        for(final Session playerClient : pickedRoom.players.values()) {
            try {
                final OutgoingMessage message = OutgoingMessageManager.getInstance().getMessageInstance(Outgoing.BattleBallJoinQueueMessage);

                if(message == null) return;

                message.client = client.getPlayer().getData().getWebsocketSession();

                message.data = new JSONObject();
                message.data.put("username", playerClient.getPlayer().getEntity().getUsername());
                message.data.put("figure", playerClient.getPlayer().getEntity().getFigure());

                message.compose();
            } catch(final Exception ex) {
                ex.printStackTrace();
                System.out.println("BattleBallEngine " + ex);
            }
        }

        pickedRoom.players.put(playerData.getId(), client);

        for(final Session playerClient : pickedRoom.players.values()) {
            try {
                final OutgoingMessage message = OutgoingMessageManager.getInstance().getMessageInstance(Outgoing.BattleBallJoinQueueMessage);

                if(message == null) return;

                message.client = playerClient.getPlayer().getData().getWebsocketSession();

                message.data = new JSONObject();
                message.data.put("username", client.getPlayer().getEntity().getUsername());
                message.data.put("figure", client.getPlayer().getEntity().getFigure());

                message.compose();

            } catch(final Exception ex) {
                ex.printStackTrace();
                System.out.println("BattleBallEngine " + ex);
            }
        }

        if(pickedRoom.room.timeToStart < 20 && pickedRoom.room.status == RoomStatus.TIMER_TO_LOBBY) {
            // TODO: Send packet to player with counter;
        }

        if(pickedRoom.players.size() >= BattleBall.GAME_MIN_PLAYERS) {
            startLoading(pickedRoom);
        }

    }

    public static boolean playerExit(Session client) {
        for(RoomQueue queue : roomQueue.values()) {
            for(Session client2 : queue.players.values()) {
                if(client2 == client) {
                    queue.room.players.remove(client);
                    System.out.println("PLAYER LEAVED");
                    // TODO: Broadcast player leave lobby message event
                    return true;
                }
            }
        }
        return false;
    }

    public static void roomLoaded(BattleBallRoom room) {

        final RoomQueue queue = roomQueue.remove(room.roomId);

        if(queue == null) {
            return;
        }


        for(Session client : queue.players.values()) {
            client.getPlayer().getEntity().setCanWalk(false);

            client.getPlayer().bypassRoomAuth(true);
            client.send(new RoomForwardMessageComposer(room.map.getId()));
            room.players.put(client.getPlayer().getId(), client);
            BattleBall.PLAYERS.put(client.getPlayer().getId(), room);
        }

        System.out.println("ROOOOOOOOOOOOOOOOOOOOM LOADEEEEEEED");

    }

    public static void startLoading(RoomQueue queue) {

        final BattleBallRoom room = queue.room;

        if(room.status == RoomStatus.TIMER_TO_LOBBY) {
            return;
        }

        room.timeToStart = BattleBall.GAME_TIME_TO_START;
        room.status = RoomStatus.TIMER_TO_LOBBY;

        System.out.println("STARTING LOADING..........................");

        // TODO: Send packet to all players with counter queue.broadcast();

        BattleBallThread.addTask(new BattleBallThread(room, queue), 0, 1000);

    }

}

