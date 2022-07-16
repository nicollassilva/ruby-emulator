package com.cometproject.server.game.utilities.validator;

public class FiguredataSettypeSet {
    public int id;
    public FigureGender gender;
    public boolean club;
    public boolean colorable;
    public boolean selectable;
    public boolean preselectable;
    public boolean sellable;

    public FiguredataSettypeSet(int id, FigureGender gender, boolean club, boolean colorable, boolean selectable, boolean preselectable, boolean sellable) {
        this.id = id;
        this.gender = gender;
        this.club = club;
        this.colorable = colorable;
        this.selectable = selectable;
        this.preselectable = preselectable;
        this.sellable = sellable;
    }
}

