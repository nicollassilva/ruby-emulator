package com.cometproject.server.game.rooms.types.misc;

public class NameColor {
    private final int id;
    private final String name;
    private final String colorCode;
    private final int minRank;
    private final int colorsLength;

    public NameColor(int id, String name, int minRank, String colorCode) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
        this.minRank = minRank;
        this.colorsLength = (this.colorCode.split(",")).length;
    }

    public int getId() {
        return id;
    }

    public int getColorsLength() {
        return colorsLength;
    }

    public int getMinRank() {
        return minRank;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getName() {
        return name;
    }
}
