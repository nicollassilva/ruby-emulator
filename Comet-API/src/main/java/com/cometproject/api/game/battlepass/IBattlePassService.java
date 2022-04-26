package com.cometproject.api.game.battlepass;

import com.cometproject.api.game.battlepass.types.BattlePassType;
import com.cometproject.api.game.battlepass.types.IBattlePassHomework;
import com.cometproject.api.utilities.Initialisable;

import java.util.Map;

public interface IBattlePassService extends Initialisable {
    void loadBattlePass();

    IBattlePassHomework getBattlePassHomework(BattlePassType battlePassType);

    Map<BattlePassType, IBattlePassHomework> getBattlePassHomeworks();
}
