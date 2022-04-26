package com.cometproject.server.game.snowwar.tasks;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.StageEndingComposer;
import com.cometproject.server.game.snowwar.gameevents.Event;
import com.cometproject.server.game.snowwar.gameobjects.GameItemObject;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.FullGameStatusComposer;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.GameStatusComposer;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class SnowArenaRun {
    public static void exec(SnowWarRoom room) {
        if (room.players.isEmpty()) {
            room.STATUS = SnowWar.CLOSE;
            return;
        }


        List<ChannelHandlerContext> filter;
        List<Event> events;
        MessageWriter writer;
        synchronized (room.gameEvents) {
            synchronized (room.fullGameStatusQueue) {
                filter = room.fullGameStatusQueue;
                room.fullGameStatusQueue = new ArrayList<>();
            }

//            if (filter.isEmpty()) {
                room.checksum = 0;
                for (final GameItemObject Object : room.gameObjects.values()) {
                    Object.GenerateCHECKSUM(room, 1);
                }
//            }

            for (final ChannelHandlerContext socket : filter) {
                socket.writeAndFlush(new FullGameStatusComposer(room));
            }

            writer = GameStatusComposer.compose(room);

            room.gameEvents.clear();
        }

        for (final HumanGameObject player : room.players.values()) {
            if (player.currentSnowWar != null) {
                if (!filter.isEmpty()) {
                    if (filter.contains(player.cn.getChannel())) {
                        continue;
                    }
                }
                try {
                    player.cn.getChannel().writeAndFlush(writer.getMessage(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        room.subturn();
        room.subturn();
        room.subturn();

        if (++room.Turn >= SnowWar.GAMETURNS) {
            room.STATUS = SnowWar.ARENA_END;
            room.broadcast(new StageEndingComposer());
        }

		/*int checksum = SerializeGameStatus.seed(room.Turn);
        Log.printLog(room.Turn+" seed:"+checksum);
		for(GameItemObject item : room.gameObjects.values()) {
			int asd = 0;
			String local5 = "";
            while (asd < item.variablesCount) {
                local5 += item.getVariable(asd)+ ",";
            	checksum += (item.getVariable(asd) * ++asd);
            };
            Log.printLog("GameItem:"+item.objectId+" temp-checksum:"+checksum+" Params:"+local5);
		}
        Log.printLog("Turn:"+room.Turn+" real-checksum:"+room.checksum+" pro-checksum:"+(room.checksum+SerializeGameStatus.seed(room.Turn)));
        */
    }
}
