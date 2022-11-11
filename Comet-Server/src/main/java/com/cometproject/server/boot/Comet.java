package com.cometproject.server.boot;

import com.cometproject.api.stats.CometStats;
import com.cometproject.server.boot.utils.ShutdownProcess;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.rcon.RCONServer;
import com.cometproject.server.utilities.CometRuntime;
import com.cometproject.server.utilities.TimeSpan;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.*;


public class Comet {
    public static String instanceId = UUID.randomUUID().toString();
    /**
     * The time the server was started
     */
    public static long start;
    /**
     * Is a debugger attached?
     */
    public static volatile boolean isDebugging = true;

    public static volatile boolean logPackets = false;
    /**
     * Is Comet running?
     */
    public static volatile boolean isRunning = true;
    /**
     * Whether or not we want to show the GUI
     */
    public static boolean showGui = false;
    /**
     * Whether we're running Comet in daemon mode or not
     */
    public static boolean daemon = false;
    /**
     * Logging during start-up & console commands
     */
    private static final Logger log = LogManager.getLogger(Comet.class.getName());
    /**
     * The main server instance
     */
    private static CometServer server;
    /**
     * The principal RCON instance
     */
    private static RCONServer rconServer;

    /**
     * Start the server!
     *
     * @param args The arguments passed from the run command
     */
    public static void run(String[] args) {
        start = System.currentTimeMillis();

        try {
            final String propertiesPath = "./config/log4j.properties";
            final File propertiesFileContext = new File(propertiesPath);

            final LoggerContext context = (LoggerContext) LogManager.getContext(false);
            context.setConfigLocation(propertiesFileContext.toURI());
        } catch (Exception e) {
            log.error("Error while loading log4j2 configuration", e);
            return;
        }

        for (final String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.contains("dt_")) {
                //isDebugging = true;
                break;
            }
        }

        org.apache.log4j.Level logLevel = org.apache.log4j.Level.INFO;


        if (args.length < 1) {
            log.debug("No config args found, falling back to default configuration!");
            server = new CometServer(null);
        } else {
            Map<String, String> cometConfiguration = new HashMap<>();

            // Parse args
            List<String> arguments = new ArrayList<>();

            for (final String arg : args) {
                if (arg.contains(" ")) {
                    final String[] splitString = arg.split(" ");

                    Collections.addAll(arguments, splitString);
                } else {
                    arguments.add(arg);
                }
            }

            for (final String arg : arguments) {
                if (arg.equals("--debug-logging")) {
                    isDebugging = true;
                    logPackets = true;
                    logLevel = org.apache.log4j.Level.TRACE;

                }
                if (arg.equals("--no-debug")) {
                    isDebugging = false;
                    logPackets = false;
                    logLevel = org.apache.log4j.Level.ERROR;

                }

                if (arg.equals("--gui")) {
                    // start GUI!
                    showGui = true;
                }

                if (arg.equals("--daemon")) {
                    daemon = true;
                }

                if (arg.startsWith("--instance-name=")) {
                    instanceId = arg.replace("--instance-name=", "");
                }

                if (!arg.contains("="))
                    continue;

                final String[] splitArgs = arg.split("=");

                cometConfiguration.put(splitArgs[0], splitArgs.length != 1 ? splitArgs[1] : "");
            }

            server = new CometServer(cometConfiguration);
        }

        org.apache.log4j.Logger.getRootLogger().setLevel(logLevel);

        Configurator.setLevel(log.getName(), Level.INFO);
        server.init();

        try {
            initRcon();
        } catch (Exception ignored) {
            System.out.println("Erro ao inicializar RCON.");
        }


        ShutdownProcess.init();
    }

    public static void initRcon() throws Exception {
        rconServer = new RCONServer("0.0.0.0", 30001);
        rconServer.initializePipeline();
        rconServer.connect();
    }

    /**
     * Exit the comet server
     *
     * @param message The message to display to the console
     */
    public static void exit(String message) {
        log.error("Comet has shutdown. Reason: \"" + message + "\"");
        rconServer.stop();
        System.exit(0);
    }

    /**
     * Get the instance time in seconds
     *
     * @return The time in seconds
     */
    public static long getTime() {
        return (System.currentTimeMillis() / 1000L);
    }

    /**
     * Get the instance date [HH:MM:SS]
     *
     * @return The date
     */
    public static String getDate() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * Get the instance build of Comet
     *
     * @return The instance build of Comet
     */

    public static Random getRandom() {
        return new Random();
    }

    public static String getBuild() {
        return Comet.class.getPackage().getImplementationVersion() == null ? "Comet-DEV" : Comet.class.getPackage().getImplementationVersion();
    }

    /**
     * Gets the instance server stats
     *
     * @return Server stats object
     */
    public static CometStats getStats() {
        CometStats statsInstance = new CometStats();

        statsInstance.setPlayers(NetworkManager.getInstance().getSessions().getUsersOnlineCount());
        statsInstance.setRooms(RoomManager.getInstance().getRoomInstances().size());
        statsInstance.setUptime(TimeSpan.millisecondsToDate(System.currentTimeMillis() - Comet.start));

        statsInstance.setProcessId(CometRuntime.processId);
        statsInstance.setAllocatedMemory((Runtime.getRuntime().totalMemory() / 1024) / 1024);
        statsInstance.setUsedMemory(statsInstance.getAllocatedMemory() - (Runtime.getRuntime().freeMemory() / 1024) / 1024);
        statsInstance.setOperatingSystem(CometRuntime.operatingSystem + " (" + CometRuntime.operatingSystemArchitecture + ")");
        statsInstance.setCpuCores(Runtime.getRuntime().availableProcessors());

        return statsInstance;
    }

    /**
     * Get the main server instance
     *
     * @return The main server instance
     */
    public static CometServer getServer() {
        return server;
    }

    public static RCONServer getRconServer() {
        return rconServer;
    }
}
