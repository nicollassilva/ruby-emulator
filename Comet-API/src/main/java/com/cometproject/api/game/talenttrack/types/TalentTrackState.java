package com.cometproject.api.game.talenttrack.types;

public enum TalentTrackState {
    LOCKED(0),
    IN_PROGRESS(1),
    COMPLETED(2);

    public final int id;

    TalentTrackState(int id) {
        this.id = id;
    }
}
