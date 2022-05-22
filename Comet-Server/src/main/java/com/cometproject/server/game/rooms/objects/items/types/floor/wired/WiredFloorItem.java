package com.cometproject.server.game.rooms.objects.items.types.floor.wired;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.AdvancedFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionMatchToSnapshot;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.data.WiredActionItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.data.WiredItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemFlashEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.cometproject.server.utilities.attributes.Stateable;
import com.google.common.collect.Lists;

import java.util.List;


/**
 * This system was inspired by Nillus' "habbod2".
 */
public abstract class WiredFloorItem extends AdvancedFloorItem<WiredItemEvent> implements WiredItemSnapshot.Refreshable, Stateable {
    /**
     * The data associated with this wired item
     */
    private WiredItemData wiredItemData = null;
    private boolean state;
    private final boolean hasTicked = false;

    private boolean execute = true;
    private List<RoomEntity> executeEntity = Lists.newArrayList();

    public WiredFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (!this.getItemData().getData().startsWith("{")) {
            this.getItemData().setData("{}");
        }

        this.load();
    }

    /**
     * Turn the wired item data into a JSON object, and then save it to the database
     */
    @Override
    public void save() {
        this.getItemData().setData(JsonUtil.getInstance().toJson(wiredItemData));

        super.save();
    }

    /**
     * Turn the JSON object into a usable WiredItemData object
     */
    public void load() {
        try {
            if (this.getItemData().getData().equals("{}")) {
                this.wiredItemData = (this instanceof WiredActionItem) ? new WiredActionItemData() : new WiredItemData();
                return;
            }

            this.wiredItemData = JsonUtil.getInstance().fromJson(this.getItemData().getData(), (this instanceof WiredActionItem) ? WiredActionItemData.class : WiredItemData.class);
            this.onDataRefresh();
        } catch (Exception e) {
            Comet.getServer().getLogger().warn("Invalid wired item data " + this.getItemData().getData() + " ID: " + this.getId());
        }
    }

    @Override
    public void onPickup() {
        this.getItemData().setData("{}");
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!(entity instanceof PlayerEntity)) {
            return true;
        }

        final PlayerEntity p = (PlayerEntity) entity;

        if (!this.getRoom().getRights().hasRights(p.getPlayerId()) && !p.getPlayer().getPermissions().getRank().roomFullControl()) {
            return true;
        }

        //this.flash();
        ((PlayerEntity) entity).getPlayer().getSession().send(this.getDialog());
        return true;
    }

    public void UpdateListItem() {
        final List<Long> toRemove = Lists.newArrayList();

        if (this.getWiredData() != null) {
            for (final long itemId : this.getWiredData().getSelectedIds()) {
                final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

                if (floorItem == null)
                    toRemove.add(itemId);
            }

            for (final long itemToRemove : toRemove) {
                this.getWiredData().getSelectedIds().remove(itemToRemove);
            }

            this.save();
        }
    }

    @Override
    public void refreshSnapshots() {
        final List<Long> toRemove = Lists.newArrayList();

        this.getWiredData().getSnapshots().clear();

        for (final long itemId : this.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
                continue;
            }

            WiredItemSnapshot snapshot = new WiredItemSnapshot(floorItem);

            if (floorItem instanceof WiredActionMatchToSnapshot) {
                snapshot = new WiredItemSnapshot(floorItem);
            }

            this.getWiredData().getSnapshots().put(itemId, snapshot);
        }

        for (final long itemToRemove : toRemove) {
            this.getWiredData().getSelectedIds().remove(itemToRemove);
        }

        this.save();
    }

    private boolean isFlashing = false;

    public void flash() {
        if (this.isFlashing) {
            return;
        }

        this.switchState();

        this.isFlashing = true;

        final WiredItemFlashEvent flashEvent = new WiredItemFlashEvent();
        this.queueEvent(flashEvent);
    }

    public void switchState() {
        if (this.isFlashing) {
            this.isFlashing = false;
        }

        this.state = !this.state;
        this.sendUpdate();
    }

    @Override
    public void onTick() {
        super.onTick();

        // any wired-only ontick stuff here?
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {

    }

    /**
     * Get the wired item data object
     *
     * @return The wired item data object
     */
    public WiredItemData getWiredData() {
        return wiredItemData;
    }

    /**
     * Get the ID of the interface of the item which will tell the client which input fields to display
     *
     * @return The ID of the interface
     */
    public abstract int getInterface();

    /**
     * Get the packet to display the full dialog to the player
     *
     * @return The dialog message composer
     */
    public abstract MessageComposer getDialog();

    /**
     * Evaluate the wired trigger/action/condition
     *
     * @param entity The entity that's involved with this event
     * @param data   The data passed by the trigger
     * @return Whether or not the evaluation was a success
     */
    public abstract boolean evaluate(RoomEntity entity, Object data);

    /**
     * Will be executed when the data has been refreshed
     */
    public void onDataRefresh() {

    }

    /**
     * Will be executed when the data has been changed (different to the "onDataRefresh" event)
     */
    public void onDataChange() {

    }

    /**
     * If Wired uses Delay
     * @return boolean
     */
    public boolean usesDelay() {
        return true;
    }

    /**
     * Custom Furni selection
     * @return integer
     */
    public int getFurniSelection() {
        return WiredUtil.MAX_FURNI_SELECTION;
    }

    @Override
    public boolean getState() {
        return this.state;
    }

    public void resetExecute() {
        if (this.getRoom() != null) {
            this.getRoom().setExecutedEvent(0);
        }

        this.execute = true;
        this.executeEntity = Lists.newArrayList();
    }
}
