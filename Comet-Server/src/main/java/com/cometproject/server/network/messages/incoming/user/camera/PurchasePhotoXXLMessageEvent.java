package com.cometproject.server.network.messages.incoming.user.camera;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.furniture.IFurnitureService;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.composers.camera.PurchasedPhotoMessageComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.players.components.types.inventory.InventoryItem;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.storage.api.StorageContext;
import com.cometproject.storage.api.data.Data;
import com.google.common.collect.Sets;
import java.util.Set;

public class PurchasePhotoXXLMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().antiSpam("PurchasePhotoXXLMessageEvent", 0.5)) {
            client.getPlayer().sendPopup(Locale.get("game.photo.toofast.title"), Locale.get("game.photo.toofast.message"));
            return;
        }

        final String code = client.getPlayer().getLastPhoto();
        final long time = System.currentTimeMillis();
        final String photoUrl = CometSettings.cameraPhotoUrl.replace("%photoId%", code);

        final String itemExtraData = "{\"t\":" + time + ",\"u\":\"" + code + "\",\"n\":\"" +
                client.getPlayer().getData().getUsername() + "\",\"m\":\"\",\"s\":" + client.getPlayer().getId() + ",\"w\":\"" +
                photoUrl + "\"}";

        final Data<Long> itemIdData = Data.createEmpty();

        StorageContext.getCurrentContext().getRoomItemRepository().createItem(client.getPlayer().getId(), CometSettings.cameraPhotoItemIdXXL, itemExtraData, itemIdData::set);

        final PlayerItem playerItem = new InventoryItem(itemIdData.get(), CometSettings.cameraPhotoItemIdXXL, itemExtraData);

        client.getPlayer().getInventory().addItem(playerItem);

        client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("camera.photoTaken", "You successfully took a photo!")));
        client.send(new UpdateInventoryMessageComposer());

        client.send(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
        client.send(new PurchasedPhotoMessageComposer());

        client.getPlayer().getAchievements().progressAchievement(AchievementType.CAMERA_PHOTO, 1);
        if (client.getPlayer().getLastPurchasedPhoto() == null) {
            StorageContext.getCurrentContext().getPhotoRepository().savePhoto(client.getPlayer().getId(), client.getPlayer().getEntity().getRoom().getId(), photoUrl, (int) time / 1000);
            client.getPlayer().setLastPurchasedPhoto(code);
        }
    }
}
