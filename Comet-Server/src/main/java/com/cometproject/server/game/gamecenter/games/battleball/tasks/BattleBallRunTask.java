package com.cometproject.server.game.gamecenter.games.battleball.tasks;

import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.game.gamecenter.games.battleball.room.BattleBallRoom;
import com.cometproject.server.game.gamecenter.games.battleball.util.RoomStatus;

public class BattleBallRunTask {

    public static void exec(BattleBallRoom room) {

        if(room.players.isEmpty()) {
            System.out.println("ROOM IS EMPTY!: " + room.map.getEntities().playerCount());
            room.status = RoomStatus.CLOSE;
            room.deleteRoom();
            return;
        }
        if(room.players.size() < 2) {
            room.status = RoomStatus.ARENA_END;

            System.out.println("EEEEEEEEEENNNNNNNNNND!");
            // TODO: Send game stats.
        }
        //System.out.println("CURRENT GAME TIME: " + room.turn);

        if(++room.turn >= BattleBall.GAME_TURNS) {
            room.status = RoomStatus.ARENA_END;

            System.out.println("EEEEEEEEEENNNNNNNNNND!");
            // TODO: Send game stats.
        }

    }

}
