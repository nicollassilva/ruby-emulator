package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.floor.MagicMoveFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerWalksOffFurni;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingComponent {
    public static final int MAX_FILL_STACK_BLOCKS = 35;
    public static final int MAX_FILL_AREA_BLOCKS = 300;
    public static final long FILL_PLACE_ITEM_DELAY = 75L;
    private static final boolean PLACE_ITEM_ASYNC = true;
    private static final Logger log = LogManager.getLogger(BuildingComponent.class.getName());
    private final ExecutorService buildingExecutor = Executors.newSingleThreadExecutor();
    private final Room room;
    private volatile boolean isBuilding = false;
    private volatile int builderId = -1;
    private String builderName = "";

    public BuildingComponent(Room room) {
        this.room = room;
    }

    public void moveFloorItem(Session client, RoomItemFloor item, Position newPositon, int newRotation) {
        if (client == null || item == null)
            return;

        if (item instanceof MagicMoveFloorItem) {
            final Position oldPositon = item.getPosition();
            final int diffX = newPositon.getX() - oldPositon.getX();
            final int diffY = newPositon.getY() - oldPositon.getY();
            final List<RoomItemFloor> itemsToMove = new ArrayList<>(100);
            final MagicMoveFloorItem magicMove = (MagicMoveFloorItem) item;
            final List<AffectedTile> affectedTiles = AffectedTile.getAffectedBothTilesAt(magicMove.getDefinition().getLength(), magicMove.getDefinition().getWidth(), magicMove.getPosition().getX(), magicMove.getPosition().getY(), magicMove.getRotation());
            for (final AffectedTile affectedTile : affectedTiles) {
                itemsToMove.addAll(this.room.getMapping().getTile(affectedTile.x, affectedTile.y).getItems());
            }

            final List<RoomTile> tilesUpdated = new ArrayList<>(affectedTiles.size());
            for (final RoomItemFloor floorItem : itemsToMove) {
                if (floorItem instanceof MagicMoveFloorItem) continue;
                final Position oldItemPosition = floorItem.getPosition().copy();
                final Position newItemPosition = oldItemPosition.copy();

                newItemPosition.setX(oldItemPosition.getX() + diffX);
                newItemPosition.setY(oldItemPosition.getY() + diffY);

                for (final RoomEntity entity : this.room.getEntities().getEntitiesAt(oldItemPosition)) {
                    floorItem.onEntityStepOff(entity);
                    WiredTriggerWalksOffFurni.executeTriggers(entity, floorItem);
                }

                room.getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(oldItemPosition, newItemPosition, 0, 0, floorItem.getVirtualId()));

                item.onPositionChanged(newItemPosition);
                item.getPosition().setX(newItemPosition.getX());
                item.getPosition().setY(newItemPosition.getY());

                item.save();

                final RoomTile oldTile = this.room.getMapping().getTile(oldItemPosition);
                final RoomTile newTile = this.room.getMapping().getTile(newItemPosition);
                oldTile.reload();
                newTile.reload();
                tilesUpdated.add(oldTile);
                tilesUpdated.add(newTile);
            }

            if (!tilesUpdated.isEmpty()) {
                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tilesUpdated));
            }
        } else {
            final boolean successfulMove = room.getItems().moveFloorItem(item.getVirtualId(), newPositon, newRotation, true, client);
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
        if (PLACE_ITEM_ASYNC) {
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
        if (PLACE_ITEM_ASYNC) {
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
        if(client == null || client.getPlayer() == null || client.getPlayer().getEntity() == null) {
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
                "O comando fill foi " + (player != null ? "ativado" : "desativado") + " para o usuário '" + this.builderName + "'."
        );
        this.room.getEntities().broadcastMessage(notification, true);
    }

    public void fillArea(Session client, int x, int y, int rot, PlayerItem item) {
        if (this.isBuilding()) {
            client.send(new NotificationMessageComposer("generic", "Ops! você não pode preencher mais de uma região ao mesmo tempo."));
            return;
        }

        this.buildingExecutor.submit(() -> {
            int counter = 0;
            this.isBuilding = true;
            final Position playerPosition = client.getPlayer().getEntity().getPosition().copy();
            playerPosition.setX(playerPosition.getX() + 1);
            playerPosition.setY(playerPosition.getY() + 1);

            out:
            for (int builderX = playerPosition.getX(); builderX < x; builderX++) {
                for (int builderY = playerPosition.getY(); builderY < y; builderY++) {
                    try {
                        if (++counter >= MAX_FILL_AREA_BLOCKS) {
                            break out;
                        }

                        PlayerItem nextItem = client.getPlayer().getInventory().getFirstItemByBaseItemId(item.getBaseId());
                        if (nextItem == null)
                            break out;

                        Thread.sleep(FILL_PLACE_ITEM_DELAY);
                        __internalPlaceFloorItem(client, nextItem, builderX, builderY, rot);
                    } catch (Exception e) {
                        log.error("error while placing item: {}", e.getMessage(), e);
                    }
                }
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
            for (int i = 0; i < client.getPlayer().getEntity().getStackCount(); i++) {
                try {
                    if (++counter >= MAX_FILL_STACK_BLOCKS) {
                        break;
                    }

                    PlayerItem nextItem = client.getPlayer().getInventory().getFirstItemByBaseItemId(item.getBaseId());
                    if (nextItem == null)
                        break;

                    Thread.sleep(FILL_PLACE_ITEM_DELAY);
                    __internalPlaceFloorItem(client, nextItem, x, y, rot);
                } catch (Exception e) {
                    log.error("error while placing item: {}", e.getMessage(), e);
                }
            }
            this.isBuilding = false;
        });
    }
}
