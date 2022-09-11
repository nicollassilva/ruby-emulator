package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.types.floor.DiceFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.football.FootballTimerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiTimerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;

import java.util.Iterator;

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
    public boolean evaluate(RoomEntity entity, Object data) {
        if (this.hasTicks()) return false;
        if (this.getWiredData().getDelay() >= 1) {
            this.setTicks(RoomItemFactory.getProcessTime(this.getWiredData().getDelay() / 2));
        } else {
            this.onTickComplete();
        }
        return true;
    }

    @Override
    public void onTickComplete() {
        if (this.getWiredData().getSnapshots().size() == 0) {
            return;
        }

        final boolean matchState = this.getWiredData().getParams().get(PARAM_MATCH_STATE) == 1;
        final boolean matchRotation = this.getWiredData().getParams().get(PARAM_MATCH_ROTATION) == 1;
        final boolean matchPosition = this.getWiredData().getParams().get(PARAM_MATCH_POSITION) == 1;

        if (this.getWiredData().getSnapshots().size() != 0) {
            Iterator var5 = this.getWiredData().getSelectedIds().iterator();

            while (true) {
                boolean rotationChanged;
                boolean stateChanged;
                RoomItemFloor floorItem;
                WiredItemSnapshot itemSnapshot;
                do {
                    long itemId;
                    do {
                        if (!var5.hasNext()) {
                            return;
                        }
                        itemId = (Long) var5.next();
                        rotationChanged = false;
                        stateChanged = false;
                        floorItem = this.getRoom().getItems().getFloorItem(itemId);
                    } while (floorItem == null);

                    itemSnapshot = (WiredItemSnapshot) this.getWiredData().getSnapshots().get(itemId);
                } while (itemSnapshot == null);


                if (matchState && !(floorItem instanceof HighscoreClassicFloorItem)) {
                    String currentExtradata = floorItem.getItemData().getData();
                    String newExtradata = itemSnapshot.getExtraData();

                    if(!currentExtradata.equals(newExtradata)) {
                        floorItem.getItemData().setData(newExtradata);
                        stateChanged = true;
                    }

                    int gameLength = 0;
                    if(floorItem instanceof FootballTimerFloorItem)
                    {
                        try
                        {
                            gameLength = Integer.parseInt(itemSnapshot.getExtraData());
                        }
                        catch (Exception e)
                        {
                            gameLength = -1;
                        }
                    }

                    if(floorItem instanceof BanzaiTimerFloorItem)
                    {
                        try
                        {
                            gameLength = Integer.parseInt(itemSnapshot.getExtraData());
                        }
                        catch (Exception e)
                        {
                            gameLength = -1;
                        }
                        if (this.getRoom().getGame().getInstance() != null && gameLength != -1)
                        {
                            this.getRoom().getGame().getInstance().ModifyTime(0);
                            this.getRoom().getGame().getInstance().ModifyTimer(gameLength);
                        }
                    }
                }

                if (matchRotation) {
                    int currentRotation = floorItem.getRotation();
                    int newRotation = itemSnapshot.getRotation();
                    if (currentRotation != newRotation) {
                        floorItem.setRotation(newRotation);
                        rotationChanged = true;
                    }
                }

                if (stateChanged || rotationChanged) {
                    floorItem.sendUpdate();
                }
                if (matchPosition) {
                    Position currentPosition = new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY(), floorItem.getPosition().getZ());
                    Position newPosition = new Position(itemSnapshot.getX(), itemSnapshot.getY(), itemSnapshot.getZ());

                    if (this.getRoom().getItems().moveFloorItemMatch(floorItem.getId(), !matchPosition ? currentPosition : newPosition, matchRotation ? itemSnapshot.getRotation() : floorItem.getRotation(), true, true, null)) {
                        this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
                    }
                    this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition.copy(), newPosition.copy(), -1, this.getVirtualId(), floorItem.getVirtualId()));
                    floorItem.setPosition(newPosition);

                    this.getRoom().getMapping().getTile(currentPosition).reload();
                    this.getRoom().getMapping().getTile(newPosition).reload();
                }
                floorItem.save();
            }
        }
    }
}
