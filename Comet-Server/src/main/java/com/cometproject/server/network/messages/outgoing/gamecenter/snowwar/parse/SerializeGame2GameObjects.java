package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.gameobjects.GameItemObject;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2GameObjects {
    public static void parse(final IComposer msg, final SnowWarRoom arena) {
        synchronized (arena.gameObjects) {
            msg.writeInt(arena.gameObjects.size());
            for (final GameItemObject Object : arena.gameObjects.values()) {
                for (int i = 0; i < Object.variablesCount; i++) {
                    msg.writeInt(Object.getVariable(i));
                }

                if (Object.getVariable(0) == GameItemObject.HUMAN) {
                    final HumanGameObject Player = (HumanGameObject) Object;
                    msg.writeString(Player.userName);
                    msg.writeString(Player.motto);
                    msg.writeString(Player.look);
                    msg.writeString(Player.sex);
                }
            }
        }
    }
}
