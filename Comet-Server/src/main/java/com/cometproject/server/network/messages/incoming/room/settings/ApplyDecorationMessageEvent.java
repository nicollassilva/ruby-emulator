package com.cometproject.server.network.messages.incoming.room.settings;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomPropertyMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.storage.api.StorageContext;
import org.apache.logging.log4j.LogManager;

import java.util.Map;


public class ApplyDecorationMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        final long itemId = ItemManager.getInstance().getItemIdByVirtualId(msg.readInt());
        final PlayerItem item = client.getPlayer().getInventory().getItem(itemId);

        if (item == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        final boolean isOwner = client.getPlayer().getId() == room.getData().getOwnerId();
        final boolean hasRights = room.getRights().hasRights(client.getPlayer().getId());

        if (isOwner || hasRights) {
            String type = "floor";
            final Map<String, String> decorations = room.getData().getDecorations();

            if (item.getDefinition().getItemName().contains("wallpaper")) {
                type = "wallpaper";
            } else if (item.getDefinition().getItemName().contains("landscape")) {
                type = "landscape";
            }

            if (decorations.containsKey(type)) {
                decorations.replace(type, item.getExtraData());
            } else {
                decorations.put(type, item.getExtraData());
            }

            if (type.equals("floor")) {
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_DECORATION_FLOOR);
                client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_FLOOR_COLOR_COUNT, 1);
            }

            if (type.equals("wallpaper")) {
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_DECORATION_WALL);
                client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_WALLPAPER_COUNT, 1);
            }

            if(type.equals("landscape")) {
                client.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_LANDSCAPE_COUNT, 1);
            }

            client.getPlayer().getInventory().removeItem(item);
            StorageContext.getCurrentContext().getRoomItemRepository().deleteItem(itemId);
            client.send(new UpdateInventoryMessageComposer());

            try {
                GameContext.getCurrent().getRoomService().saveRoomData(room.getData());
                room.getEntities().broadcastMessage(new RoomPropertyMessageComposer(type, item.getExtraData()));
            } catch (Exception e) {
                LogManager.getLogger(ApplyDecorationMessageEvent.class.getName()).error("Error while saving room data", e);
            }
        }
    }
}
