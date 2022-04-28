package com.cometproject.server.game.rooms.objects.items.types.floor.games.battleball;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiPuckFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.game.rooms.types.components.games.battleball.BattleBallGame;

import java.util.LinkedList;
import java.util.List;

public class BattleBallTileFloorItem extends RoomItemFloor {
    private GameTeam gameTeam = GameTeam.NONE;
    private int points = 0;
    private boolean needsChange = false;
    private int ticker = 0;

    public BattleBallTileFloorItem(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);
        this.getItemData().setData("0");
    }

    private static List<BattleBallTileFloorItem> buildBanzaiRectangle(BattleBallTileFloorItem triggerItem, int x, int y, int goX, int goY, int currentDirection, int turns, GameTeam team) {
        final boolean[] directions = new boolean[4];

        if (goX == -1 || goX == 0) {
            directions[0] = true;
        }
        if (goX == 1 || goX == 0) {
            directions[2] = true;
        }
        if (goY == -1 || goY == 0) {
            directions[1] = true;
        }
        if (goY == 1 || goY == 0) {
            directions[3] = true;
        }

        if ((goX != 0 || goY != 0) && triggerItem.getPosition().getX() == x && triggerItem.getPosition().getY() == y) {
            return new LinkedList<>();
        }

        final Room room = triggerItem.getRoom();

        for (int i = 0; i < 4; ++i) {
            BattleBallTileFloorItem item;
            RoomItemFloor obj;

            if (!directions[i]) continue;

            int nextXStep = 0;
            int nextYStep = 0;

            if (i == 0 || i == 2) {
                nextXStep = i == 0 ? 1 : -1;
            } else if (i == 1 || i == 3) {
                nextYStep = i == 1 ? 1 : -1;
            }

            int nextX = x + nextXStep;
            int nextY = y + nextYStep;

            if (room.getMapping().getTile(nextX, nextY) == null || (obj = room.getItems().getFloorItem(room.getMapping().getTile(nextX, nextY).getTopItem())) == null || !(obj instanceof BattleBallTileFloorItem) || (item = (BattleBallTileFloorItem)obj).getTeam() != team || item.getPoints() != 3) continue;

            List<BattleBallTileFloorItem> foundPatches = null;

            if (currentDirection != i && currentDirection != -1) {
                if (turns > 0) {
                    foundPatches = BattleBallTileFloorItem.buildBanzaiRectangle(triggerItem, nextX, nextY, nextXStep == 0 ? goX * -1 : nextXStep * -1, nextYStep == 0 ? goY * -1 : nextYStep * -1, i, turns - 1, team);
                }
            } else {
                foundPatches = BattleBallTileFloorItem.buildBanzaiRectangle(triggerItem, nextX, nextY, nextXStep == 0 ? goX : nextXStep * -1, nextYStep == 0 ? goY : nextYStep * -1, i, turns, team);
            }

            if (foundPatches == null) continue;

            foundPatches.add(item);

            return foundPatches;
        }

        return null;
    }

    @Override
    public void onPickup() {
        if (!(this.getRoom().getGame().getInstance() instanceof BattleBallGame)) {
            return;
        }
        ((BattleBallGame)this.getRoom().getGame().getInstance()).removeTile();
    }

    @Override
    public void onPlaced() {
        if (!(this.getRoom().getGame().getInstance() instanceof BattleBallGame)) {
            return;
        }
        ((BattleBallGame)this.getRoom().getGame().getInstance()).addTile();
    }

    @Override
    public void onEntityPostStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).getGameTeam() == GameTeam.NONE || !(this.getRoom().getGame().getInstance() instanceof BattleBallGame)) {
            return;
        }

        if (this.points == 3) {
            return;
        }

        if (((PlayerEntity)entity).getGameTeam() == this.gameTeam) {
            ++this.points;
        } else {
            this.gameTeam = ((PlayerEntity)entity).getGameTeam();
            this.points = 1;
        }

        if (this.points == 3) {
            //((PlayerEntity)entity).getPlayer().getAchievements().progressAchievement(AchievementType.ACH_10, 1);
            ((BattleBallGame)this.getRoom().getGame().getInstance()).increaseScore(this.gameTeam, 1);
            ((BattleBallGame)this.getRoom().getGame().getInstance()).decreaseTileCount();

            final List<BattleBallTileFloorItem> rectangle = BattleBallTileFloorItem.buildBanzaiRectangle(this, this.getPosition().getX(), this.getPosition().getY(), 0, 0, -1, 4, this.gameTeam);

            if (rectangle != null) {
                for (final BattleBallTileFloorItem roomItemFloor : this.getRoom().getItems().getByClass(BattleBallTileFloorItem.class)) {
                    if (roomItemFloor.getPoints() == 3) continue;

                    final boolean[] borderCheck = new boolean[4];

                    for (final BattleBallTileFloorItem rectangleItem : rectangle) {
                        if (rectangleItem.getPosition().getY() == roomItemFloor.getPosition().getY()) {
                            if (rectangleItem.getPosition().getX() > roomItemFloor.getPosition().getX()) {
                                borderCheck[0] = true;
                                continue;
                            }
                            borderCheck[1] = true;
                            continue;
                        }

                        if (rectangleItem.getPosition().getX() != roomItemFloor.getPosition().getX()) continue;

                        if (rectangleItem.getPosition().getY() > roomItemFloor.getPosition().getY()) {
                            borderCheck[2] = true;
                            continue;
                        }

                        borderCheck[3] = true;
                    }

                    if (!borderCheck[0] || !borderCheck[1] || !borderCheck[2] || !borderCheck[3] || roomItemFloor.getId() == this.getId()) continue;

                    roomItemFloor.setPoints(3);
                    roomItemFloor.setTeam(this.gameTeam);
                    //((PlayerEntity)entity).getPlayer().getAchievements().progressAchievement(AchievementType.ACH_10, 1);
                    ((BattleBallGame)this.getRoom().getGame().getInstance()).increaseScore(this.gameTeam, 1);
                    ((BattleBallGame)this.getRoom().getGame().getInstance()).decreaseTileCount();
                    roomItemFloor.updateTileData();
                }
            }
        }

        this.updateTileData();
    }

    @Override
    public void onTick() {
        if (this.hasTicks() && this.ticker >= RoomItemFactory.getProcessTime(0.5)) {
            if (this.needsChange) {
                this.getItemData().setData("1");
                this.sendUpdate();
                this.needsChange = false;
            } else {
                this.needsChange = true;
                this.updateTileData();
            }
            this.ticker = 0;
        }

        ++this.ticker;
    }

    @Override
    public void onTickComplete() {
        this.updateTileData();
    }

    public void flash() {
        if (this.points == 3) {
            this.needsChange = true;
            this.setTicks(RoomItemFactory.getProcessTime(3.5));
        }
    }

    public void onGameStarts() {
        this.gameTeam = GameTeam.NONE;
        this.points = 0;
        this.updateTileData();
    }

    public void onGameEnds() {
        this.getItemData().setData("0");
        this.sendUpdate();
    }

    public void updateTileData() {
        if (this.points != 0) {
            this.getItemData().setData(this.points + this.gameTeam.getTeamId() * 3 - 1 + "");
        } else {
            this.getItemData().setData("1");
        }
        this.sendUpdate();
    }

    public GameTeam getTeam() {
        return this.gameTeam;
    }

    public void setTeam(GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}


