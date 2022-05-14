package com.cometproject.game.items.inventory.items;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.LimitedEditionItem;
import com.cometproject.api.game.groups.types.IGroupData;
import com.cometproject.api.game.players.data.components.inventory.InventoryItemData;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.game.items.inventory.InventoryItem;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class GroupInventoryItem extends InventoryItem {
    public GroupInventoryItem(InventoryItemData inventoryItemData, FurnitureDefinition furnitureDefinition) {
        super(inventoryItemData, furnitureDefinition);
    }

    @Override
    public boolean composeData(IComposer msg) {
        int groupId = 0;
        int state = 0;

        msg.writeInt(17);

        final String[] _data = this.getExtraData().split(";");

        if (Arrays.stream(_data).count() >= 2 && StringUtils.isNumeric(_data[0]) && StringUtils.isNumeric((_data[1]))) {
            groupId = Integer.parseInt(_data[0]);
            state = Integer.parseInt(_data[1]);
        }

        final IGroupData groupData = groupId == 0 ? null : GameContext.getCurrent().getGroupService().getData(groupId);

        if (groupData == null) {
            msg.writeInt(2);
            msg.writeInt(0);
        } else {
            msg.writeInt(2);
            msg.writeInt(5);
            msg.writeString(state);
            msg.writeString(groupId);
            msg.writeString(groupData.getBadge());

            final String colourA = GameContext.getCurrent().getGroupService().getItemService().getSymbolColours().get(groupData.getColourA()) != null ? GameContext.getCurrent().getGroupService().getItemService().getSymbolColours().get(groupData.getColourA()).getFirstValue() : "ffffff";
            final String colourB = GameContext.getCurrent().getGroupService().getItemService().getBackgroundColours().get(groupData.getColourB()) != null ?  GameContext.getCurrent().getGroupService().getItemService().getBackgroundColours().get(groupData.getColourB()).getFirstValue() : "ffffff";

            msg.writeString(colourA);
            msg.writeString(colourB);
        }

        return true;
    }
}
