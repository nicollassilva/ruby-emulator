package com.cometproject.server.storage.queries.config;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.rooms.filter.FilterMode;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.catalog.TargetOffer;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;


public class ConfigDao {
    public static void getAll() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet config = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM server_configuration LIMIT 1", sqlConnection);

            config = preparedStatement.executeQuery();

            while (config.next()) {
                TargetOffer.ACTIVE_TARGET_OFFER_ID = config.getInt("target_offer_id");
                CometSettings.motdEnabled = config.getBoolean("motd_enabled");
                CometSettings.motdMessage = config.getString("motd_message");
                CometSettings.hotelName = config.getString("hotel_name");
                CometSettings.hotelUrl = config.getString("hotel_url");
                CometSettings.groupCost = config.getInt("group_cost");
                CometSettings.onlineRewardEnabled = config.getBoolean("online_reward_enabled");
                CometSettings.onlineRewardCredits = config.getInt("online_reward_credits");
                CometSettings.onlineRewardDuckets = config.getInt("online_reward_duckets");
                CometSettings.onlineRewardInterval = config.getInt("online_reward_interval");
                CometSettings.aboutImg = config.getString("about_image");
                CometSettings.aboutShowPlayersOnline = config.getBoolean("about_show_players_online");
                CometSettings.aboutShowRoomsActive = config.getBoolean("about_show_rooms_active");
                CometSettings.aboutShowUptime = config.getBoolean("about_show_uptime");
                CometSettings.floorEditorMaxX = config.getInt("floor_editor_max_x");
                CometSettings.floorEditorMaxY = config.getInt("floor_editor_max_y");
                CometSettings.floorEditorMaxTotal = config.getInt("floor_editor_max_total");
                CometSettings.roomMaxPlayers = config.getInt("room_max_players");
                CometSettings.roomEncryptPasswords = config.getBoolean("room_encrypt_passwords");
                CometSettings.roomCanPlaceItemOnEntity = config.getBoolean("room_can_place_item_on_entity");
                CometSettings.roomMaxBots = config.getInt("room_max_bots");
                CometSettings.roomMaxPets = config.getInt("room_max_pets");
                CometSettings.roomWiredRewardMinimumRank = config.getInt("room_wired_reward_minimum_rank");
                CometSettings.roomIdleMinutes = config.getInt("room_idle_minutes");
                CometSettings.wordFilterMode = FilterMode.valueOf(config.getString("word_filter_mode").toUpperCase());
                CometSettings.useDatabaseIp = config.getBoolean("use_database_ip");
                CometSettings.saveLogins = config.getBoolean("save_logins");
                CometSettings.playerInfiniteBalance = config.getBoolean("player_infinite_balance");
                CometSettings.playerGiftCooldown = config.getInt("player_gift_cooldown");
                CometSettings.playerPurchaseCooldown = config.getInt("player_purchase_cooldown");
                CometSettings.playerChangeFigureCooldown = config.getInt("player_change_figure_cooldown");
                CometSettings.PLAYER_FIGURE_VALIDATION_ALLOW = config.getBoolean("player_figure_validation");
                CometSettings.messengerMaxFriends = config.getInt("messenger_max_friends");
                CometSettings.messengerLogMessages = config.getBoolean("messenger_log_messages");
//                CometSettings.storageItemQueueEnabled = config.getBoolean("storage_item_queue_enabled");
//                CometSettings.storagePlayerQueueEnabled = config.getBoolean("storage_player_queue_enabled");
                CometSettings.cameraPhotoUrl = config.getString("camera_photo_url");
                CometSettings.cameraPhotoItemId = config.getInt("camera_photo_itemid");
                CometSettings.cameraPhotoItemIdXXL = config.getInt("camera_photo_itemdid_xxl");
                CometSettings.cameraUploadUrl = config.getString("camera_photo_upload_url");
                CometSettings.thumbnailUploadUrl = config.getString("camera_thumbnail_upload_url");

                CometSettings.emojiImagePath = config.getString("emoji_image_path_url");
                CometSettings.cantityUsers = config.getInt("cantity_users_landing");
                CometSettings.roomsForUsers = config.getInt("max_rooms_creations");
                CometSettings.rewardConcurrentUsers = config.getString("reward_code_badge");

                CometSettings.maxConnectionsPerIpAddress = config.getInt("max_connections_per_ip");
                CometSettings.maxConnectionsBlockSuspicious = config.getBoolean("max_connections_block_suspicious");
                CometSettings.groupChatEnabled = config.getBoolean("group_chat_enabled");
                CometSettings.logCatalogPurchases = config.getBoolean("log_catalog_purchases");
                CometSettings.hallOfFameEnabled = config.getBoolean("hall_of_fame_enabled");
                CometSettings.hallOfFameCurrency = config.getString("hall_of_fame_currency");
                CometSettings.hallOfFameRefreshMinutes = config.getInt("hall_of_fame_refresh_minutes");
                CometSettings.hallOfFameTextsKey = config.getString("hall_of_fame_texts_key");
                CometSettings.bonusBagEnabled = config.getBoolean("bonus_bag_enabled");
                CometSettings.bonusRewardName = config.getString("bonus_reward_name");
                CometSettings.bonusHours = config.getInt("bonus_hours");
                CometSettings.bonusRewardItemId = config.getInt("bonus_reward_itemid");

                CometSettings.onlineRewardDiamondsInterval = config.getInt("online_reward_diamonds_interval");
                CometSettings.onlineRewardDiamonds = config.getInt("online_reward_diamonds");

                CometSettings.cryptoEnabled = config.getBoolean("crypto_enabled");
                CometSettings.crypto_d = config.getString("crypto_d");
                CometSettings.crypto_n = config.getString("crypto_n");
                CometSettings.crypto_e = config.getString("crypto_e");
                CometSettings.websocketsEnabled = config.getBoolean("websockets_enabled");
                CometSettings.nitroWsHeader = config.getString("websockets_header");
                //CometSettings.monsterSeedId = config.getInt("monster_seed_id");
                CometSettings.websocketOriginWhitelist = config.getString("websockets_whitelist").split(",");
                CometSettings.talentTrackEnabled = config.getBoolean("talenttrack_enabled");

                CometSettings.cameraCoinsPricing = config.getInt("camera_pricing_coins");
                CometSettings.cameraDucketsPricing = config.getInt("camera_pricing_duckets");
                CometSettings.FIGURE_VALIDATION = config.getBoolean("player_figure_validation");
                CometSettings.CATALOG_SOLD_OUT_LTD_PAGE_ID = config.getInt("catalog_soldout_ltd_page");

                final String characters = config.getString("word_filter_strict_chars");

                CometSettings.strictFilterCharacters.clear();

                for (String charSet : characters.split(",")) {
                    if (!charSet.contains(":")) continue;

                    final String[] chars = charSet.split(":");

                    if (chars.length == 2) {
                        CometSettings.strictFilterCharacters.put(chars[0], chars[1]);
                    } else {
                        CometSettings.strictFilterCharacters.put(chars[0], "");
                    }
                }


                final String doubleDays = config.getString("online_reward_double_days");
                CometSettings.onlineRewardDoubleDays.clear();

                if(doubleDays.length() > 1) {
                    final String[] days = doubleDays.split(",");

                    for(String day : days) {
                        CometSettings.onlineRewardDoubleDays.add(DayOfWeek.valueOf(day.toUpperCase()));
                    }
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(config);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void getExternalConfig() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet config = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM server_external_configuration LIMIT 1", sqlConnection);

            config = preparedStatement.executeQuery();

            while (config.next()) {
                CometExternalSettings.housekeepingFurnitureEdition = config.getString("houseekeping_furniture_edition");
                CometExternalSettings.currentGameBadgePrefix = config.getString("prefix_badge_event");
                CometExternalSettings.currentGameBadgeLimit = config.getInt("badge_event_limit");
                CometExternalSettings.eventDiamantsReward = config.getInt("event_diamonds_reward");
                CometExternalSettings.eventDiamantsRewardDouble = config.getInt("event_diamonds_reward_double");
                CometExternalSettings.enableStaffMessengerLogs = config.getBoolean("enable_staff_messenger_logs");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(config);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveActiveTargetOffer(int offerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE server_configuration SET target_offer_id = ?", sqlConnection);
            preparedStatement.setInt(1, offerId);
            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
