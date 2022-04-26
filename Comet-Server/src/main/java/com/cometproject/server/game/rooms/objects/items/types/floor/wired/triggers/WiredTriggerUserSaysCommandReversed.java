package com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.CommandManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class WiredTriggerUserSaysCommandReversed extends WiredTriggerItem {
    public static final int PARAM_OWNERONLY = 0;

    public WiredTriggerUserSaysCommandReversed(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getWiredData().getText().isEmpty()) {
            this.getWiredData().setText(":trigger;1");
        }
    }

    public static boolean executeTriggers(PlayerEntity playerEntity, String message) {
        boolean wasExecuted = false;

        if (getTriggers(playerEntity.getRoom(), WiredTriggerUserSaysCommandReversed.class).size() > 0) {

            String executor = message.split(" ")[0].toLowerCase();
            final String[] params = CommandManager.getParams(message.split(" "));

            if (params.length == 0 || executor == null)
                return false;

            for (RoomItemFloor floorItem : getTriggers(playerEntity.getRoom(), WiredTriggerUserSaysCommandReversed.class)) {
                WiredTriggerUserSaysCommandReversed trigger = ((WiredTriggerUserSaysCommandReversed) floorItem);

                final boolean ownerOnly = trigger.getWiredData().getParams().containsKey(PARAM_OWNERONLY) && trigger.getWiredData().getParams().get(PARAM_OWNERONLY) != 0;
                final boolean isOwner = playerEntity.getPlayerId() == trigger.getRoom().getData().getOwnerId();
                final String[] values = trigger.getWiredData().getText().split(";");

                if (!ownerOnly || isOwner) {
                    if (!trigger.getWiredData().getText().isEmpty() && (executor.equals(values[0]) || executor.equals(":" + values[0]))) {

                        String username = params[0];
                        Session user = NetworkManager.getInstance().getSessions().fromPlayer(username);
                        if (user == null) {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.user.offline")));
                            return true;
                        }
                        if (user.getPlayer().getEntity() == playerEntity) {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.user.yourself")));
                            return true;
                        }

                        Room room = playerEntity.getRoom();
                        if (room == null) {
                            return false;
                        }

                        if (room.getEntities().getEntityByPlayerId(user.getPlayer().getId()) == null) {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.user.notinroom")));
                            return true;
                        }

                        int posX = user.getPlayer().getEntity().getPosition().getX();
                        int posY = user.getPlayer().getEntity().getPosition().getY();
                        int playerX = playerEntity.getPosition().getX();
                        int playerY = playerEntity.getPosition().getY();

                        int limit = 1;
                        if (values.length > 1) {

                            try {
                                limit = Integer.parseInt(values[1]);
                                if (limit < 1 || limit > 500)
                                    limit = 1;

                            } catch (Exception e) {

                            }
                        }

                        if (Math.abs((posX - playerX)) <= limit && Math.abs(posY - playerY) <= limit) {

                            if (wasExecuted)
                                trigger.evaluate(playerEntity, message);
                            else
                                wasExecuted = trigger.evaluate(playerEntity, message);

                        } else {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.getOrDefault("command.user.toofar", "No estás a la distancia establecida que es de " + limit + " cuadros").replace("%username%", username)));
                            return true;
                        }
                    }
                }
            }

        }

        return wasExecuted;
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 0;
    }
}
