package com.cometproject.server.game.rooms.objects.items.types.floor.football;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;


public class FootballScoreFloorItem extends RoomItemFloor {
    private GameTeam gameTeam;

    public FootballScoreFloorItem(RoomItemData roomItemData, Room room) {
        super(roomItemData, room);

        this.getItemData().setData("0");

        switch (this.getDefinition().getItemName()) {
            case "fball_score_b":
                this.gameTeam = GameTeam.BLUE;
                break;
            case "fball_score_r":
                this.gameTeam = GameTeam.RED;
                break;
            case "fball_score_y":
                this.gameTeam = GameTeam.YELLOW;
                break;
            case "fball_score_g":
                this.gameTeam = GameTeam.GREEN;
                break;
        }
    }

    public void sendUpdate() {
        this.getItemData().setData(this.getRoom().getGame().getScore(this.gameTeam) + "");

        super.sendUpdate();
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTriggered) {
        if (!isWiredTriggered) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity) entity;

            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId())
                    && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return true;
            }
        }
        switch(requestData) {
            case 1:
                this.getRoom().getGame().increaseScore(this.gameTeam, 1);
                break;
            case 2:
                if(this.getRoom().getGame().getScore(this.gameTeam) > 0)
                    this.getRoom().getGame().decreaseScore(this.gameTeam, 1);
                break;
            default: this.getRoom().getGame().getScores().replace(gameTeam, 0); break;
        }
        this.sendUpdate();
        return true;
    }

    public void reset() {
        this.getItemData().setData(0 + "");
        this.sendUpdate();
    }
}
