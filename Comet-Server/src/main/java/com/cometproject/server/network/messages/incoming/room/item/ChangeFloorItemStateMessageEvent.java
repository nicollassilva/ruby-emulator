package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.game.quests.QuestType;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.user.building.BuildingType;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerStateChanged;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.custom.WiredTriggerCustomStateChanged;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.misc.OpenLinkMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChangeFloorItemStateMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {

        if (client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1, 1));
            return;
        }

        if (client.getPlayer().antiSpam("ChangeFloorItemStateMessageEvent", 0.03)) {
            return;
        }

        final int virtualId = msg.readInt();
        final Long itemId = ItemManager.getInstance().getItemIdByVirtualId(virtualId);

        if (itemId == null) {
            return;
        }

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        if (!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        final RoomItemFloor item = room.getItems().getFloorItem(itemId);

        if (item == null) {
            return;
        }

        if(client.getPlayer().getEntity().hasRights() && client.getPlayer().getEntity().getBuildingType().equals(BuildingType.FILL)){
            if(client.getPlayer().getEntity().hasAttribute("fill_command_item")){
                final RoomItemFloor firstItem = (RoomItemFloor)client.getPlayer().getEntity().getAttribute("fill_command_item");
                if(firstItem.getId() == item.getId()){
                    client.getPlayer().getEntity().removeAttribute("fill_command_item");
                    client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(),Locale.getOrDefault("fill.command.area.undo", "Esse mobi deixou de ser selecionado para o preenchimento por área.")));
                    return;
                }

                if(firstItem.getDefinition().getId() != item.getDefinition().getId()){
                    client.getPlayer().getEntity().removeAttribute("fill_command_item");
                    client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(),Locale.getOrDefault("fill.command.area.incompatible.selection", "Você só pode selecionar mobis se forem iguais!")));
                    return;
                }

                final Position to = item.getPosition().copy();
                client.getPlayer().getEntity().getRoom().getBuilderComponent().fillArea(client, firstItem.getPosition(), to, item.getRotation(), item.getDefinition().getId());
                client.getPlayer().getEntity().removeAttribute("fill_command_item");
                client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(),Locale.getOrDefault("fill.command.area.done","Área preenchida com sucesso!")));
            }
            else{
                client.getPlayer().getEntity().setAttribute("fill_command_item", item);
                client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(),Locale.getOrDefault("fill.command.area.first_item.selected", "Você selecionou o primeiro item, a âncora de seleção do preenchimento por área. Para preencher, clique 2x em outro mobi igual!")));
            }

            return;
        }

        if (client.getPlayer().getIsFurnitureEditing() && !CometExternalSettings.housekeepingFurnitureEdition.isEmpty()) {
            client.send(
                    new OpenLinkMessageComposer(CometExternalSettings.housekeepingFurnitureEdition.replace("{id}", item.getDefinition().getId() + ""))
            );

            return;
        }

        if (client.getPlayer().viewingHeight() && client.getPlayer().getEntity() != null) {
            client.send(
                    new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "A altura do mobi é: " + item.getPosition().getZ())
            );

            return;
        }

        if (client.getPlayer().getIsFurniturePickup()) {
            final Map<Long, RoomItemFloor> floorItems = room.getItems().getFloorItems();
            int count = 0;

            for (final RoomItemFloor floorItem : floorItems.values()) {
                if(floorItem == null) continue;

                if(floorItem.getItemData().getOwnerId() != client.getPlayer().getId()) continue;

                if(floorItem.getDefinition().getId() != item.getDefinition().getId()) continue;

                floorItem.onPickup();

                room.getItems().removeItem(floorItem, client);
                count++;
            }

            client.getPlayer().isFurniturePickup(false);
            client.send(new NotificationMessageComposer(
                    "generic",
                    Locale.getOrDefault("command.pickup.success", "Foram coletados %count% mobis.").replace("%count%", count + "")
            ));
            return;
        }

        client.getPlayer().getQuests().progressQuest(QuestType.EXPLORE_FIND_ITEM, item.getDefinition().getSpriteId());

        if (item.onInteract(client.getPlayer().getEntity(), msg.readInt(), false)) {
            WiredTriggerStateChanged.executeTriggers(client.getPlayer().getEntity(), item);
            WiredTriggerCustomStateChanged.executeTriggers(client.getPlayer().getEntity(), item);

            final List<Position> tilesToUpdate = new ArrayList<>();
            tilesToUpdate.add(new Position(item.getPosition().getX(), item.getPosition().getY(), 0d));

            for (final AffectedTile tile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation())) {
                // what the fuck does this do?
                // room.getEntities().getEntitiesAt(new Position(tile.x, tile.y));
                tilesToUpdate.add(new Position(tile.x, tile.y, 0d));
            }

            for (final Position tileToUpdate : tilesToUpdate) {
                final RoomTile tile = room.getMapping().getTile(tileToUpdate);

                if (tile != null) {
                    tile.reload();

                    room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
                }
            }
        }
    }
}
