package com.cometproject.server.game.rooms.objects.items.types.floor.football;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.types.PlayerAvatarActions;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class FootballGoalFloorItem extends RoomItemFloor {
    private GameTeam gameTeam;

    public FootballGoalFloorItem(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        switch (this.getDefinition().getItemName()) {
            case "fball_goal_b":
                this.gameTeam = GameTeam.BLUE;
                break;
            case "fball_goal_r":
                this.gameTeam = GameTeam.RED;
                break;
            case "fball_goal_y":
                this.gameTeam = GameTeam.YELLOW;
                break;
            case "fball_goal_g":
                this.gameTeam = GameTeam.GREEN;
                break;
        }
    }

    @Override
    public void onItemAddedToStack(RoomItemFloor floorItem) {
        if (!(floorItem instanceof FootballFloorItem))
            return;


        final FootballFloorItem ball = (FootballFloorItem) floorItem;

        if(ball.getPusher() == null) return;

        this.getRoom().getGame().increaseScore(this.gameTeam, 1);

        final int playerId = (
                ball.getPusher() != null && ball.getPusher() instanceof PlayerEntity
                        ? ((PlayerEntity) ball.getPusher()).getPlayerId()
                        : this.getRoom().getData().getOwnerId()
        );
        if (PlayerManager.getInstance().isOnline(playerId)) {
            final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session != null && session.getPlayer() != null && session.getPlayer().getAchievements() != null) {
                session.getPlayer().getAchievements().progressAchievement(AchievementType.FOOTBALL_GOAL, 1);

                if(playerId == this.getRoom().getData().getOwnerId()) {
                    session.getPlayer().getAchievements().progressAchievement(AchievementType.FOOTBALL_GOAL_ON_ROOM, 1);
                }

                nearestPlayerEntity().getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new ActionMessageComposer(session.getPlayer().getEntity().getId(), PlayerAvatarActions.EXPRESSION_RESPECT.getValue()));
            }
        }
    }

    public GameTeam getGameTeam() {
        return gameTeam;
    }
}
