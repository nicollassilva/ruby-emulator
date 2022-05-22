package com.cometproject.server.game.commands;

import com.cometproject.api.commands.CommandInfo;
import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.config.CometSettings;
import com.cometproject.api.config.Configuration;
import com.cometproject.api.utilities.Initialisable;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.bot.BotControlCommand;
import com.cometproject.server.game.commands.bot.FreezeBotCommand;
import com.cometproject.server.game.commands.development.*;
import com.cometproject.server.game.commands.gimmicks.*;
import com.cometproject.server.game.commands.staff.fun.BubbleCommand;
import com.cometproject.server.game.commands.staff.housekeeping.EditFurniCommand;
import com.cometproject.server.game.commands.staff.rewards.custom.WinCommand;
import com.cometproject.server.game.commands.staff.security.LogsClientCommand;
import com.cometproject.server.game.commands.user.MentionsCommand;
import com.cometproject.server.game.commands.user.OnlineCommand;
import com.cometproject.server.game.commands.notifications.NotificationManager;
import com.cometproject.server.game.commands.staff.*;
import com.cometproject.server.game.commands.staff.alerts.*;
import com.cometproject.server.game.commands.staff.banning.*;
import com.cometproject.server.game.commands.staff.bundles.BundleCommand;
import com.cometproject.server.game.commands.staff.bundles.CloneRoomCommand;
import com.cometproject.server.game.commands.staff.cache.ReloadCommand;
import com.cometproject.server.game.commands.staff.cache.ReloadGroupCommand;
import com.cometproject.server.game.commands.staff.fun.RollCommand;
import com.cometproject.server.game.commands.staff.muting.MuteCommand;
import com.cometproject.server.game.commands.staff.muting.RoomMuteCommand;
import com.cometproject.server.game.commands.staff.muting.UnmuteCommand;
import com.cometproject.server.game.commands.staff.rewards.*;
import com.cometproject.server.game.commands.staff.rewards.mass.*;
import com.cometproject.server.game.commands.user.*;
import com.cometproject.server.game.commands.user.group.DeleteGroupCommand;
import com.cometproject.server.game.commands.user.group.EjectAllCommand;
import com.cometproject.server.game.commands.user.muting.MuteBotsCommand;
import com.cometproject.server.game.commands.user.muting.MutePetsCommand;
import com.cometproject.server.game.commands.user.room.*;
import com.cometproject.server.game.commands.user.room.ClearHighscoreCommand;
import com.cometproject.server.game.commands.user.settings.*;
import com.cometproject.server.game.commands.user.ws.*;
import com.cometproject.server.game.commands.vip.*;
import com.cometproject.server.game.commands.websocketvue.BuildCommand;
import com.cometproject.server.game.commands.websocketvue.EventAlertVueCommand;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.logging.LogManager;
import com.cometproject.server.logging.entries.CommandLogEntry;
import com.cometproject.server.modules.ModuleManager;
import com.cometproject.server.network.messages.outgoing.messenger.InstantChatMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CommandManager implements Initialisable {
    private static CommandManager commandManagerInstance;
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(CommandManager.class.getName());

    private NotificationManager notifications;
    private Map<String, ChatCommand> commands;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Configuration.currentConfig().get("comet.system.commandExecutorThreads")));


    /**
     * Initialize the commands map and load all commands
     */
    private CommandManager() {

    }

    public static CommandManager getInstance() {
        if (commandManagerInstance == null) {
            commandManagerInstance = new CommandManager();
        }

        return commandManagerInstance;
    }

    @Override
    public void initialize() {
        this.commands = new HashMap<>();

        this.reloadAllCommands();
        log.info("Loaded " + commands.size() + " chat commands");

        this.notifications = new NotificationManager();
        log.info("CommandManager initialized");
    }

    public void reloadAllCommands() {
        this.commands.clear();

        this.loadUserCommands();
        this.loadStaffCommands();
        this.loadBotCommands();
        this.notifications = new NotificationManager();

        if (Comet.isDebugging) {
            this.addCommand("reloadmapping", new ReloadMappingCommand());
            this.addCommand("instancestats", new InstanceStatsCommand());
            this.addCommand("roomgrid", new RoomGridCommand());
            this.addCommand("processtimes", new ProcessTimesCommand());
            this.addCommand("pos", new PositionCommand(true));
            this.addCommand("itemdata", new ItemDataCommand());
        }

        this.addCommand("itemid", new ItemVirtualIdCommand());
    }

    /**
     * Loads all user commands
     */
    private void loadUserCommands() {
        this.addCommand(Locale.get("command.commands.name"), new CommandsCommand());
        this.addCommand(Locale.get("command.tagprofile.name"), new TagProfileCommand());
        this.addCommand(Locale.get("command.mimicoff.name"), new MimicOfflineCommand());
        this.addCommand(Locale.get("command.about.name"), new AboutCommand());
        this.addCommand(Locale.get("command.pickall.name"), new PickAllCommand());
        this.addCommand(Locale.get("command.ejectall.name"), new EjectAllCommand());
        this.addCommand(Locale.get("command.empty.name"), new EmptyCommand());
        this.addCommand(Locale.get("command.sit.name"), new SitCommand());
        this.addCommand(Locale.get("command.lay.name"), new LayCommand());
        this.addCommand(Locale.get("command.stand.name"), new StandCommand());
        this.addCommand(Locale.get("command.home.name"), new HomeCommand());
        this.addCommand(Locale.get("command.setmax.name"), new SetMaxCommand());
        this.addCommand(Locale.get("command.position.name"), new PositionCommand());
        this.addCommand(Locale.get("command.deletegroup.name"), new DeleteGroupCommand());
        this.addCommand(Locale.get("command.togglefriends.name"), new ToggleFriendsCommand());
        this.addCommand(Locale.get("command.enablecommand.name"), new EnableCommand());
        this.addCommand(Locale.get("command.disablecommand.name"), new DisableCommand());
        this.addCommand(Locale.getOrDefault("command.pyramid.name", "pyramid"), new PyramidCommand());
        this.addCommand(Locale.getOrDefault("command.addlookwardrobe.name", "addlook"), new AddLookToWardrobeCommand());
        this.addCommand("screenshot", new ScreenshotCommand());
        this.addCommand(Locale.getOrDefault("command.logsclient.name", "logsclient"), new LogsClientCommand());
        this.addCommand(Locale.get("command.look.name"), new LookCommand());
        this.addCommand(Locale.get("command.colour.name"), new ColourCommand());
        this.addCommand(Locale.get("command.flagme.name"), new FlagMeCommand());
        this.addCommand(Locale.get("command.flaguser.name"), new FlagUserCommand());
        this.addCommand(Locale.get("command.randomize.name"), new RandomizeCommand());
        this.addCommand(Locale.get("command.emptypets.name"), new EmptyPetsCommand());
        this.addCommand(Locale.get("command.emptybots.name"), new EmptyBotsCommand());
        this.addCommand(Locale.get("command.mutebots.name"), new MuteBotsCommand());
        this.addCommand(Locale.get("command.mutepets.name"), new MutePetsCommand());
        this.addCommand(Locale.get("command.toggleevents.name"), new ToggleEventsCommand());
        this.addCommand(Locale.get("command.emptyfriends.name"), new EmptyFriendsCommand());
        //this.addCommand(Locale.get("command.reward.name"), new RewardCommand());
        this.addCommand(Locale.getOrDefault("command.setz.name", "setz"), new SetZCommand());
        this.addCommand(Locale.getOrDefault("command.override.name", "override"), new OverrideCommand());
        this.addCommand(Locale.getOrDefault("command.maxfloor.name", "maxfloor"), new MaxFloorCommand());
        this.addCommand(Locale.get("command.brb.name"), new BrbCommand());
        this.addCommand(Locale.get("command.setidletimer.name"), new SetIdleTimerCommand());
        this.addCommand(Locale.getOrDefault("command.autofloor.name", "autofloor"), new AutoFloorCommand());
        this.addCommand("looktest", new LookTestCommand());

        // VIP commands
        this.addCommand(Locale.get("command.push.name"), new PushCommand());
        this.addCommand(Locale.get("command.pull.name"), new PullCommand());
        this.addCommand(Locale.get("command.moonwalk.name"), new MoonwalkCommand());
        this.addCommand(Locale.get("command.enable.name"), new EffectCommand());
        this.addCommand(Locale.get("command.setspeed.name"), new SetSpeedCommand());
        this.addCommand(Locale.get("command.mimic.name"), new MimicCommand());
        this.addCommand(Locale.get("command.transform.name"), new TransformCommand());
        this.addCommand(Locale.get("command.noface.name"), new NoFaceCommand());
        this.addCommand(Locale.get("command.follow.name"), new FollowCommand());
        this.addCommand(Locale.get("command.superpull.name"), new SuperPullCommand());
        this.addCommand(Locale.getOrDefault("command.mentionsettings.name", "mentionsettings"), new MentionSettingsCommand());
        this.addCommand(Locale.get("command.redeemcredits.name"), new RedeemCreditsCommand());
        this.addCommand(Locale.get("command.handitem.name"), new HandItemCommand());
        this.addCommand(Locale.get("command.togglediagonal.name"), new ToggleDiagonalCommand());
        this.addCommand(Locale.get("command.fastwalk.name"), new FastWalkCommand());
        this.addCommand(Locale.get("command.hidewired.name"), new HideWiredCommand());
        this.addCommand(Locale.get("command.disablewhisper.name"), new DisableWhisperCommand());
        this.addCommand(Locale.getOrDefault("command.namecolour.name", "nc"), new NameColourCommand());
        this.addCommand(Locale.get("command.donate.name"), new DonateCommand());
        this.addCommand(Locale.getOrDefault("command.emoji.name", "emoji"), new EmojiCommand());
        this.addCommand(Locale.get("command.bubble.name"), new BubbleCommand());
        this.addCommand(Locale.getOrDefault("command.clearscore.name", "clearscore"), new ClearHighscoreCommand());
        this.addCommand(Locale.getOrDefault("command.freezebot.name", "freezebot"), new FreezeBotCommand());
        this.addCommand(Locale.getOrDefault("command.warpbot.name", "warpbot"), new WarpBotCommand());
        //this.addCommand("pursebaits", new PurseBaitsSocketCommand());
        this.addCommand(Locale.getOrDefault("command.superwired.name", "superwired"), new SuperWiredCommand());
        this.addCommand(Locale.getOrDefault("command.mentions.name", "mentions"), new MentionsCommand());
        this.addCommand(Locale.getOrDefault("command.djalert.name", "dj"), new DjAlertCommand());
        this.addCommand(Locale.getOrDefault("command.banner.name", "banner"), new BannerCommand());

        // Gimmick commands
        this.addCommand(Locale.get("command.rob.name"), new RobCommand());
        this.addCommand(Locale.get("command.kiss.name"), new KissCommand());
        this.addCommand(Locale.get("command.hug.name"), new HugCommand());
        this.addCommand(Locale.get("command.punch.name"), new PunchCommand());
        this.addCommand(Locale.get("command.sex.name"), new SexCommand());
        this.addCommand(Locale.get("command.smoke.name"), new SmokeCommand());
        this.addCommand(Locale.get("command.kill.name"), new KillCommand());
        this.addCommand(Locale.get("command.online.name"), new OnlineCommand());
        this.addCommand(Locale.get("command.build.name"), new BuildCommand());
        this.addCommand(Locale.getOrDefault("command.event_alert_websocket.name", "eventvue"), new EventAlertVueCommand());

        this.addCommand(Locale.getOrDefault("command.disabledcommands.name", "listcommands"), new ListDisabledCommandsCommand());
        this.addCommand(Locale.get("command.toggleshoot.name"), new ToggleShootCommand());

        this.addCommand(Locale.get("command.sell_room.name"), new SellRoomCommand());
        this.addCommand(Locale.get("command.buy_room.name"), new BuyRoomCommand());
        this.addCommand(Locale.get("command.see_height.name"), new SeeHeightCommand());
        this.addCommand(Locale.get("command.teleport_to_me.name"), new TeleportToMeCommand());
    }

    /**
     * Load bot commands
     */
    private void loadBotCommands() {
        this.addCommand("botcontrol", new BotControlCommand());
        this.addCommand("freezebot", new FreezeBotCommand());
    }

    /**
     * Loads all staff commands
     */
    private void loadStaffCommands() {
        this.addCommand(Locale.get("command.warp.name"), new WarpCommand());
        this.addCommand(Locale.getOrDefault("command.tradeban.name", "tradeban"), new TradeBanCommand());
        this.addCommand(Locale.getOrDefault("command.notilook.name", "notilook"), new NotificationLookCommand());
        //this.addCommand("verclones", new VerClonesCommand());
        this.addCommand(Locale.get("command.teleport.name"), new TeleportCommand());
        this.addCommand(Locale.get("command.massmotd.name"), new MassMotdCommand());
        this.addCommand(Locale.get("command.hotelalert.name"), new HotelAlertCommand());
        this.addCommand(Locale.get("command.invisible.name"), new InvisibleCommand());
        this.addCommand(Locale.get("command.superban.name"), new SuperBanCommand());
        this.addCommand(Locale.get("command.ban.name"), new BanCommand());
        this.addCommand(Locale.get("command.unban.name"), new UnBanCommand());
        this.addCommand(Locale.get("command.kick.name"), new KickCommand());
        this.addCommand(Locale.get("command.disconnect.name"), new DisconnectCommand());
        this.addCommand(Locale.get("command.ipban.name"), new IpBanCommand());
        this.addCommand(Locale.get("command.alert.name"), new AlertCommand());
        this.addCommand(Locale.get("command.roomalert.name"), new RoomAlertCommand());
        this.addCommand(Locale.get("command.givebadge.name"), new GiveBadgeCommand());
        this.addCommand(Locale.get("command.removebadge.name"), new RemoveBadgeCommand());
        this.addCommand(Locale.get("command.roomkick.name"), new RoomKickCommand());
        this.addCommand(Locale.get("command.coins.name"), new CoinsCommand());
        this.addCommand(Locale.get("command.points.name"), new PointsCommand());
        this.addCommand(Locale.get("command.duckets.name"), new DucketsCommand());
        this.addCommand(Locale.get("command.unload.name"), new UnloadCommand(false));
        this.addCommand(Locale.get("command.reloadroom.name"), new UnloadCommand(true));
        this.addCommand(Locale.get("command.roommute.name"), new RoomMuteCommand());
        this.addCommand(Locale.get("command.reload.name"), new ReloadCommand());
        this.addCommand(Locale.get("command.dance.name"), new DanceCommand());
        this.addCommand(Locale.get("command.maintenance.name"), new MaintenanceCommand());
        this.addCommand(Locale.get("command.roomaction.name"), new RoomActionCommand());
        this.addCommand(Locale.get("command.eventalert.name"), new EventAlertCommand());
        this.addCommand(Locale.get("command.machineban.name"), new MachineBanCommand());
        this.addCommand(Locale.get("command.makesay.name"), new MakeSayCommand());
        this.addCommand(Locale.get("command.mute.name"), new MuteCommand());
        this.addCommand(Locale.get("command.unmute.name"), new UnmuteCommand());
        this.addCommand(Locale.get("command.masscoins.name"), new MassCoinsCommand());
        this.addCommand(Locale.get("command.massbadge.name"), new MassBadgeCommand());
        this.addCommand(Locale.get("command.massduckets.name"), new MassDucketsCommand());
        this.addCommand(Locale.get("command.masspoints.name"), new MassPointsCommand());
        this.addCommand(Locale.get("command.mass.seasonal.name"), new MassSeasonalCommand());
        this.addCommand(Locale.get("command.playerinfo.name"), new PlayerInfoCommand());
        this.addCommand(Locale.get("command.roombadge.name"), new RoomBadgeCommand());
        this.addCommand(Locale.get("command.shutdown.name"), new ShutdownCommand());
        this.addCommand(Locale.get("command.summon.name"), new SummonCommand());
        this.addCommand(Locale.get("command.hotelalertlink.name"), new HotelAlertLinkCommand());
        this.addCommand(Locale.get("command.gotoroom.name"), new GotoRoomCommand());
        this.addCommand(Locale.get("command.notification.name"), new NotificationCommand());
        this.addCommand(Locale.get("command.quickpoll.name"), new QuickPollCommand());
        this.addCommand(Locale.get("command.roomoption.name"), new RoomOptionCommand());
        this.addCommand(Locale.get("command.massfreeze.name"), new MassFreezeCommand());
        this.addCommand(Locale.get("command.masswarp.name"), new MassWarpCommand());
        this.addCommand(Locale.get("command.listen.name"), new ListenCommand());
        this.addCommand(Locale.get("command.staffalert.name"), new StaffAlertCommand());
        this.addCommand(Locale.get("command.staffinfo.name"), new StaffInfoCommand());
        this.addCommand(Locale.get("command.roomnotification.name"), new RoomNotificationCommand());
        this.addCommand(Locale.get("command.roomvideo.name"), new RoomVideoCommand());
        this.addCommand(Locale.get("command.closedice.name"), new CloseDiceCommand());
        this.addCommand(Locale.get("command.publicroom.name"), new PublicRoomCommand());

        // New
        this.addCommand(Locale.get("command.advban.name"), new AdvBanCommand());
        this.addCommand(Locale.get("command.softban.name"), new SoftBanCommand());
        this.addCommand(Locale.get("command.masseffect.name"), new MassEffectCommand());
        this.addCommand(Locale.get("command.masshanditem.name"), new MassHandItemCommand());
        this.addCommand(Locale.get("command.freeze.name"), new FreezeCommand());
        this.addCommand(Locale.get("command.unfreeze.name"), new UnfreezeCommand());
        this.addCommand(Locale.get("command.eventreward.name"), new EventRewardCommand());
        this.addCommand(Locale.get("command.paygame.name"), new PayGameCommand());
        this.addCommand(Locale.get("command.eventwon.name"), new EventWonCommand());
        this.addCommand(Locale.get("command.viewinventory.name"), new ViewInventoryCommand());
        this.addCommand(Locale.get("command.eventvote.name"), new EventVoteCommand());
        this.addCommand(Locale.get("command.giverank.name"), new GiveRankCommand());
        this.addCommand(Locale.getOrDefault("command.furnifix.name", "furnifix"), new FurniFixCommand());
        this.addCommand(Locale.getOrDefault("command.welcome.name", "welcome"), new WelcomeCommand());
        this.addCommand(Locale.getOrDefault("command.whisperalert.name", "wha"), new WhisperAlertCommand());
        //this.addCommand(Locale.getOrDefault("command.married.name", "marry"), new MarriedCommand());
        this.addCommand(Locale.getOrDefault("command.nospam.name", "nospam"), new NoSpamCommand());
        this.addCommand(Locale.getOrDefault("command.setbet.name", "setbet"), new SetBetCommand());
        this.addCommand(Locale.getOrDefault("command.me.name", "me"), new MeCommand());
        this.addCommand(Locale.getOrDefault("command.reminderevent.name", "re"), new ReminderEventCommand());
        this.addCommand(Locale.getOrDefault("command.senduser.name", "sendto"), new SendUserCommand());
        this.addCommand(Locale.getOrDefault("command.toggletrade.name", "trades"), new ToggleTradeCommand());
        this.addCommand(Locale.get("command.winner.name"), new WinnerCommand());
        this.addCommand(Locale.getOrDefault("command.pickallwired.name", "pickallwired"), new PickAllWiredCommand());
        this.addCommand("addfilter", new FilterWordsCommand());
        this.addCommand("prefix", new PrefixCommand());
        this.addCommand("givebanner", new GiveBannerCommand());
        this.addCommand("keyboard", new KeyboardWalkCommand());

        // Room bundles
        this.addCommand(Locale.get("command.bundle.name"), new BundleCommand());
        this.addCommand(Locale.get("command.cloneroom.name"), new CloneRoomCommand());

        // Cache
        this.addCommand(Locale.get("command.reloadgroup.name"), new ReloadGroupCommand());

        // Fun
        this.addCommand(Locale.get("command.roll.name"), new RollCommand());

        // Target Offer
        this.addCommand(Locale.get("commands.keys.cmd_promote_offer"), new PromoteTargetOfferCommand());

        // Nicollas Commands
        this.addCommand(Locale.get("command.editfurni.name"), new EditFurniCommand());
        this.addCommand(Locale.getOrDefault("command.win.name", "win"), new WinCommand());
        this.addCommand(Locale.getOrDefault("command.pickup.name", "pickup"), new PickupCommand());
    }

    /**
     * Checks whether the request is a valid command alias
     *
     * @param message The requested command alias
     * @return The result of the check
     */
    public boolean isCommand(String message) {
        if (message.length() <= 1) return false;

        if (message.startsWith(" ")) return false;

        String executor = message.split(" ")[0].toLowerCase();

        if (executor.startsWith(" ")) {
            executor = executor.substring(1);
        }

        final boolean isCommand = executor.equals(":" + Locale.get("command.commands.name")) || commands.containsKey(executor.substring(1)) || ModuleManager.getInstance().getEventHandler().getCommands().containsKey(executor);

        if (!isCommand) {
            for (final String keys : this.commands.keySet()) {
                final List<String> keyList = Lists.newArrayList(keys.split(","));

                if (keyList.contains(executor)) {
                    return true;
                }
            }
        }

        return isCommand;
    }

    /**
     * Attempts to execute the given command
     *
     * @param message The alias of the command and the parameters
     * @param client  The player who is attempting to execute the command
     */
    public boolean parse(String message, Session client) {
        final String executor = message.split(" ")[0].toLowerCase();
        final ChatCommand chatCommand = this.get(executor);

        if (message.startsWith(" "))
            return false;

        final CommandInfo moduleCommandInfo = ModuleManager.getInstance().getEventHandler().getCommands().get(executor);
        final String commandName = chatCommand == null ? (moduleCommandInfo != null ? moduleCommandInfo.getPermission() : null) : chatCommand.getPermission();

        if (commandName == null) {
            return false;
        }

        if(!PermissionsManager.getInstance().getCommands().containsKey(commandName)) {
            return false;
        }

        final boolean userIsVip = client.getPlayer().getData().getRank() == CometSettings.vipRank;
        final boolean userHasCommand = client.getPlayer().getPermissions().hasCommand(commandName);
        final boolean commandIsVipOnly = PermissionsManager.getInstance().getCommands().get(commandName).isVipOnly();

        if ((userHasCommand && !commandIsVipOnly) || (commandIsVipOnly && userIsVip) || commandName.equals("")) {
            if (client.getPlayer().getEntity().getRoom().getData().getDisabledCommands().contains(executor)) {
                ChatCommand.sendNotif(Locale.get("command.disabled"), client);
                return true;
            }

            if(client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
                client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
                client.send(new EmailVerificationWindowMessageComposer(1,1));
                return false;
            }

            final String[] params = getParams(message.split(" "));

            if (chatCommand == null) {
                ModuleManager.getInstance().getEventHandler().handleCommand(client, executor, params);
            } else {
                if (chatCommand.isAsync()) {
                    this.executorService.submit(new ChatCommand.Execution(chatCommand, params, client));
                } else {
                    chatCommand.execute(client, params);
                }
            }

            try {
                if (LogManager.ENABLED && CometExternalSettings.enableStaffMessengerLogs) {
                    LogManager.getInstance().getStore().getLogEntryContainer().put(new CommandLogEntry(client.getPlayer().getEntity().getRoom().getId(), client.getPlayer().getId(), message));

                    if (chatCommand != null && client.getPlayer().getData().getRank() >= Integer.parseInt(Locale.getOrDefault("logchat.minrank", "10")) && chatCommand.isLoggable()) {
                        for (final Session player : ModerationManager.getInstance().getLogChatUsers()) {
                            player.send(new InstantChatMessageComposer(chatCommand.getLoggableDescription(), Integer.MAX_VALUE - 1));
                        }
                    }
                }
            } catch (Exception ignored) {

            }

            return true;
        } else {
            if (commandIsVipOnly) {
                ChatCommand.sendNotif(Locale.get("command.vip"), client);
            }

            return false;
        }
    }

    /**
     * Gets the parameters from the command that was executed (removing the first record of this array)
     *
     * @param splitStr The executed command, split by " "
     * @return The parameters for the command
     */
    public static String[] getParams(String[] splitStr) {
        final String[] a = new String[splitStr.length - 1];

        for (int i = 0; i < splitStr.length; i++) {
            if (i == 0) continue;

            a[i - 1] = splitStr[i];
        }

        return a;
    }

    private ChatCommand get(final String executor) {
        if (this.commands.containsKey(executor))
            return this.commands.get(executor);

        for (final String keys : this.commands.keySet()) {
            final List<String> keyList = Lists.newArrayList(keys.split(","));

            if (keyList.contains(executor)) {
                return this.commands.get(keys);
            }
        }

        return null;
    }

    private void addCommand(String executor, ChatCommand command) {
        final List<String> keyList = Lists.newArrayList(executor.split(","));

        for (final String key : keyList) {
            this.commands.put(":" + key, command);
        }
    }

    public NotificationManager getNotifications() {
        return notifications;
    }

    public Map<String, ChatCommand> getChatCommands() {
        return this.commands;
    }
}
