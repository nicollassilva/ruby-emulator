package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.utilities.Pair;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory.WiredMemoryBox;
import com.cometproject.server.game.rooms.types.Room;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WiredConditionMemoryUniversal extends WiredConditionMemory{
    private final List<Pair<Op, String>> operations = new ArrayList<>();
    public WiredConditionMemoryUniversal(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public int getInterface() {
        return 21;
    }

    @Override
    public boolean canOp(double wiredValue, double value){
        return false;
    }
/*
    public void parse(String text){
        int i =0;
        do{
            final char current = text.charAt(i);

            i++;
        }
        while (i < text.length());
    }

    private void parseToken(final String reader){

    }*/

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
   /*     final double wiredValue = WiredMemoryUtil.parseDoubleOrZero(this.getWiredData().getText());
        for(final WiredMemoryBox box:WiredMemoryUtil.getMemoriesBoxFrom(this)){
            if(!this.canOp(wiredValue, WiredMemoryUtil.readMemoryFrom(box)) && !isNegative)
                return false;
        }*/

        return true;
    }
}
