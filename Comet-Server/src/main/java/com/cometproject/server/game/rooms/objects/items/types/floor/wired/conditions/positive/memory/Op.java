package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.memory;

public enum Op {
    SUM("+"),
    SUBTRACT("-"),
    INCREMENT("++"),
    DECREMENT("--"),

    GREATER_THAN(">"),
    GREATER_THAN_EQUALS(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUALS("<="),
    EQUALS("=="),
    NOT_EQUALS("!="),

    MULTIPLY("*"),
    DIVIDE("/"),
    POWER("^"),

    AND("&&"),
    OR("||"),
    SQRT("sqrt", true),
    ROUND("round", true),
    ABS("abs", true)
    ;

    private final String code;
    private final boolean isFunction;

    Op(String code) {
        this.code = code;
        this.isFunction = false;
    }
    Op(String code, boolean isFunction) {
        this.code = code;
        this.isFunction = isFunction;
    }

    public String getCode() {
        return code;
    }

    public boolean isFunction() {
        return isFunction;
    }
}
