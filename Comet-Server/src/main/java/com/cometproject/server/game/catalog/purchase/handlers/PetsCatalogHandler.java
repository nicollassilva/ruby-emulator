package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.pets.data.PetData;
import com.cometproject.server.game.pets.data.StaticPetProperties;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.PetInventoryMessageComposer;
import com.cometproject.server.storage.queries.pets.PetDao;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;

public class PetsCatalogHandler extends PurchaseHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        String petRace = item.getDisplayName().replace("a0 pet", "");
        String[] petData = data.split("\n"); // [0:name, 1:race, 2:colour]

        if (petData.length == 3) {
            if (PetManager.getInstance().validatePetName(petData[0]) == 0) {
                int petId = PetDao.createPet(client.getPlayer().getId(), petData[0], Integer.parseInt(petRace), Integer.parseInt(petData[1]), petData[2], "");
                client.getPlayer().getAchievements().progressAchievement(AchievementType.PET_LOVER, 1);
                client.getPlayer().getPets().addPet(new PetData(petId, petData[0], 0, StaticPetProperties.DEFAULT_LEVEL, StaticPetProperties.DEFAULT_HAPPINESS, StaticPetProperties.DEFAULT_EXPERIENCE, StaticPetProperties.DEFAULT_ENERGY, StaticPetProperties.DEFAULT_HUNGER, client.getPlayer().getId(), client.getPlayer().getData().getUsername(), petData[2], Integer.parseInt(petData[1]), Integer.parseInt(petRace)));
                client.send(new PetInventoryMessageComposer(client.getPlayer().getPets().getPets()));
                client.send(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                    put(3, Lists.newArrayList(petId));
                }}));
            }
        }
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
