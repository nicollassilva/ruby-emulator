package com.cometproject.api.game.battlepass.types;

public enum BattlePassType {
    PLACE_ITEM("BP_PlaceItem"),
    USE_NBLOVE("BP_UseNBLove");

    private final String battlePassHomework;

    BattlePassType(String battlePassHomework) { this.battlePassHomework = battlePassHomework; }

    public String getBattlePassHomework() { return battlePassHomework; }

    public static BattlePassType getTypeByName(String name) {
        for(BattlePassType type : BattlePassType.values()) {
            if(type.battlePassHomework.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
