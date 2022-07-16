package com.cometproject.server.game.utilities.validator;

public enum FigureGender {
    Unisex("U"),
    Male("M"),
    Female("F");

    private final String code;

    FigureGender(String code) {
        this.code = code;
    }

    public static FigureGender fromString(String code){
        final String lowered = code.toUpperCase();
        for (final FigureGender gender : values()) {
            if(gender.code.equals(lowered))
                return gender;
        }

        return FigureGender.Unisex;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.getCode();
    }
}
