package com.cometproject.server.network.messages.outgoing.catalog;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.Comparator;
import java.util.List;

public class CatalogIndexMessageComposer extends MessageComposer {
    private final int playerRank;
    private String mode = "normal";

    public CatalogIndexMessageComposer(final int playerRank) {
        this.playerRank = playerRank;
    }

    public CatalogIndexMessageComposer(final int playerRank, final String mode) {
        this.playerRank = playerRank;
        this.mode = mode;
    }

    @Override
    public short getId() {
        return Composers.CatalogIndexMessageComposer;
    }

    @Override
    public void compose(final IComposer msg) {
        final List<ICatalogPage> pages = CatalogManager.getInstance().getPagesByRank(this.playerRank, -1);

        msg.writeBoolean(true);
        msg.writeInt(0);
        msg.writeInt(-1);
        msg.writeString("root");
        msg.writeString("");
        msg.writeInt(0);
        msg.writeInt(pages.size());

        for (final ICatalogPage category : pages) {
            if(category.isVipOnly() && this.playerRank != CometSettings.vipRank && this.playerRank < CometSettings.rankCanSeeVipContent) continue;

            append(category, msg, false);
        }

        msg.writeBoolean(false);
        msg.writeString(this.mode);
    }

    private void append(ICatalogPage category, IComposer msg, boolean order) {
        final List<ICatalogPage> pagesList = CatalogManager.getInstance().getPagesByRank(this.playerRank, category.getId());

        if (order) {
            pagesList.sort(Comparator.comparing(ICatalogPage::getCaption));
        }

        msg.writeBoolean(true);
        msg.writeInt(category.getIcon());
        msg.writeInt(category.isEnabled() ? category.getId() : -1);
        msg.writeString(category.getLinkName().equals("undefined") ? category.getCaption().toLowerCase().replaceAll("[^A-Za-z0-9]", "").replace(" ", "_") : category.getLinkName());
        msg.writeString(this.playerRank > 8 && CometSettings.catalogPageIdEnabled ? category.getCaption() + " (" + category.getId() + ")" : category.getCaption());
        msg.writeInt(category.getOfferSizeByRank(this.playerRank));

        for (final ICatalogItem item : category.getItems().values()) {
            if(category.isVipOnly() && this.playerRank != CometSettings.vipRank && this.playerRank < CometSettings.rankCanSeeVipContent) continue;

            if (this.playerRank >= category.getMinRank() && category.getTemplate().equals("default_3x3")) {
                msg.writeInt(item.getId());
            }
        }

        msg.writeInt(pagesList.size());

        for (final ICatalogPage page : pagesList) {
            append(page, msg, true);
        }
    }
}
