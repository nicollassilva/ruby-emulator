package com.cometproject.server.game.gamecenter.games.battleball.tasks;

import com.cometproject.server.game.gamecenter.games.battleball.BattleBall;
import com.cometproject.server.game.gamecenter.games.battleball.room.BattleBallRoom;
import com.cometproject.server.game.gamecenter.games.battleball.util.RoomStatus;

public class BattleBallRunTask {

    public static void exec(BattleBallRoom room) {
        if(room.players.isEmpty()) {
            room.status = RoomStatus.CLOSE;
            room.deleteRoom();
            return;
        }

        // Temporary modification. Original: room.players.size() < 2
        if(room.players.size() > 2) {
            room.status = RoomStatus.ARENA_END;

            // Send BattleBall gaming stats
        }

        //System.out.println("CURRENT GAME TIME: " + room.turn);

        if(++room.turn >= BattleBall.GAME_TURNS) {
            room.status = RoomStatus.ARENA_END;

            // Send BattleBall gaming stats
        }
    }

}
