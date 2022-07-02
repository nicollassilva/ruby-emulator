package com.cometproject.server.network.messages.incoming.room.settings;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.api.game.rooms.settings.*;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.navigator.types.Category;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.filter.FilterResult;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomWriter;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.RoomInfoUpdatedMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.RoomVisualizationSettingsMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.SettingsUpdatedMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.rooms.RoomDao;


public class SaveRoomDataMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int id = msg.readInt();

        if (id == 0) {
            if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
                id = client.getPlayer().getEntity().getRoom().getId();
            }
        }

        Room room = null;
        IRoomData data = null;

        if (RoomManager.getInstance().isActive(id)) {
            room = RoomManager.getInstance().get(id);

            if (room.getData() != null) {
                data = room.getData();
            }
        } else {
            data = GameContext.getCurrent().getRoomService().getRoomData(id);
        }

        if (data == null) return;

        if (room == null || (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        String name = msg.readString();
        String description = msg.readString();
        int state = msg.readInt();
        String password = msg.readString();
        int maxUsers = msg.readInt();
        int categoryId = msg.readInt();
        int tagCount = msg.readInt();

        StringBuilder tagBuilder = new StringBuilder();

        for (int i = 0; i < tagCount; i++) {
            if (i > 0) {
                tagBuilder.append(",");
            }

            String tag = msg.readString();
            tagBuilder.append(tag);
        }

        String tagString = tagBuilder.toString();
        String[] tags = tagString.split(",");

        int tradeState = msg.readInt();

        boolean allowPets = msg.readBoolean();
        boolean allowPetsEat = msg.readBoolean();

        boolean allowWalkthrough = msg.readBoolean();
        boolean hideWall = msg.readBoolean();
        int wallThick = msg.readInt();
        int floorThick = msg.readInt();

        int muteState = msg.readInt();
        int kickState = msg.readInt();
        int banState = msg.readInt();

        int bubbleMode = msg.readInt();
        int bubbleType = msg.readInt();
        int bubbleScroll = msg.readInt();
        int chatDistance = msg.readInt();
        int antiFloodSettings = msg.readInt();

        if (wallThick < -2 || wallThick > 1) {
            wallThick = 0;
        }

        if (floorThick < -2 || floorThick > 1) {
            floorThick = 0;
        }

        if (name.length() < 1) {
            return;
        }

        if (state < 0 || state > 3) {
            return;
        }

        if (maxUsers < 0) {
            return;
        }

        Category category = NavigatorManager.getInstance().getCategory(categoryId);

        if (category == null) {
            return;
        }

        if (category.getRequiredRank() > client.getPlayer().getData().getRank()) {
            categoryId = 15; // 15 = the uncategorized category.
        }

        if (tags.length > 2) {
            return;
        }

        String filteredName = name;
        String filteredDescription = description;

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(filteredName);
            FilterResult filterResultDesc = RoomManager.getInstance().getFilter().filter(filteredDescription);

            if (filterResult.isBlocked() || filterResultDesc.isBlocked()) {
                //client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.sendQueue(new NotificationMessageComposer("filter", Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                if(filterResult.isBlocked()) {
                    filterResult.sendLogToStaffs(client, "<SaveRoomData>");
                } else if (filterResultDesc.isBlocked()) {
                    filterResultDesc.sendLogToStaffs(client, "<SaveRoomData>");
                }
                return;
            }

            filteredName = filterResult.getMessage();
            filteredDescription = filterResultDesc.getMessage();
        }

        RoomAccessType currentAccessState = RoomWriter.roomAccessToString(state);
        RoomMuteState currentMuteState = RoomMuteState.valueOf(muteState);
        RoomKickState currentKickState = RoomKickState.valueOf(kickState);
        RoomBanState currentBanState = RoomBanState.valueOf(banState);

        if(room.getData().getAccess() != currentAccessState) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_SETTINGS_DOOR_MODE, 1);
        }

        if(room.getData().getMuteState() != currentMuteState) {
            client.getPlayer().getAchievements().progressAchievement( AchievementType.ROOM_SETTINGS_CAN_MUTE, 1);
        }

        if(room.getData().getKickState() != currentKickState) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_SETTINGS_CAN_KICK, 1);
        }

        if(room.getData().getBanState() != currentBanState) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_SETTINGS_CAN_BAN, 1);
        }

        if(room.getData().getAntiFloodSettings() != antiFloodSettings) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_SETTINGS_FLOOD_FILTER, 1);
        }

        if(room.getData().getBubbleScroll() != bubbleScroll) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_SETTINGS_CHAT_SCROLL_SPEED, 1);
        }

        if(room.getData().getChatDistance() != chatDistance) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_SETTINGS_CHAT_DISTANCE, 1);
        }

        System.out.println(wallThick);
        System.out.println(floorThick);

        data.setAccess(currentAccessState);
        data.setCategoryId(categoryId);
        data.setName(filteredName);
        //RoomDao.updateRoomName(filteredName, data.getId());
        data.setDescription(filteredDescription);
        data.setPassword(password);
        data.setMaxUsers(maxUsers);
        data.setTags(tags);
        data.setThicknessWall(wallThick);
        data.setThicknessFloor(floorThick);
        data.setHideWalls(hideWall);
        data.setAllowWalkthrough(allowWalkthrough);
        data.setAllowPets(allowPets);

        data.setTradeState(RoomTradeState.valueOf(tradeState));
        data.setMuteState(currentMuteState);
        data.setKickState(currentKickState);
        data.setBanState(currentBanState);

        data.setChatDistance(chatDistance);
        data.setBubbleMode(bubbleMode);
        data.setBubbleScroll(bubbleScroll);
        data.setBubbleType(bubbleType);
        data.setAntiFloodSettings(antiFloodSettings);

        try {
            GameContext.getCurrent().getRoomService().saveRoomData(data);

            room.getEntities().broadcastMessage(new RoomVisualizationSettingsMessageComposer(hideWall, wallThick, floorThick));
//            room.getEntities().broadcastMessage(new RoomDataMessageComposer(room, true, room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()));
            room.getEntities().broadcastMessage(new SettingsUpdatedMessageComposer(data.getId()));
            room.getEntities().broadcastMessage(new RoomInfoUpdatedMessageComposer(data.getId()));

            for(PlayerEntity playerEntity : room.getEntities().getPlayerEntities()) {
                playerEntity.getPlayer().flush();
            }
        } catch (Exception e) {
            RoomManager.log.error("Error while saving room data", e);
        }
    }
}
