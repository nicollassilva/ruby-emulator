package com.cometproject.server.game.rooms.types.components.games.battleball;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.battleball.BattleBallTileFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.battleball.BattleBallTimerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.freeze.FreezeExitFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameEnds;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameStarts;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.game.rooms.types.components.games.GameType;
import com.cometproject.server.game.rooms.types.components.games.RoomGame;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class BattleBallGame extends RoomGame {
    private int startBattleBallTileCount = 0;
    private int battleBallTileCount = 0;

    public BattleBallGame(Room room) {
        super(room, GameType.BATTLEBALL);
    }

    @Override
    public void tick() {
        if (this.startBattleBallTileCount != 0 && this.battleBallTileCount == 0) {
            // Stop the game!
            this.timer = this.gameLength;
        }

        for (RoomItemFloor item : room.getItems().getByClass(BattleBallTimerFloorItem.class)) {
            item.getItemData().setData((gameLength - timer) + "");
            item.sendUpdate();
        }

        for (RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (entity.getEntityType().equals(RoomEntityType.PLAYER)) {
                PlayerEntity playerEntity = (PlayerEntity) entity;

                if (this.getGameComponent().getTeam(playerEntity.getPlayerId()) != GameTeam.NONE) {
                    if (playerEntity.getBanzaiPlayerAchievement() >= 60) {
                        //playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.ACH_9, 1);
                        playerEntity.setBanzaiPlayerAchievement(0);
                    } else {
                        playerEntity.incrementBanzaiPlayerAchievement();
                    }
                }
            }
        }
    }

    public void updateTiles() {
        for (RoomItemFloor item : this.room.getItems().getByClass(BattleBallTileFloorItem.class)) {
            this.battleBallTileCount++;
            ((BattleBallTileFloorItem) item).onGameStarts();
        }
    }

    @Override
    public void onGameStarts() {
        for (final RoomEntity entityStart : this.room.getEntities().getAllEntities().values()) {
            if (entityStart.getEntityType().equals(RoomEntityType.PLAYER)) {
                WiredTriggerGameStarts.executeTriggers(entityStart.getRoom());
            }
        }

        this.battleBallTileCount = 0;

        for (BattleBallTileFloorItem item : this.room.getItems().getByClass(BattleBallTileFloorItem.class)) {
            if(item == null) continue;

            this.battleBallTileCount++;
            item.onGameStarts();
        }

        this.startBattleBallTileCount = this.battleBallTileCount;

        this.updateScoreboard(null);
    }

    @Override
    public void onGameEnds() {
        final GameTeam winningTeam = this.winningTeam();

        for (final BattleBallTileFloorItem item : this.room.getItems().getByClass(BattleBallTileFloorItem.class)) {
            if (item != null) {
                if (item.getTeam() == winningTeam && winningTeam != GameTeam.NONE) {
                    item.flash();
                } else {
                    item.onGameEnds();
                }
            }
        }

        final List<Long> exitid = Lists.newArrayList();

        for (final RoomItemFloor item : this.room.getItems().getByClass(FreezeExitFloorItem.class)) {
            if (item instanceof FreezeExitFloorItem) {
                exitid.add(item.getId());
            }
        }

        for (final RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (entity.getEntityType().equals(RoomEntityType.PLAYER)) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                if (this.getGameComponent().getTeam(playerEntity.getPlayerId()).equals(winningTeam) && winningTeam != GameTeam.NONE) {
                    //playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.BB_WINNER, 1);
                    this.room.getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), 1)); // wave o/
                } else if (playerEntity.getGameTeam() != GameTeam.NONE && playerEntity.getGameTeam() != null) {
                    this.room.getGame().removeFromTeam(playerEntity);

                    final Long itemId = WiredUtil.getRandomElement(exitid);

                    if (itemId == null) {
                        continue;
                    }

                    final RoomItemFloor item = this.room.getItems().getFloorItem(itemId);

                    if (item == null || item.isAtDoor() || item.getPosition() == null || item.getTile() == null) {
                        continue;
                    }

                    final Position position = new Position(item.getPosition().getX(), item.getPosition().getY(), item.getTile().getWalkHeight());

                    entity.applyEffect(new PlayerEffect(4, 5));
                    entity.cancelWalk();
                    entity.warp(position);
                }

                WiredTriggerGameEnds.executeTriggers(entity.getRoom());
            }
        }

        /*for (RoomItemFloor item : room.getItems().getByClass(WiredActionSetScore.class)) {
            if (item instanceof WiredActionSetScore) {
                item.SetMaxGiveScore(1);
            }
        }

        for (RoomItemFloor item : room.getItems().getByClass(WiredActionGiveScore.class)) {
            if (item instanceof WiredActionGiveScore) {
                item.SetMaxGiveScore(1);
            }
        }*/

        this.getGameComponent().resetScores();

        exitid.clear();
    }

    public void increaseScore(GameTeam team, int amount) {
        this.getGameComponent().increaseScore(team, amount);
        this.updateScoreboard(team);
    }

    public void updateScoreboard(GameTeam team) {
        for (RoomItemFloor scoreboard : this.getGameComponent().getRoom().getItems().getByInteraction("%_score")) {
            if (team == null || scoreboard.getDefinition().getInteraction().toUpperCase().startsWith(team.name())) {
                scoreboard.getItemData().setData(team == null ? "0" : this.getScore(team) + "");
                scoreboard.sendUpdate();
            }
        }
    }

    public void addTile() {
        this.battleBallTileCount += 1;
        this.startBattleBallTileCount += 1;
    }

    public void removeTile() {
        this.battleBallTileCount -= 1;
        this.startBattleBallTileCount -= 1;
    }

    public int getScore(GameTeam team) {
        return this.getGameComponent().getScore(team);
    }

    public GameTeam winningTeam() {
        boolean equality = false;
        Map.Entry<GameTeam, Integer> winningTeam = null;

        for (final Map.Entry<GameTeam, Integer> score : this.getGameComponent().getScores().entrySet()) {
            if (winningTeam == null || winningTeam.getValue() < score.getValue()) {

                winningTeam = score;
                equality = false;

            } else if (winningTeam.getValue().equals(score.getValue())) {
                equality = true;
            }
        }

        return winningTeam != null && !equality ? winningTeam.getKey() : GameTeam.NONE;
    }

    public void decreaseTileCount() {
        this.battleBallTileCount--;
    }

}