package com.cometproject.server.game.commands.vip;

import com.cometproject.api.game.GameContext;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom.WiredAddonNoItemsAnimateEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonKebBar;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.SendFloorItemMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class HideWiredCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();
        if (!client.getPlayer().getPermissions().getRank().roomFullControl() && !client.getPlayer().getEntity().hasRights()) {
            return;
        }

        room.getData().setIsWiredHidden(!room.getData().isWiredHidden());
        final boolean isHidden = room.getData().isWiredHidden();
        for (final RoomItemFloor floorItem : room.getItems().getFloorItems().values()) {
            if (floorItem instanceof WiredFloorItem || floorItem instanceof WiredAddonUnseenEffect || floorItem instanceof WiredAddonRandomEffect || floorItem instanceof WiredAddonNoItemsAnimateEffect || floorItem instanceof WiredAddonKebBar) {
                if (isHidden) {
                    room.getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(floorItem.getVirtualId(), client.getPlayer().getId()));
                } else {
                    room.getEntities().broadcastMessage(new SendFloorItemMessageComposer(floorItem));
                }

                floorItem.getTile().reload();
            }
        }

        sendNotif(isHidden ? Locale.getOrDefault("command.hidewired.hidden", "Wired is now hidden")
                        : Locale.getOrDefault("command.hidewired.shown", "Wired is now visible")
                , client
        );

        GameContext.getCurrent().getRoomService().saveRoomData(room.getData());
    }

    @Override
    public String getPermission() {
        return "hidewired_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.hidewired.description", "Remueve los wireds en tu sala");
    }
}
