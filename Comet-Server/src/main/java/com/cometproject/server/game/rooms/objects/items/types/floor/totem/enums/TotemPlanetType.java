package com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums;

public enum TotemPlanetType {
    MOON(0),
    SUN(1),
    EARTH(2);

    public final int type;

    TotemPlanetType(int type) {
        this.type = type;
    }

    public static TotemPlanetType fromInt(int type) {
        for (final TotemPlanetType planetType : TotemPlanetType.values()) {
            if (planetType.type == type)
                return planetType;
        }

        return MOON;
    }
}
