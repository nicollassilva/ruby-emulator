package com.cometproject.server.game.commands.staff.cache;

import com.cometproject.api.game.GameContext;
import com.cometproject.server.composers.catalog.CatalogPublishMessageComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.achievements.AchievementManager;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.commands.CommandManager;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.landing.LandingManager;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.pets.commands.PetCommandManager;
import com.cometproject.server.game.polls.PollManager;
import com.cometproject.server.game.polls.types.Poll;
import com.cometproject.server.game.quests.QuestManager;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.bundles.RoomBundleManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceConfigComposer;
import com.cometproject.server.network.messages.outgoing.moderation.ModToolMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.polls.InitializePollMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.config.ConfigDao;


public class ReloadCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        final String command = params.length == 0 ? "" : params[0];

        switch (command) {
            default:
                client.send(new MotdNotificationMessageComposer(
                        Locale.getOrDefault("command.reload.title", "Here's a list of what you can reload using the :reload <type> command!") +
                                "\n\n" +
                                "- achievements\n" +
                                "- bans\n" +
                                "- bundles\n" +
                                "- catalog\n" +
                                "- config\n" +
                                "- emojis\n" +
                                "- filter\n" +
                                "- groupitems\n" +
                                "- items\n" +
                                "- locale\n" +
                                "- models\n" +
                                "- modpresets\n" +
                                "- music\n" +
                                "- namecolors\n" +
                                "- navigator\n" +
                                "- news\n" +
                                "- notifications\n" +
                                "- permissions\n" +
                                "- pets\n" +
                                "- polls\n" +
                                "- quests"
                ));

                break;
            case "bans":
                BanManager.getInstance().loadBans();

                sendNotif(Locale.get("command.reload.bans"), client);
                break;

            case "catalog":
                CatalogManager.getInstance().loadItemsAndPages();
                CatalogManager.getInstance().loadGiftBoxes();

                NetworkManager.getInstance().getSessions().broadcast(new MarketplaceConfigComposer());
                NetworkManager.getInstance().getSessions().broadcast(new CatalogPublishMessageComposer(true));
                sendNotif(Locale.get("command.reload.catalog"), client);
                break;

            case "navigator":
                NavigatorManager.getInstance().loadCategories();
                NavigatorManager.getInstance().loadPublicRooms();
                NavigatorManager.getInstance().loadStaffPicks();

                sendNotif(Locale.get("command.reload.navigator"), client);
                break;

            case "permissions":
                PermissionsManager.getInstance().loadRankPermissions();
                PermissionsManager.getInstance().loadPerks();
                PermissionsManager.getInstance().loadCommands();
                PermissionsManager.getInstance().loadOverrideCommands();
                PermissionsManager.getInstance().loadEffects();
                PermissionsManager.getInstance().loadChatBubbles();
                PermissionsManager.getInstance().loadPlayerBanner();

                sendNotif(Locale.get("command.reload.permissions"), client);
                break;

            case "config":
                ConfigDao.getAll();
                ConfigDao.getExternalConfig();

                sendNotif(Locale.get("command.reload.config"), client);
                break;

            case "news":
                LandingManager.getInstance().loadArticles();

                sendNotif(Locale.get("command.reload.news"), client);
                break;

            case "items":
                ItemManager.getInstance().loadItemDefinitions();

                sendNotif(Locale.get("command.reload.items"), client);
                break;

            case "filter":
                RoomManager.getInstance().getFilter().loadFilter();

                sendNotif(Locale.get("command.reload.filter"), client);
                break;

            case "locale":
                Locale.reload();
                CommandManager.getInstance().reloadAllCommands();

                sendNotif(Locale.get("command.reload.locale"), client);
                break;

            case "modpresets":
                ModerationManager.getInstance().loadPresets();

                sendNotif(Locale.get("command.reload.modpresets"), client);

                ModerationManager.getInstance().getModerators().forEach((session -> session.send(new ModToolMessageComposer())));
                break;

            case "groupitems":
                GameContext.getCurrent().getGroupService().getItemService().load();
                sendNotif(Locale.get("command.reload.groupitems"), client);
                break;

            case "models":
                GameContext.getCurrent().getRoomModelService().loadModels();

                sendNotif(Locale.get("command.reload.models"), client);
                break;

            case "music":
                ItemManager.getInstance().loadMusicData();
                sendNotif(Locale.get("command.reload.music"), client);
                break;

            case "quests":
                QuestManager.getInstance().loadQuests();
                sendNotif(Locale.get("command.reload.quests"), client);
                break;

            case "achievements":
                AchievementManager.getInstance().loadAchievements();

                sendNotif(Locale.get("command.reload.achievements"), client);
                break;

            case "pets":
                PetManager.getInstance().loadPetRaces();
                PetManager.getInstance().loadPetSpeech();
                PetManager.getInstance().loadTransformablePets();
                PetManager.getInstance().loadPetBreedPallets();

                PetCommandManager.getInstance().initialize();

                sendNotif(Locale.get("command.reload.pets"), client);
                break;

            case "crafting":
                ItemManager.getInstance().loadCraftingMachines();

                sendNotif(Locale.get("command.reload.crafting"), client);
                break;

            case "polls":
                PollManager.getInstance().initialize();

                if (PollManager.getInstance().roomHasPoll(client.getPlayer().getEntity().getRoom().getId())) {
                    Poll poll = PollManager.getInstance().getPollByRoomId(client.getPlayer().getEntity().getRoom().getId());

                    client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new InitializePollMessageComposer(poll.getPollId(), poll.getPollTitle(), poll.getThanksMessage()));
                }

                sendNotif(Locale.get("command.reload.polls"), client);
                break;
            case "bundles": {
                RoomBundleManager.getInstance().initialize();

                sendNotif(Locale.get("command.reload.bundles"), client);

                break;
            }
            case "namecolors": {
                RoomManager.getInstance().reloadNameColors();
                sendNotif(Locale.get("command.reload.namecolors"), client);
            }
            case "emojis": {
                RoomManager.getInstance().reloadEmojis();
                sendNotif(Locale.get("command.reload.emojis"), client);
            }
            case "notification": {
                CommandManager.getInstance().reloadAllCommands();
                sendNotif(Locale.get("command.reload.notification"), client);
            }

            case "gamecenter": {
                GameCenterManager.getInstance().initialize();
                sendNotif("GameCenter reiniciado com sucesso.", client);
            }
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getPermission() {
        return "reload_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.reload.description");
    }
}
