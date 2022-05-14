package com.cometproject.server.game.catalog.purchase.handlers.items;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class GroupForumCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        if (data.isEmpty() || !StringUtils.isNumeric(data))
            return;

        if (!client.getPlayer().getGroups().contains(Integer.valueOf(data))) {
            return;
        }

        int groupId = Integer.parseInt(data);
        IGroup group = GameContext.getCurrent().getGroupService().getGroup(groupId);

        if (!group.getData().hasForum() && group.getData().getOwnerId() == client.getPlayer().getId()) {
            GameContext.getCurrent().getGroupService().addForum(group);

            Map<String, String> notificationParams = Maps.newHashMap();

            notificationParams.put("groupId", groupId + "");
            notificationParams.put("groupName", group.getData().getTitle());

            client.send(new NotificationMessageComposer("forums.delivered", notificationParams));
        }

        String extraData = "" + groupId;

        super.execute(item, client, amount, page, giftData, definition, bundledItem, data, extraData + ";0");
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
