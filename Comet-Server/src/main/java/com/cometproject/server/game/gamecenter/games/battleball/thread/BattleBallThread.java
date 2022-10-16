package com.cometproject.server.game.gamecenter.games.battleball.thread;

import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.game.gamecenter.games.battleball.player.BattleBallPlayerQueue;
import com.cometproject.server.game.gamecenter.games.battleball.room.BattleBallRoom;
import com.cometproject.server.game.gamecenter.games.battleball.room.RoomQueue;
import com.cometproject.server.game.gamecenter.games.battleball.tasks.BattleBallEndTask;
import com.cometproject.server.game.gamecenter.games.battleball.tasks.BattleBallRunTask;
import com.cometproject.server.game.gamecenter.games.battleball.util.RoomStatus;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.game.rooms.types.components.games.GameType;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.util.concurrent.ScheduledFuture;

public class BattleBallThread extends Thread {

    public ScheduledFuture<?> future;

    public static void addTask(final BattleBallThread task, final int initDelay, final int repeatDelay) {
        BattleBallWorkerThread.addTask(task, initDelay, repeatDelay, BattleBallWorkerThread.BattleBallTasks);
    }

    public BattleBallRoom room;
    private final RoomQueue pickedRoom;

    public BattleBallThread(final BattleBallRoom battleBallRoom, final RoomQueue queue) {
        room = battleBallRoom;
        pickedRoom = queue;
    }

    @Override
    public void run() {
        try {
            if (room.status == RoomStatus.ARENA_END) {
                future.cancel(false);
                BattleBallEndTask.exec(room);
                return;
            }

            if (room.status == RoomStatus.ARENA) {
                BattleBallRunTask.exec(room);
                return;
            }

            if (room.status == RoomStatus.STAGE_RUNNING) {
                //BattleBallRunTask.exec(room);

                for(Session client : room.players.values()) {
                    final OutgoingMessage message = OutgoingMessageManager.getInstance().getMessageInstance(Outgoing.BattleBallStartedMessage);

                    if(message == null) return;

                    message.client = client.getPlayer().getData().getWebsocketSession();
                    message.data = new JSONObject();

                    message.compose();
                }

                if (room.map.getGame().getInstance() != null) {
                    room.map.getGame().getInstance().onGameEnds();
                    room.map.getGame().stop();
                }

                if (room.map.getGame().getInstance() == null) {
                    room.map.getGame().createNew(GameType.BATTLEBALL);
                    room.map.getGame().getInstance().startTimer(BattleBall.GAME_LENGTH);
                }



                for(final Session client : room.players.values()) {
                    client.getPlayer().getEntity().setFreeze(false);
                }

                room.status = RoomStatus.ARENA;
                return;
            }

            if (room.status == RoomStatus.STAGE_STARTING) {
                room.status = RoomStatus.STAGE_RUNNING;

                for(final Session client : room.players.values()) {
                    final OutgoingMessage message = OutgoingMessageManager.getInstance().getMessageInstance(Outgoing.BattleBallCounterMessage);

                    if(message == null) return;

                    message.client = client.getPlayer().getData().getWebsocketSession();
                    message.data = new JSONObject();

                    message.compose();

                    /* dumb code
                    room.map.getGame().joinTeam(GameTeam.RED, client.getPlayer().getEntity());
                    client.getPlayer().getEntity().setGameTeam(GameTeam.RED);
                    client.getPlayer().getEntity().applyTeamEffect(new PlayerEffect(GameTeam.RED.getEffect(GameType.BATTLEBALL), 0));

                    client.getPlayer().getEntity().warpImmediately(new Position(2,10,2));
                     */
                }

                BattleBallThread.addTask(this, 5000, BattleBall.GAME_TURN_MILLIS);
                return;
            }

            if (room.status == RoomStatus.STAGE_LOADING) {
                room.status = RoomStatus.STAGE_STARTING;

                System.out.println("STAGE LOADING");

                if (room.status == RoomStatus.STAGE_STARTING) {
                    System.out.println("Currently starting...");
                    future.cancel(false);
                    BattleBallThread.addTask(this, 6000, 0);
                }
                return;
            }

            if (room.status == RoomStatus.TIMER_TO_LOBBY) {
                System.out.println("TEMPS AVANT LANCEMENT: " + room.timeToStart);

//                if(pickedRoom.players.size() == 1) {
//                    return;
//                }

                if(pickedRoom.players.isEmpty()) {
                    room.status = RoomStatus.CLOSE;

                    return;
                }

                if (room.timeToStart-- == 0) {
                    future.cancel(false);

//                    if(pickedRoom.players.size() == 1) {
//                        room.status = RoomStatus.CLOSE;
//
//                        return;
//                    }

                    //TODO: REACTIVATE WHEN FINISHED

                    BattleBallPlayerQueue.roomLoaded(room);
                    room.status = RoomStatus.STAGE_LOADING;
                    BattleBallThread.addTask(this, 100, 200);
                }
            }
        } catch(final Exception ex) {
            future.cancel(false);
            ex.printStackTrace();
            System.out.println("BattleBallEngine " + ex);
        }
    }
}
