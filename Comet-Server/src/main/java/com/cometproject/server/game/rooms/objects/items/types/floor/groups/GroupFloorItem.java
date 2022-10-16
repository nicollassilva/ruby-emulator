package com.cometproject.server.game.rooms.objects.items.types.floor.groups;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.groups.IGroupItemService;
import com.cometproject.api.game.groups.types.IGroupData;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class GroupFloorItem extends RoomItemFloor {
    private final int groupId;
    private int state;

    public GroupFloorItem(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        final String[] _data = this.getItemData().getData().split(";");

        if (Arrays.stream(_data).count() < 2 || !StringUtils.isNumeric(_data[0]) || !StringUtils.isNumeric((_data[1]))) {
            this.groupId = 0;
            this.state = 0;
        } else {
            this.groupId = Integer.parseInt(_data[0]);
            this.state = Integer.parseInt(_data[1]);
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            final PlayerEntity pEntity = (PlayerEntity) entity;

            if (this.getDefinition().requiresRights()) {
                if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId()) && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                    return false;
                }
            }

            if (pEntity.getPlayer().getId() == this.getRoom().getData().getOwnerId()) {
                pEntity.getPlayer().getQuests().progressQuest(QuestType.FURNI_SWITCH);
            }
        }

        this.state++;

        if (this.state >= (this.getDefinition().getInteractionCycleCount() - 1)) {
            this.getItemData().setData(this.groupId + ";0");
            this.state = 0;
        } else {
            this.getItemData().setData(this.groupId + ";" + this.state);
        }

        this.sendUpdate();
        this.saveData();

        return true;
    }

    @Override
    public void composeItemData(IComposer msg) {
        final IGroupData groupData = GameContext.getCurrent().getGroupService().getData(this.groupId);

        msg.writeInt(0);

        if (groupData == null) {
            msg.writeInt(2);
            msg.writeInt(0);
        } else {
            msg.writeInt(2);
            msg.writeInt(5);
            msg.writeString(this instanceof GroupGateFloorItem ? ((GroupGateFloorItem) this).isOpen ? "1" : "0" : this.state);
            msg.writeString(this.groupId);
            msg.writeString(groupData.getBadge());

            final IGroupItemService groupItemService = GameContext.getCurrent().getGroupService().getItemService();

            msg.writeString(groupItemService.isValidSymbolColour(groupData.getColourA())
                    ? groupItemService.getSymbolColourByIndex(groupData.getColourA()).getFirstValue()
                    : "ffffff");
            msg.writeString(groupItemService.isValidBackgroundColour(groupData.getColourB())
                    ? groupItemService.getBackgroundColourByIndex(groupData.getColourB()).getFirstValue()
                    : "ffffff");
        }
    }

    public void onEntityStepOn(RoomEntity entity, boolean instantUpdate) {
        if (!this.getDefinition().canSit()) return;

        double height = (entity instanceof PetEntity || entity.hasAttribute("transformation")) ? this.getSitHeight() / 2 : this.getSitHeight();

        entity.setBodyRotation(this.getRotation());
        entity.setHeadRotation(this.getRotation());
        entity.addStatus(RoomEntityStatus.SIT, String.valueOf(height).replace(',', '.'));

        if (instantUpdate)
            this.getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(entity));
        else
            entity.markNeedsUpdate();
    }

    @Override
    public String getDataObject() {
        return this.groupId + ";" + this.state;
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        this.onEntityStepOn(entity, false);
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if (entity.hasStatus(RoomEntityStatus.SIT)) {
            entity.removeStatus(RoomEntityStatus.SIT);
        }

        entity.markNeedsUpdate();
    }

    public double getSitHeight() {
        return this.getDefinition().getHeight();
    }

    public int getGroupId() {
        return groupId;
    }
}
