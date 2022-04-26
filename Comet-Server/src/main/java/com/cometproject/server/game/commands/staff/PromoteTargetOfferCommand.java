package com.cometproject.server.game.commands.staff;

import com.cometproject.api.game.catalog.ITargetOffer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.TargetOffer;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.catalog.TargetedOfferComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.sessions.SessionManager;
import com.cometproject.server.storage.queries.config.ConfigDao;
import gnu.trove.map.hash.THashMap;

public class PromoteTargetOfferCommand extends ChatCommand {
    @Override
    public void execute(final Session client, final String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.getOrDefault("commands.error.cmd_promote_offer.not_found", "Oops, ocorreu um erro ao inserir uma nova target offer."), client);
            return;
        }

        final String offerKey = params[0];

        if (offerKey.equalsIgnoreCase("remove")) {
            TargetOffer.ACTIVE_TARGET_OFFER_ID = 0;
            ConfigDao.saveActiveTargetOffer(0);
            sendNotif(Locale.getOrDefault("commands.cmd_promote_offer.remove", "Oferta removida com sucesso."), client);
            return;
        }

        if (offerKey.equalsIgnoreCase(Locale.getOrDefault("commands.cmd_promote_offer.info", "info"))) {
            this.sendPromoteOfferList(client);
            return;
        }

        int offerId = 0;

        try {
            offerId = Integer.parseInt(offerKey);
        } catch (Exception ignored) {
        }

        if (offerId <= 0) {
            sendNotif(Locale.getOrDefault("commands.error.cmd_promote_offer.not_found", "Oferta não encontrada. Use :promoteoffer info para ver todas as ofertas."), client);
            return;
        }

        final ITargetOffer offer = CatalogManager.getInstance().getTargetOffer(offerId);

        if (offer == null) {
            sendNotif(Locale.getOrDefault("commands.error.cmd_promote_offer.not_found", "Oferta não encontrada. Use :promoteoffer info para ver todas as ofertas."), client);
            return;
        }

        if(offer.getId() == TargetOffer.ACTIVE_TARGET_OFFER_ID) {
            sendNotif(Locale.getOrDefault("commands.error.cmd_promote_offer.already_active", "Essa oferta já está ativa"), client);
            return;
        }

        TargetOffer.ACTIVE_TARGET_OFFER_ID = offer.getId();

        sendNotif(Locale.getOrDefault("commands.success.cmd_promote_offer", "A oferta foi alterada com sucesso para %id%: %title%").replace("%id%", offerKey).replace("%title%", offer.getTitle()), client);
        ConfigDao.saveActiveTargetOffer(offer.getId());

        final SessionManager sessionManager = NetworkManager.getInstance().getSessions();

        for (final ISession onlineSession : sessionManager.getSessions().values()) {
            onlineSession.send(new TargetedOfferComposer(onlineSession.getPlayer(), offer));
        }
    }

    public void sendPromoteOfferList(final Session client) {
        final THashMap<Integer, ITargetOffer> targetOffers = CatalogManager.getInstance().targetOffers;
        String textConfig = Locale.getOrDefault("commands.cmd_promote_offer.list", "Ofertas disponíveis (%amount%):").replace("%amount%", targetOffers.size() + "").concat("\n");
        final String entryConfig = Locale.getOrDefault("commands.cmd_promote_offer.list.entry", "%id%: %title%");

        for (ITargetOffer offer : targetOffers.values()) {
            textConfig = textConfig.concat("\n")
                    .concat(entryConfig
                    .replace("%id%", offer.getId() + "")
                    .replace("%title%", offer.getTitle())
            );
        }

        client.send(new MotdNotificationMessageComposer(textConfig));
    }

    @Override
    public String getPermission() {
        return "promoteoffer_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.promoteoffer", "<offerId> [info]");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.promoteoffer.description");
    }
}
