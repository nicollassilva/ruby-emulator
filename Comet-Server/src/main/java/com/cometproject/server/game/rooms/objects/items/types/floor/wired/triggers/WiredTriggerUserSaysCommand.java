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
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class WiredTriggerUserSaysCommand extends WiredTriggerItem {
    public static final int PARAM_OWNERONLY = 0;

    public WiredTriggerUserSaysCommand(RoomItemData itemData, Room room) {
        super(itemData, room);

        if (this.getWiredData().getText().isEmpty()) {
            this.getWiredData().setText(":trigger;1");
        }
    }

    public static boolean executeTriggers(PlayerEntity playerEntity, String message) {
        boolean wasExecuted = false;

        if (getTriggers(playerEntity.getRoom(), WiredTriggerUserSaysCommand.class).size() > 0) {
            final String executor = message.split(" ")[0].toLowerCase();
            final String[] params = CommandManager.getParams(message.split(" "));

            if (params.length == 0) {
                return false;
            }

            for (final WiredTriggerUserSaysCommand floorItem : getTriggers(playerEntity.getRoom(), WiredTriggerUserSaysCommand.class)) {
                if(floorItem == null) continue;

                final boolean ownerOnly = floorItem.getWiredData().getParams().containsKey(PARAM_OWNERONLY) && floorItem.getWiredData().getParams().get(PARAM_OWNERONLY) != 0;
                final boolean isOwner = playerEntity.getPlayerId() == floorItem.getRoom().getData().getOwnerId();
                final String[] values = floorItem.getWiredData().getText().split(";");

                if (!ownerOnly || isOwner) {
                    if (!floorItem.getWiredData().getText().isEmpty() && (executor.equals(values[0]) || executor.equals(":" + values[0]))) {

                        final Session user = NetworkManager.getInstance().getSessions().fromPlayer(params[0]);

                        if (user == null) {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.user.offline")));
                            return true;
                        }

                        if (user.getPlayer().getEntity() == playerEntity) {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.user.yourself")));
                            return true;
                        }

                        final Room room = playerEntity.getRoom();

                        if (room == null) {
                            return false;
                        }

                        if (room.getEntities().getEntityByPlayerId(user.getPlayer().getId()) == null) {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.get("command.user.notinroom")));
                            return true;
                        }

                        final int posX = user.getPlayer().getEntity().getPosition().getX();
                        final int posY = user.getPlayer().getEntity().getPosition().getY();
                        final int playerX = playerEntity.getPosition().getX();
                        final int playerY = playerEntity.getPosition().getY();

                        int limit = 1;

                        if (values.length > 1) {
                            try {
                                limit = Integer.parseInt(values[1]);

                                if (limit < 1 || limit > 500) {
                                    limit = 1;
                                }
                            } catch (Exception ignored) {}
                        }

                        if (Math.abs((posX - playerX)) <= limit && Math.abs(posY - playerY) <= limit) {
                            if (wasExecuted) {
                                floorItem.evaluate(user.getPlayer().getEntity(), message);
                            } else {
                                wasExecuted = floorItem.evaluate(user.getPlayer().getEntity(), message);
                            }
                        } else {
                            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer("generic", Locale.getOrDefault("command.user.toofar", "No estás a la distancia establecida que es de " + limit + " cuadros").replace("%username%", params[0])));
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
