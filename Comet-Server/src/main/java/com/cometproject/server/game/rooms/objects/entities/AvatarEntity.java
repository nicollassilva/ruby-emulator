package com.cometproject.server.game.rooms.objects.entities;

import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.utilities.attributes.Attributable;

import java.util.List;
import java.util.Map;


public interface AvatarEntity extends Attributable {
    int getId();

    Position getWalkingGoal();

    void setWalkingGoal(int x, int y);

    Position getPositionToSet();

    boolean usingTeleportItem();

    void updateAndSetPosition(Position position);

    void markPositionIsSet();

    int getBodyRotation();

    void setBodyRotation(int rotation);

    int getHeadRotation();

    void setHeadRotation(int rotation);

    List<Square> getWalkingPath();

    int getAfkTime();

    void setWalkingPath(List<Square> path);

    List<Square> getProcessingPath();

    void setProcessingPath(List<Square> path);

    boolean isWalking();


    void moveTo(int x, int y);

    Map<RoomEntityStatus, String> getStatuses();

    void addStatus(RoomEntityStatus key, String value);

    void removeStatus(RoomEntityStatus key);

    boolean hasStatus(RoomEntityStatus key);

    void clearStatus();

    void markNeedsUpdate();

    boolean needsUpdate();

    void setIdle();

    int getIdleTime();

    boolean isIdleAndIncrement();

    void resetIdleTime();

    int getDanceId();

    void setDanceId(int danceId);

    int getSignTime();

    void markDisplayingSign();

    boolean isDisplayingSign();

    boolean isOverriden();

    void setOverriden(boolean overriden);

    PlayerEffect getCurrentEffect();

    void applyEffect(PlayerEffect effect);

    void carryItem(int id);

    void carryItem(int id, boolean timer);

    int getHandItem();

    boolean handItemNeedsRemove();

    int getHandItemTimer();

    void setHandItemTimer(int time);

    String getUsername();

    String getMotto();

    String getFigure();

    String getGender();

    void compose(IComposer msg);

    void warp(Position position);

    void warpImmediately(Position position);

    void warpBanzai(Position position);

    int getIdleTimeWiredWalk();

    void resetIdleTimeWiredWalk();

    void onReachedTile(RoomTile tile);

    void kick();

    long getJoinTime();
}
