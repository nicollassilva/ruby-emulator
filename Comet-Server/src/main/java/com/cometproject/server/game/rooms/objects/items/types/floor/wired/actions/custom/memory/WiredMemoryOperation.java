package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;

public abstract class WiredMemoryOperation extends WiredActionItem {
    public WiredMemoryOperation(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 21;
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    public abstract double doOp(double wiredValue, double value);

    /**
     * Raw wired text user input to double value
     * Used for get user-defined operators/conditionals input
     * @return
     */
    public double getInputValueOrDefault(){
        return WiredMemoryUtil.parseDoubleOrZero(this.getWiredData().getText());
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        final double baseValue = this.getInputValueOrDefault();
        for (WiredMemoryBox box : WiredMemoryUtil.getMemoriesBoxFrom(this)) {
            double value = WiredMemoryUtil.readMemoryFrom(box);
            WiredMemoryUtil.setMemoryInto(box, this.doOp(baseValue, value));
        }
    }
}
