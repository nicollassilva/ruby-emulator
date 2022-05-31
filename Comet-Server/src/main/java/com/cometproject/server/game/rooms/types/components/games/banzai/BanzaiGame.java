package com.cometproject.server.game.rooms.types.components.games.banzai;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.server.game.players.types.PlayerAvatarActions;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiTileFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.games.banzai.BanzaiTimerFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameEnds;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameStarts;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.game.rooms.types.components.games.GameType;
import com.cometproject.server.game.rooms.types.components.games.RoomGame;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;


public class BanzaiGame extends RoomGame {
    private int startBanzaiTileCount = 0;
    private int banzaiTileCount = 0;

    public BanzaiGame(Room room) {
        super(room, GameType.BANZAI);
    }

    @Override
    public void tick() {
        if (this.startBanzaiTileCount != 0 && this.banzaiTileCount == 0) {
            // Stop the game!
            this.timer = this.gameLength;
        }

        for (final RoomItemFloor item : room.getItems().getByClass(BanzaiTimerFloorItem.class)) {
            item.getItemData().setData((gameLength - timer) + "");
            item.sendUpdate();
        }

        for (final RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (entity.getEntityType().equals(RoomEntityType.PLAYER)) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                if (this.getGameComponent().getTeam(playerEntity.getPlayerId()) != GameTeam.NONE) {
                    if (playerEntity.getBanzaiPlayerAchievement() >= 60) {
                        playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.BB_PLAYER, 1);
                        playerEntity.setBanzaiPlayerAchievement(0);
                    } else {
                        playerEntity.incrementBanzaiPlayerAchievement();
                    }
                }
            }
        }
    }

    @Override
    public void onGameStarts() {
        WiredTriggerGameStarts.executeTriggers(this.room);

        this.banzaiTileCount = 0;

        for (final BanzaiTileFloorItem item : this.room.getItems().getByClass(BanzaiTileFloorItem.class)) {
            this.banzaiTileCount++;
            item.onGameStarts();
        }

        this.startBanzaiTileCount = this.banzaiTileCount;

        this.updateScoreboard(null);
    }

    @Override
    public void onGameEnds() {
        final GameTeam winningTeam = this.winningTeam();

        for (final BanzaiTileFloorItem item : this.room.getItems().getByClass(BanzaiTileFloorItem.class)) {
            if (item == null) continue;

            if (item.getTeam() == winningTeam && winningTeam != GameTeam.NONE) {
                item.flash();
            } else {
                item.onGameEnds();
            }
        }

        final List<HighscoreFloorItem> scoreboards = this.room.getItems().getByClass(HighscoreFloorItem.class);

        if (scoreboards.size() != 0) {
            final List<Integer> winningPlayers = this.room.getGame().getTeams().get(this.winningTeam());
            final List<String> winningPlayerUsernames = Lists.newArrayList();
            final List<PlayerEntity> playerEntities = Lists.newArrayList();
            final int score = this.getScore(winningTeam);

            for (final int playerId : winningPlayers) {
                final PlayerEntity player = this.room.getEntities().getEntityByPlayerId(playerId);
                playerEntities.add(player);
                winningPlayerUsernames.add(player.getUsername());
            }

            if (winningPlayerUsernames.size() != 0) {
                for (final HighscoreFloorItem scoreboard : scoreboards) {
                    scoreboard.onTeamWins(winningPlayerUsernames, playerEntities, score);
                }
            }
        }

        for (final RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (entity.getEntityType().equals(RoomEntityType.PLAYER)) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                if (this.getGameComponent().getTeam(playerEntity.getPlayerId()).equals(winningTeam) && winningTeam != GameTeam.NONE) {
                    playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.BB_WINNER, 1);
                    playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.GAME_PLAYER_EXPERIENCE, this.getGameComponent().getScore(winningTeam));

                    this.room.getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), PlayerAvatarActions.EXPRESSION_WAVE.getValue())); // wave o/
                }
            }
        }

        final Session ownerSession = NetworkManager.getInstance().getSessions().fromPlayer(this.getGameComponent().getRoom().getData().getOwnerId());

        if (ownerSession != null && winningTeam != null) {
            ownerSession.getPlayer().getAchievements().progressAchievement(AchievementType.GAME_AUTHOR_EXPERIENCE, this.getGameComponent().getScore(winningTeam));
        }

        this.getGameComponent().resetScores(true);
        WiredTriggerGameEnds.executeTriggers(this.room);
    }

    public void increaseScore(GameTeam team, int amount) {
        this.getGameComponent().increaseScore(team, amount);
        this.updateScoreboard(team);
    }

    public void updateScoreboard(GameTeam team) {
        for (final RoomItemFloor scoreboard : this.getGameComponent().getRoom().getItems().getByInteraction("%_score")) {
            if (team == null || scoreboard.getDefinition().getInteraction().toUpperCase().startsWith(team.name())) {
                scoreboard.getItemData().setData(team == null ? "0" : this.getScore(team) + "");
                scoreboard.sendUpdate();
            }
        }
    }

    public void addTile() {
        this.banzaiTileCount += 1;
        this.startBanzaiTileCount += 1;
    }

    public void removeTile() {
        this.banzaiTileCount -= 1;
        this.startBanzaiTileCount -= 1;
    }

    public int getScore(GameTeam team) {
        return this.getGameComponent().getScore(team);
    }

    public GameTeam winningTeam() {
        Map.Entry<GameTeam, Integer> winningTeam = null;

        for (final Map.Entry<GameTeam, Integer> score : this.getGameComponent().getScores().entrySet()) {
            if (winningTeam == null || winningTeam.getValue() < score.getValue()) {
                winningTeam = score;
            }
        }

        return winningTeam != null ? winningTeam.getKey() : GameTeam.NONE;
    }

    public void decreaseTileCount() {
        this.banzaiTileCount--;
    }
}
