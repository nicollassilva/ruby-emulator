package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


public class MoveFloorItemMessageEvent implements Event {
    private static final Logger log = LogManager.getLogger(MoveFloorItemMessageEvent.class.getName());

    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1, 1));
            return;
        }

        final Long id = ItemManager.getInstance().getItemIdByVirtualId(msg.readInt());
        if (id == null) {
            return;
        }

        final int x = msg.readInt();
        final int y = msg.readInt();
        final int rot = msg.readInt();

        if (client.getPlayer().getEntity() == null) return;

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) return;

        final RoomItemFloor floorItem = room.getItems().getFloorItem(id);

        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            final Map<String, String> notificationParams = Maps.newHashMap();
            notificationParams.put("message", "${room.error.cant_set_not_owner}");

            client.send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            client.send(new UpdateFloorItemMessageComposer(floorItem));
            return;
        }

        final Position newPos = new Position(x, y);
        if (floorItem != null) {
            if (rot != floorItem.getRotation()) {
                // ADD TRIGGER FOR WIRED IF ITEM IS ROTATE
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_ROTATE);
            }

            if (!newPos.equals(floorItem.getPosition())) {
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_MOVE);
            }
        }

        try {

            client.getPlayer().getEntity().getRoom().getBuilderComponent().moveFloorItem(client, floorItem, new Position(x, y), rot);

        } catch (Exception e) {
            log.error("Error whilst changing floor item position!", e);
        }
    }
}
