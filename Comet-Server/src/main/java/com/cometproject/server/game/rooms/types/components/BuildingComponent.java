package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.floor.MagicMoveFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BuildingComponent {
    private static final Logger log = LogManager.getLogger(BuildingComponent.class.getName());
    private final ExecutorService buildingExecutor = Executors.newSingleThreadExecutor();
    private final Room room;
    private volatile boolean isBuilding = false;
    private volatile int builderId = -1;
    private String builderName = "";

    public void dispose(){
        try {
            buildingExecutor.shutdown();
        }catch (Exception ex){
            log.error(ex);
        }
    }

    public BuildingComponent(Room room) {
        this.room = room;
    }

    public void moveFloorItem(Session client, RoomItemFloor item, Position newPosition, int newRotation) {
        if (client == null || item == null)
            return;

        if (item instanceof MagicMoveFloorItem) {
            room.getItems().moveItemsOnSquare((MagicMoveFloorItem) item, newPosition);
        } else {
            final boolean successfulMove = room.getItems().moveFloorItem(item.getId(), newPosition, newRotation, client);
            if (successfulMove) {
                room.getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
                if (item.getTile().getItems().size() > 1) {
                    client.getPlayer().getQuests().progressQuest(QuestType.FURNI_STACK);
                }
            } else {
                final Map<String, String> notificationParams = Maps.newHashMap();
                notificationParams.put("message", "${room.error.cant_set_item}");
                client.send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            }
        }
    }

    public void placeWallItem(Session client, String[] parts, int id) {
        if (CometSettings.PLACE_ITEMS_ASYNC) {
            buildingExecutor.submit(() -> __internalPlaceWallItem(client, parts, id));
        } else {
            __internalPlaceWallItem(client, parts, id);
        }
    }

    /**
     * Método usado internamente para colocar um piso na parede. Não chame diretamente, use placeWallItem().
     *
     * @return true se conseguiu colocar o mobi no quarto.
     */
    private void __internalPlaceWallItem(Session client, String[] parts, int id) {
        final String position = Position.validateWallPosition(parts[1] + " " + parts[2] + " " + parts[3]);

        if (position == null) {
            return;
        }

        final Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);
        if (itemId == null) {
            return;
        }

        final PlayerItem item = client.getPlayer().getInventory().getItem(itemId);
        if (item == null) {
            return;
        }

        client.getPlayer().getEntity().getRoom().getItems().placeWallItem(item, position, client.getPlayer());
        final RoomItemWall roomItemWall = client.getPlayer().getEntity().getRoom().getItems().getWallItem(item.getId());
        if (roomItemWall != null) {
            client.getPlayer().getQuests().progressQuest(QuestType.FURNI_PLACE);
        }
    }

    public void placeFloorItem(Session client, PlayerItem item, int x, int y, int rot) {
        if (CometSettings.PLACE_ITEMS_ASYNC) {
            this.buildingExecutor.submit(() -> __internalPlaceFloorItem(client, item, x, y, rot));
        } else {
            __internalPlaceFloorItem(client, item, x, y, rot);
        }
    }

    /**
     * Método usado internamente para colocar um piso no chão. Não chame diretamente, use placeFloorItem().
     *
     * @return true se conseguiu colocar o mobi no quarto.
     */
    private boolean __internalPlaceFloorItem(Session client, PlayerItem item, int x, int y, int rot) {
        if (client == null || client.getPlayer() == null || client.getPlayer().getEntity() == null) {
            return false;
        }

        if (client.getPlayer().getEntity().getRoom() == null) {
            return false;
        }

        client.getPlayer().getEntity().getRoom().getItems().placeFloorItem(item, x, y, rot, client.getPlayer());
        if (client.getPlayer().getEntity().getRoom().getItems() == null) {
            return false;
        }

        final RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(item.getId());
        if (floorItem != null) {
            final RoomTile tile = floorItem.getTile();
            if (tile != null) {
                if (tile.getItems().size() > 1) {
                    client.getPlayer().getQuests().progressQuest(QuestType.FURNI_STACK);
                }
            }
            return true;
        }

        return false;
    }

    public synchronized boolean isBuilding() {
        return isBuilding;
    }

    public int getBuilderId() {
        return builderId;
    }

    public String getBuilderName() {
        return builderName;
    }

    public boolean isBuilder(PlayerEntity playerEntity) {
        return playerEntity.getPlayerId() == this.builderId;
    }

    public void setBuilder(@Nullable PlayerEntity player) {
        this.builderId = player != null ? player.getPlayerId() : -1;
        this.builderName = player != null ? player.getUsername() : this.builderName;
        MessageComposer notification = new NotificationMessageComposer("generic",
                "O comando fill foi " + (player != null ? "ativado" : "desativado") + " para o usuário " + this.builderName + "."
        );
        this.room.getEntities().broadcastMessage(notification, true);
    }

    public void fillArea(Session client, Position from, Position to, int rot, int baseItemId) {
        if (this.isBuilding()) {
            client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("fill.command.in.use", "Ops! você não pode preencher mais de uma região ao mesmo tempo.")));
            return;
        }

        this.buildingExecutor.submit(() -> {
            this.isBuilding = true;
            try {
                final List<Position> fillArea = Position.makeSquareInclusive(from, to);
                for (final Position position : fillArea.stream().limit(CometSettings.FILL_AREA_MAX_SIZE).collect(Collectors.toList())) {
                    try {
                        PlayerItem nextItem = client.getPlayer().getInventory().getFirstItemByBaseItemId(baseItemId);
                        if (nextItem == null)
                            break;

                        Thread.sleep(CometSettings.FILL_ITEM_PLACE_DELAY);
                        __internalPlaceFloorItem(client, nextItem, position.getX(), position.getY(), rot);
                    } catch (Exception e) {
                        log.error("error while placing item: {}", e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error("error while placing item: {}", e.getMessage(), e);
            }

            this.isBuilding = false;
        });
    }

    public void fillStack(Session client, int x, int y, int rot, PlayerItem item) {
        if (this.isBuilding()) {
            client.send(new NotificationMessageComposer("generic", "Ops! você não pode preencher mais de uma região ao mesmo tempo."));
            return;
        }

        this.buildingExecutor.submit(() -> {
            int counter = 0;
            this.isBuilding = true;
            try {
                for (int i = 0; i < client.getPlayer().getEntity().getStackCount(); i++) {
                    try {
                        if (++counter >= CometSettings.FILL_STACK_MAX_HEIGHT) {
                            break;
                        }

                        PlayerItem nextItem = client.getPlayer().getInventory().getFirstItemByBaseItemId(item.getBaseId());
                        if (nextItem == null)
                            break;


                        Thread.sleep(CometSettings.FILL_ITEM_PLACE_DELAY);
                        __internalPlaceFloorItem(client, nextItem, x, y, rot);
                    } catch (Exception e) {
                        log.error("error while placing item: {}", e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error("error while placing item: {}", e.getMessage(), e);
            }
            this.isBuilding = false;
        });
    }
}
