package com.cometproject.api.game.rooms.entities;

public enum RoomEntityStatus {
    SIT_IN("sit-in"),
    SIT("sit", true),
    SIT_OUT("sit-out"),

    MOVE("mv", true),

    LAY_IN("lay-in"),
    LAY("lay"),
    LAY_OUT("lay-out"),

    SIGN("sign"),
    CONTROLLER("flatctrl"),
    TRADE("trd"),
    VOTE("vote"),
    GESTURE("gst"),

    PLAY_IN("pla-in"),
    PLAY("pla", true),
    PLAY_OUT("pla-out"),

    PLAY_DEAD_IN("ded-in"),
    PLAY_DEAD("ded", true),
    PLAY_DEAD_OUT("ded-out"),

    JUMP_IN("jmp-in"),
    JUMP("jmp", true),
    JUMP_OUT("jmp-out"),

    EAT_IN("eat-in"),
    EAT("eat"),
    EAT_OUT("eat-out"),

    SLEEP_IN("slp-in"),
    SLEEP("slp"),
    SLEEP_OUT("slp-out"),

    DIP("dip"),
    BEG("beg", true),
    RDY("rdy"),
    SCRATCH("scr"),
    SPEAK("spk"),
    CROAK("crk"),
    RELAX("rlx"),
    WINGS("wng", true),
    FLAME("flm"),
    KICK("kck"),
    WAG_TAIL("wag"),
    DANCE("dan"),
    AMS("ams"),
    SWIM("swm"),
    TURN("trn"),
    FLASH("spd"),

    SRP_IN("srp-in"),
    SRP("srp"),

    RIP("rip"),

    GROW("grw"),
    GROW_1("grw1"),
    GROW_2("grw2"),
    GROW_3("grw3"),
    GROW_4("grw4"),
    GROW_5("grw5"),
    GROW_6("grw6"),
    GROW_7("grw7"),
    SAD("sad"),
    HAPPY("sml");

    private final String statusCode;
    public final boolean removeWhenWalking;

    RoomEntityStatus(String statusCode) {
        this.statusCode = statusCode;
        this.removeWhenWalking = false;
    }

    RoomEntityStatus(String key, boolean removeWhenWalking) {
        this.statusCode = key;
        this.removeWhenWalking = removeWhenWalking;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public static RoomEntityStatus fromString(String key) {
        for (RoomEntityStatus status : RoomEntityStatus.values()) {
            if (!status.statusCode.equals(key)) continue;
            return status;
        }
        return null;
    }
}
