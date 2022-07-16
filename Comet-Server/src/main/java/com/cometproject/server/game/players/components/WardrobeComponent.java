package com.cometproject.server.game.players.components;

import com.cometproject.api.game.catalog.types.IClothingItem;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.storage.queries.player.PlayerClothingDao;
import com.cometproject.server.utilities.collections.ConcurrentHashSet;

import java.util.Set;

public class WardrobeComponent {
    private final Set<IClothingItem> purchasedClothing;

    private final Player player;

    public WardrobeComponent(final Player player) {
        this.player = player;

        this.purchasedClothing = new ConcurrentHashSet<>();
        PlayerClothingDao.getClothing(this.player.getId(), this.purchasedClothing);
    }

    public void dispose() {
        this.purchasedClothing.clear();
    }

    public Set<IClothingItem> getClothing() {
        return purchasedClothing;
    }
}
