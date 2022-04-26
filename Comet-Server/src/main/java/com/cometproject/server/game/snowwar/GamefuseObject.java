package com.cometproject.server.game.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.items.Item;
import com.cometproject.server.game.snowwar.items.StringStuffData;

public class GamefuseObject extends Item {
    public int X;
    public int Y;
    public int Rot;
    public int Z;

    public GamefuseObject() {
        extraData = new StringStuffData(null);
    }
}
