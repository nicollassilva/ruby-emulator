package com.cometproject.api.config;

public class CometExternalSettings {
    /**
     * Used to :editfurni command
     */
    public static String housekeepingFurnitureEdition = "";

    /**
     * Game badge prefix. Eg: LVL_
     */
    public static String currentGameBadgePrefix = "";

    /**
     * Game badges limit
     */
    public static int currentGameBadgeLimit = 0;

    /**
     * Used to reward diamonds to the event winner
     */
    public static int eventDiamantsReward = 0;

    /**
     * Used to reward diamonds to the event winner (double rewards)
     */
    public static int eventDiamantsRewardDouble = 0;

    /**
     * Used to send messenger message to staffs
     */
    public static boolean enableStaffMessengerLogs = false;

    /**
     * Used to send discord webhooks for staffs
     */
    public static boolean discordWebhooksEnabled = true;
}
