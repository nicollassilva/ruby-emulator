package com.cometproject.server.boot;

import com.cometproject.api.config.Configuration;
import com.cometproject.api.game.GameContext;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.GameCycle;
import com.cometproject.server.game.achievements.AchievementManager;
import com.cometproject.server.game.battlepass.BattlePassManager;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.commands.CommandManager;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.gamecenter.games.battleball.thread.BattleBallWorkerThread;
import com.cometproject.server.game.groups.items.GroupItemManager;
import com.cometproject.server.game.guides.GuideManager;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.landing.LandingManager;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.polls.PollManager;
import com.cometproject.server.game.quests.QuestManager;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.bundles.RoomBundleManager;
import com.cometproject.server.game.snowwar.thread.WorkerTasks;
import com.cometproject.server.game.utilities.validator.PlayerFigureValidator;
import com.cometproject.server.logging.LogManager;
import com.cometproject.server.modules.ModuleManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.storage.StorageManager;
import com.cometproject.server.storage.queries.config.ConfigDao;
import com.cometproject.server.tasks.CometThreadManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class CometServer {
    public static final String CLIENT_VERSION = "PRODUCTION-201611291003-338511768";
    private final Logger log = org.apache.logging.log4j.LogManager.getLogger(CometServer.class.getName());


    public CometServer(Map<String, String> overridenConfig) {

        Configuration.setConfiguration(new Configuration("./config/comet.properties"));

        if (overridenConfig != null) {
            Configuration.currentConfig().override(overridenConfig);
        }
    }


    /**
     * Initialize Comet Server
     */
    public void init() {
        ModuleManager.getInstance().initialize();

        CometThreadManager.getInstance().initialize();
        StorageManager.getInstance().initialize();
        LogManager.getInstance().initialize();

        // Locale & config
        ConfigDao.getAll();
        ConfigDao.getExternalConfig();

        Locale.initialize();

        // Initialize the game managers
        // TODO: Implement some sort of dependency injection so we don't need any of this crap!!

        PermissionsManager.getInstance().initialize();
        RoomBundleManager.getInstance().initialize();
        ItemManager.getInstance().initialize();
        CatalogManager.getInstance().initialize();
        RoomManager.getInstance().initialize();
        NavigatorManager.getInstance().initialize();
        CommandManager.getInstance().initialize();
        BanManager.getInstance().initialize();
        ModerationManager.getInstance().initialize();
        PetManager.getInstance().initialize();
        LandingManager.getInstance().initialize();
        PlayerManager.getInstance().initialize();
        QuestManager.getInstance().initialize();
        AchievementManager.getInstance().initialize();
        BattlePassManager.getInstance().initialize();
        PollManager.getInstance().initialize();
        GuideManager.getInstance().initialize();
        BattleBallWorkerThread.initWorkers();

        GameContext gameContext = new GameContext();

        //GameManager.makeSet();
        //GameManager.registerGame(FastFoodGame.class);
        //GameManager.registerGame(SnowWarGame.class);

        GameCenterManager.getInstance().initialize();

        gameContext.setCatalogService(CatalogManager.getInstance());
        gameContext.setFurnitureService(ItemManager.getInstance());
        gameContext.setPlayerService(PlayerManager.getInstance());

        GameContext.setCurrent(gameContext);

        String ipAddress = this.getConfig().get("comet.network.host"),
                port = this.getConfig().get("comet.network.port");

        WorkerTasks.initWorkers();
        NetworkManager.getInstance().initialize(ipAddress, port);


        ModuleManager.getInstance().setupModules();

        GameContext.getCurrent().getGroupService().setItemService(new GroupItemManager());

        GameCycle.getInstance().initialize();
        DiscordIntegration.getInstance().initialize();
    }

    /**
     * Get the Comet configuration
     *
     * @return Comet configuration
     */
    public Configuration getConfig() {
        return Configuration.currentConfig();
    }

    public Logger getLogger() {
        return log;
    }
}
