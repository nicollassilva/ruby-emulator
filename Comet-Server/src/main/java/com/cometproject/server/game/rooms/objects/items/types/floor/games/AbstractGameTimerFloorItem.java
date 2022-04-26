package com.cometproject.server.game.rooms.objects.items.types.floor.games;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameState;
import com.cometproject.server.game.rooms.types.components.games.GameType;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractGameTimerFloorItem extends RoomItemFloor {
    private String lastTime;

    public AbstractGameTimerFloorItem(RoomItemData itemData, Room room) {
        super(itemData, room);
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
                return false;
            }
        }

        InteractionGameTimerAction action = InteractionGameTimerAction.getByAction(requestData);
        if (action == InteractionGameTimerAction.INCREASE_TIME) {
            if(this.getRoom().getGame().getInstance() != null) {
                if(this.getRoom().getGame().getInstance().getState().equals(GameState.RUNNING)) return false;
                if(this.getRoom().getGame().getInstance().getState().equals(GameState.PAUSED)) {
                    this.getRoom().getGame().getInstance().onGameEnds();
                    this.getRoom().getGame().stop();
                }
            }
            int time = 0;

            if (!this.getItemData().getData().isEmpty() && StringUtils.isNumeric(this.getItemData().getData())) {
                time = Integer.parseInt(this.getItemData().getData());
            }

            if (time == 0 || time == 30 || time == 60 || time == 120 || time == 180 || time == 300 || time == 600) {
                switch (time) {
                    default:
                        time = 0;
                        break;
                    case 0:
                        time = 30;
                        break;
                    case 30:
                        time = 60;
                        break;
                    case 60:
                        time = 120;
                        break;
                    case 120:
                        time = 180;
                        break;
                    case 180:
                        time = 300;
                        break;
                    case 300:
                        time = 600;
                        break;
                }
            } else {
                time = 0;
            }

            this.getItemData().setData(time + "");
            this.sendUpdate();
            this.saveData();
        } else { // pause/start game
            if (this.getItemData().getData().equals("0") && this.lastTime != null && !this.lastTime.isEmpty()) {
                this.getItemData().setData(this.lastTime);
            }

            int gameLength = Integer.parseInt(this.getItemData().getData());

            if (gameLength == 0) return true;

            if (this.getRoom().getGame().getInstance() == null) {
                this.lastTime = this.getItemData().getData();
                this.getRoom().getGame().createNew(this.getGameType());
                this.getRoom().getGame().getInstance().startTimer(gameLength);
            }
            else if(this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance().getState().equals(GameState.RUNNING)) {
                this.getRoom().getGame().pause();
            }
            else if(this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance().getState().equals(GameState.PAUSED)) {
                this.getRoom().getGame().getInstance().startTimer(this.getRoom().getGame().getInstance().getGameLength());
            }
        }

        return true;
    }

    @Override
    public void onPickup() {
        if (this.getRoom().getGame().getInstance() != null) {
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
        }
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public abstract GameType getGameType();

    public enum InteractionGameTimerAction {
        START_PAUSE(1),
        INCREASE_TIME(2);

        private final int action;

        InteractionGameTimerAction(int action) {
            this.action = action;
        }

        public int getAction() {
            return action;
        }

        public static InteractionGameTimerAction getByAction(int action) {
            if (action == 1) return START_PAUSE;
            if (action == 2) return INCREASE_TIME;

            return START_PAUSE;
        }
    }
}
