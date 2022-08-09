package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredMemoryUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.memory.WiredMemoryBox;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public abstract class WiredConditionMemory extends WiredConditionItem {
    public WiredConditionMemory(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
    }

    @Override
    public int getInterface() {
        return 21;
    }

    public abstract boolean canOp(double wiredValue, double value);

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        final double wiredValue = WiredMemoryUtil.parseDoubleOrZero(this.getWiredData().getText());
        for(final WiredMemoryBox box:WiredMemoryUtil.getMemoriesBoxFrom(this)){
            if(!this.canOp(wiredValue, WiredMemoryUtil.readMemoryFrom(box)) && !isNegative)
                return false;
        }

        return true;
    }

    @Override
    public MessageComposer getDialog() {
        return new CustomWiredComposer(this);
    }


    public static class CustomWiredComposer extends MessageComposer{
        public final WiredConditionItem wiredCondition;
        public CustomWiredComposer(WiredConditionItem item){
            wiredCondition = item;
        }

        @Override
        public short getId() {
            return Composers.WiredEffectConfigMessageComposer;
        }

        @Override
        public void compose(IComposer msg) {
            msg.writeBoolean(false); // advanced
            msg.writeInt(wiredCondition.getFurniSelection());

            msg.writeInt(wiredCondition.getWiredData().getSelectedIds().size());

            for (Long itemId : wiredCondition.getWiredData().getSelectedIds()) {
                msg.writeInt(ItemManager.getInstance().getItemVirtualId(itemId));
            }

            msg.writeInt(wiredCondition.getDefinition().getSpriteId());
            msg.writeInt(wiredCondition.getVirtualId());

            msg.writeString(wiredCondition.getWiredData().getText());

            msg.writeInt(wiredCondition.getWiredData().getParams().size());

            for (int param : wiredCondition.getWiredData().getParams().values()) {
                msg.writeInt(param);
            }

            msg.writeInt(wiredCondition.getWiredData().getSelectionType());
            msg.writeInt(wiredCondition.getInterface());
            msg.writeInt(0);

            msg.writeInt(0);
/*
            for (WiredTriggerItem incompatibleTrigger : incompatibleTriggers) {
                msg.writeInt(incompatibleTrigger.getDefinition().getSpriteId());
            }*/
        }
    }
}
