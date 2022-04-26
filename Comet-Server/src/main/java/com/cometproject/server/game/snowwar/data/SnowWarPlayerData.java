package com.cometproject.server.game.snowwar.data;

import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.snowwar.SnowBallGameObject;
import com.cometproject.server.game.snowwar.SnowPlayerQueue;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.Tile;
import com.cometproject.server.game.snowwar.gameevents.*;
import com.cometproject.server.game.snowwar.gameobjects.GameItemObject;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SnowWarPlayerData {
    public Player player;
    public SnowWarRoom currentSnowWar;
    public HumanGameObject humanObject;

    public int snowLevel;
    public int PointsNeed;

    private final int rank;
    private final int score;

    public SnowWarPlayerData(Player playerData) {
        player = playerData;
        snowLevel = 1;
        rank = 1;
        score = 10;
        PointsNeed = 100;
    }

    public void userLeft() {
        if (humanObject != null && currentSnowWar != null) {
            SnowPlayerQueue.playerExit(currentSnowWar, humanObject);
        }
    }

    public void makeSnowBall() {
        synchronized(currentSnowWar.gameEvents) {
            currentSnowWar.gameEvents.add(new MakeSnowBall(humanObject));
        }
    }

    public void playerMove(int x, int y) {
        synchronized(currentSnowWar.gameEvents) {
            currentSnowWar.gameEvents.add(new UserMove(humanObject, x, y));
        }
    }

    public void throwSnowballFlood(int destX, int destY) {
        final SnowBallGameObject ball = new SnowBallGameObject(currentSnowWar);
        ball.objectId = currentSnowWar.objectIdCounter++;
        final SnowBallGameObject ball1 = new SnowBallGameObject(currentSnowWar);
        ball1.objectId = currentSnowWar.objectIdCounter++;
        final SnowBallGameObject ball2 = new SnowBallGameObject(currentSnowWar);
        ball2.objectId = currentSnowWar.objectIdCounter++;
        final SnowBallGameObject ball3 = new SnowBallGameObject(currentSnowWar);
        ball3.objectId = currentSnowWar.objectIdCounter++;
        final SnowBallGameObject ball4 = new SnowBallGameObject(currentSnowWar);
        ball4.objectId = currentSnowWar.objectIdCounter++;
        final SnowBallGameObject ball5 = new SnowBallGameObject(currentSnowWar);
        ball5.objectId = currentSnowWar.objectIdCounter++;
        final SnowBallGameObject ball6 = new SnowBallGameObject(currentSnowWar);
        ball6.objectId = currentSnowWar.objectIdCounter++;
        synchronized(currentSnowWar.gameEvents) {
            currentSnowWar.gameEvents.add(new BallThrowToPosition(humanObject, destX * Tile.TILE_SIZE, destY * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball, humanObject, destX * Tile.TILE_SIZE, destY * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball4, humanObject, destX * Tile.TILE_SIZE, (destY + 1) * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball1, humanObject, (destX + 1) * Tile.TILE_SIZE, destY * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball6, humanObject, (destX - 1) * Tile.TILE_SIZE, (destY + 1) * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball2, humanObject, (destX - 1) * Tile.TILE_SIZE, (destY - 1) * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball3, humanObject, (destX + 1) * Tile.TILE_SIZE, (destY - 1) * Tile.TILE_SIZE, 3));
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball5, humanObject, (destX + 1) * Tile.TILE_SIZE, (destY + 1) * Tile.TILE_SIZE, 3));
        }
    }

    public void throwSnowballAtHuman(int victim, int type) {
        if(!humanObject.canThrowSnowBall()) {
            return;
        }

        final GameItemObject vict = currentSnowWar.gameObjects.get(victim);
        if(vict == null) {
            return;
        }

        final SnowBallGameObject ball = new SnowBallGameObject(currentSnowWar);
        ball.objectId = currentSnowWar.objectIdCounter++;
        synchronized(currentSnowWar.gameEvents) {
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball, humanObject, vict.location3D().x(), vict.location3D().y(), type));
            currentSnowWar.gameEvents.add(new BallThrowToHuman(humanObject, (HumanGameObject)vict, 0));
        }
    }

    public void throwSnowballAtPosition(int destX, int destY, int type) {
        if(!humanObject.canThrowSnowBall()) {
            return;
        }

        final SnowBallGameObject ball = new SnowBallGameObject(currentSnowWar);
        ball.objectId = currentSnowWar.objectIdCounter++;
        synchronized(currentSnowWar.gameEvents) {
            currentSnowWar.gameEvents.add(new CreateSnowBall(ball, humanObject, destX, destY, type));
            currentSnowWar.gameEvents.add(new BallThrowToPosition(humanObject, destX, destY, 0));
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public SnowWarRoom getSnowRoom() {
        return this.currentSnowWar;
    }

    public HumanGameObject getHumanObject() {
        return this.humanObject;
    }

    public int getSnowLevel() {
        return this.snowLevel;
    }

    public int getPointsNeed() {
        return this.PointsNeed;
    }

    public int getRank() {
        return this.rank;
    }

    public int getScore() {
        return this.score;
    }

    public void setHumanObject(HumanGameObject humanGameObject) {
        if(humanGameObject == null) {
            humanObject.snowWarPlayer = null;
            humanObject.cn = null;
            humanObject.userId = 0;
            humanObject = null;
        } else {
            humanObject = humanGameObject;
            humanObject.snowWarPlayer = this;

            humanObject.cn = player.getSession();
            humanObject.userId = player.getData().getId();
            humanObject.userName = player.getData().getUsername();
            humanObject.look = player.getData().getFigure();
            humanObject.motto = player.getData().getMotto();
            humanObject.sex = player.getData().getGender().toUpperCase();
        }
    }

    public void setRoom(SnowWarRoom room) {
        currentSnowWar = room;
        humanObject.currentSnowWar = room;
    }
}
