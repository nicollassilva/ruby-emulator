package com.cometproject.server.game.rooms.types.mapping;

public class RoomTileStatus {
    private final RoomTileStatusType statusType;
    private final int effectId;

    private final int positionX;
    private final int positionY;
    private final int rotation;

    private final double interactionHeight;

    public RoomTileStatus(RoomTileStatusType type, int effectId, int positionX, int positionY, int rotation, double interactionHeight) {
        this.statusType = type;
        this.effectId = effectId;

        this.positionX = positionX;
        this.positionY = positionY;
        this.rotation = rotation;

        this.interactionHeight = interactionHeight;
    }

    public double getInteractionHeight() {
        return this.interactionHeight;
    }
}
