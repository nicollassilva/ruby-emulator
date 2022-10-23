package com.cometproject.server.game.rooms.objects.items.types.floor.totem.enums;

public enum TotemColor {

    NONE(0),
    RED(1),
    YELLOW(2),
    BLUE(3);

    public final int color;

    TotemColor(int color) {
        this.color = color;
    }

    public static TotemColor fromInt(int color) {
        for (final TotemColor totemColor : TotemColor.values()) {
            if (totemColor.color == color)
                return totemColor;
        }

        return NONE;
    }
}
