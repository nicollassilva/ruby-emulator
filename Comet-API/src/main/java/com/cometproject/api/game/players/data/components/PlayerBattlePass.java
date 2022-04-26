package com.cometproject.api.game.players.data.components;

import com.cometproject.api.game.battlepass.types.BattlePassType;
import com.cometproject.api.game.players.data.IPlayerComponent;
import com.cometproject.api.game.players.data.components.battlepass.IBattlePassProgress;

public interface PlayerBattlePass extends IPlayerComponent {

    void progressBattlePass(BattlePassType type, int data);

    void loadBattlePass();

    boolean hasStartedHomework(BattlePassType battlePassType);

    IBattlePassProgress getProgress(BattlePassType battlePassType);
}
