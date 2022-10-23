package com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums;

public enum TotemType {

    NONE(0),
    TROLL(1),
    SNAKE(2),
    BIRD(3);

    public final int type;

    TotemType(int type) {
        this.type = type;
    }

    public static TotemType fromInt(int type) {
        for (final TotemType totemType : TotemType.values()) {
            if (totemType.type == type)
                return totemType;
        }

        return NONE;
    }
}
