package com.cometproject.server.game.rooms.objects.items.types.floor.games;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.rooms.models.RoomTileState;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.BetaRollableFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomEntityMovementNode;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.game.snowwar.Direction8;
import com.cometproject.server.game.utilities.DistanceCalculator;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BallonFootBall implements CometTask {

    private Direction8 rot;
    private final RoomEntity avatar;
    private final RoomItemFloor ballItem;

    public static final int KICK_POWER = 6;

    private boolean isRolling = false;
    private RoomEntity kickerEntity;
    private boolean skipNext = false;
    private boolean wasDribbling = false;
    private int rollStage = -1;

    public BallonFootBall(RoomItemFloor item, RoomEntity avt, boolean lastSteep) {
        this.rot = Direction8.getRot(avt.getPosition().getX(), avt.getPosition().getY(), item.getPosition().getX(),
                item.getPosition().getY());
        this.ballItem = item;
        this.avatar = avt;
        this.ballItem.getItemData().setData(lastSteep ? 55 : 0);
        //todo: maybe remove.
        //this.ballItem.sendUpdate();
    }


    private boolean isTileBocked(int x, int y) {
        RoomTile tile = this.ballItem.getRoom().getMapping().getTile(x, y);
        if (tile == null) {
            return true;
        }


        if (tile.getEntities().size() > 0) {
            return true;
        }


        if (tile.getMovementNode() != RoomEntityMovementNode.OPEN || tile.getState() != RoomTileState.VALID) {
            return true;
        }

        return false;

    }


    private static void roll(RoomItemFloor item, Position from, Position to, Room room) {

        final Map<Integer, Double> items = new HashMap<>();

//        items.put(item.getVirtualId(), item.getPosition().getZ());
        room.getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(from.copy(), to.copy(), item.getVirtualId(), 0, item.getVirtualId()));
    }

    public void run() {
        try {
            boolean goal = false;

            var room = this.ballItem.getRoom();

            int nextX = this.ballItem.getPosition().getX() + this.rot.getDiffX();
            int nextY = this.ballItem.getPosition().getY() + this.rot.getDiffY();


            if (isTileBocked(nextX, nextY)) {
                this.rot = this.rot.rotateDirection180Degrees();
                nextX = this.ballItem.getPosition().getX() + this.rot.getDiffX();
                nextY = this.ballItem.getPosition().getY() + this.rot.getDiffY();
            }


            var pos = new Position(nextX, nextY, this.ballItem.getPosition().getZ());

            RoomTile newTile = this.ballItem.getRoom().getMapping().getTile(pos);

            if (newTile == null) {
                return;
            }

            pos.setZ(newTile.getStackHeight());

            //roll(this.ballItem, this.ballItem.getPosition().copy(), pos.copy(), this.ballItem.getRoom());

            //roll(this, this.getPosition().copy(), pos.copy(), this.getRoom());
            var bkpItem = this.ballItem.getPosition().copy();

            RoomTile tile = this.ballItem.getRoom().getMapping().getTile(this.ballItem.getPosition());

            this.ballItem.setRotation(this.rot.getRot());

            this.ballItem.getPosition().setX(pos.getX());
            this.ballItem.getPosition().setY(pos.getY());
            this.ballItem.getPosition().setZ(pos.getZ());

            if (tile != null) {
                tile.reload();
            }

            newTile.reload();

            for (RoomItemFloor floorItem : this.ballItem.getRoom().getItems().getItemsOnSquare(pos.getX(), pos.getY())) {
                if (floorItem != null)
                    floorItem.onItemAddedToStack(this.ballItem);
            }


            this.ballItem.getPosition().setZ(pos.getZ());
            this.ballItem.save();


            if (goal) {
                this.ballItem.getItemData().setData(11);
                this.ballItem.sendUpdate();
                return;
            }

            if (this.ballItem.getItemData().getIntData() == 55) {

                CometThreadManager.getInstance().executeSchedule(
                        this,
                        100, TimeUnit.MILLISECONDS);
            } else if (this.ballItem.getItemData().getIntData() == 44) {

                CometThreadManager.getInstance().executeSchedule(
                        this,
                        100, TimeUnit.MILLISECONDS);
            } else if (this.ballItem.getItemData().getIntData() == 33) {

                CometThreadManager.getInstance().executeSchedule(
                        this,
                        200, TimeUnit.MILLISECONDS);
            } else if (this.ballItem.getItemData().getIntData() == 22) {
                CometThreadManager.getInstance().executeSchedule(
                        this,
                        250, TimeUnit.MILLISECONDS);

            } else if (this.ballItem.getItemData().getIntData() == 11) {

                CometThreadManager.getInstance().executeSchedule(
                        this,
                        500, TimeUnit.MILLISECONDS);
            } else {
                this.ballItem.getItemData().setData(11);
            }

            roll(this.ballItem, bkpItem, pos.copy(), this.ballItem.getRoom());

            //     this.ballItem.sendUpdate();

            this.ballItem.getItemData().decItemData(11);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


}
