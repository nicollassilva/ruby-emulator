package com.cometproject.server.game.utilities.validator;

public class PlayerFigureSet {
    private final String gender;
    private final int clubCode;
    private final boolean colorable;
    private final boolean selectable;
    private final int colorCount;

    public PlayerFigureSet(final String gender, final int clubCode, final boolean colorable, final boolean selectable, final int colorCount) {
        this.gender = gender;
        this.clubCode = clubCode;
        this.colorable = colorable;
        this.selectable = selectable;
        this.colorCount = colorCount;
    }

    public String getGender() {
        return this.gender;
    }

    public int getClubCode() {
        return this.clubCode;
    }

    public boolean isColorable() {
        return this.colorable;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

    public int getColorCount() {
        return this.colorCount;
    }
}

