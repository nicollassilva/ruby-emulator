package com.cometproject.server.game.battlepass;

import com.cometproject.api.game.battlepass.IBattlePassService;
import com.cometproject.api.game.battlepass.types.BattlePassType;
import com.cometproject.api.game.battlepass.types.IBattlePassHomework;
import com.cometproject.server.storage.queries.battlepass.BattlePassDao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BattlePassManager implements IBattlePassService {
    private static final Logger log = LogManager.getLogger(BattlePassManager.class.getName());
    private static BattlePassManager battlePassManager;
    private final Map<BattlePassType, IBattlePassHomework> battlePassHomeworks;
    private final Map<Integer, Map<BattlePassType, BattlePassGroup>> gameBattlePassHomework;

    public BattlePassManager() {
        this.battlePassHomeworks = new ConcurrentHashMap<>();
        this.gameBattlePassHomework = new ConcurrentHashMap<>();
    }

    public static BattlePassManager getInstance() {
        if(battlePassManager == null) {
            battlePassManager = new BattlePassManager();
        }

        return battlePassManager;
    }

    @Override
    public void initialize() {
        this.loadBattlePass();

        log.info("BattlePass initialized");
    }

    @Override
    public void loadBattlePass() {
        if(this.battlePassHomeworks.size() != 0) {
            for(IBattlePassHomework battlePassHomework : this.battlePassHomeworks.values()) {
                if(battlePassHomework.getBattlepass().size() != 0) {
                    battlePassHomework.getBattlepass().clear();
                }
            }

            this.battlePassHomeworks.clear();
        }

        final int battlePassCount = BattlePassDao.getHomeworks(this.battlePassHomeworks);

        log.info("Loaded " + battlePassCount + " homeworks of battle pass");
    }

    @Override
    public IBattlePassHomework getBattlePassHomework(BattlePassType homeworkName) {
        return this.battlePassHomeworks.get(homeworkName);
    }

    public Map<Integer, Map<BattlePassType, BattlePassGroup>> getGameBattlePassHomework() {
        return this.gameBattlePassHomework;
    }

    @Override
    public Map<BattlePassType, IBattlePassHomework> getBattlePassHomeworks() { return this.battlePassHomeworks;}
}
