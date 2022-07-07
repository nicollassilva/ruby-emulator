package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.DiceFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredCustomSetSpeedRollerSelected;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;

public class WiredActionMatchToSnapshot extends WiredActionItem {
    private static final int PARAM_MATCH_STATE = 0;
    private static final int PARAM_MATCH_ROTATION = 1;
    private static final int PARAM_MATCH_POSITION = 2;
    public WiredActionMatchToSnapshot(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 3;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (this.getWiredData().getSnapshots().size() == 0) {
            return;
        }

        final boolean matchState = this.getWiredData().getParams().get(PARAM_MATCH_STATE) == 1;
        final boolean matchRotation = this.getWiredData().getParams().get(PARAM_MATCH_ROTATION) == 1;
        final boolean matchPosition = this.getWiredData().getParams().get(PARAM_MATCH_POSITION) == 1;

        for (long itemId : this.getWiredData().getSelectedIds()) {

            final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) continue;

            final WiredItemSnapshot itemSnapshot = this.getWiredData().getSnapshots().get(itemId);

            if (itemSnapshot == null) continue;

            if (matchState && !(floorItem instanceof DiceFloorItem || floorItem instanceof HighscoreFloorItem)) {

                /*if (floorItem instanceof BanzaiTimerFloorItem) {
                    if (this.getRoom().getGame().getInstance() != null) {
                        this.getRoom().getGame().getInstance().setTimer(0);
                        this.getRoom().getGame().getInstance().setGameLength(Integer.parseInt(itemSnapshot.getExtraData()));
                    }
                }*/

                floorItem.getItemData().setData(itemSnapshot.getExtraData());
                //  floorItem.sendUpdate();
            }
            //    floorItem.setExtraData(itemSnapshot.getExtraData());
            //floorItem.save();
            //  floorItem.saveData();

            final Position currentPosition = floorItem.getPosition().copy();
            final Position newPosition = new Position(itemSnapshot.getX(), itemSnapshot.getY(), itemSnapshot.getZ());

            if (this.getRoom().getItems().moveFloorItemWired(floorItem, !matchPosition ? currentPosition : newPosition, matchRotation ? itemSnapshot.getRotation() : floorItem.getRotation(), true, false, false)) {
                if (!(floorItem instanceof WiredCustomSetSpeedRollerSelected)) {
                    if (this.useItemsAnimation()) {
                        this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
                    } else {
                        this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
                        this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, !matchPosition ? currentPosition : newPosition, 0, 0, floorItem.getVirtualId()));
                    }
                }
            }

           /* if(matchState) {
                floorItem.sendUpdate();
            }*/
        }
    }
}