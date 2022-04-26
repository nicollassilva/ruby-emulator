package com.cometproject.server.game.utilities.validator;

public class PlayerFigureColor {
    private final int clubCode;
    private final boolean selectable;

    public PlayerFigureColor(final int clubCode, final boolean selectable) {
        this.clubCode = clubCode;
        this.selectable = selectable;
    }

    public int getClubCode() {
        return this.clubCode;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

}

