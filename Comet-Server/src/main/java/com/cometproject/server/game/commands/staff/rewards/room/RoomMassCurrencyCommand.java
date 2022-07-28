package com.cometproject.server.game.commands.staff.rewards.room;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.webhooks.RoomMassCurrencyWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;

public abstract class RoomMassCurrencyCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1 || params[0].isEmpty() || !StringUtils.isNumeric(params[0]))
            return;

        final Room room = client.getPlayer().getEntity().getRoom();
        final int amount = Integer.parseInt(params[0]);

        for (final RoomEntity entity : room.getEntities().getPlayerEntities()) {
            if (entity.getEntityType() == RoomEntityType.PLAYER) {
                final PlayerEntity playerEntity = (PlayerEntity) entity;

                try {

                    String currencyType = "coins";

                    if (this instanceof RoomMassCoinsCommand) {
                        playerEntity.getPlayer().getData().increaseCredits(amount);

                        RoomMassCurrencyWebhook.send(client.getPlayer().getData().getUsername(), amount, "moedas", room.getId());
                    } else if (this instanceof RoomMassPointsCommand) {
                        playerEntity.getPlayer().getData().increaseVipPoints(amount);
                        currencyType = "vip.points";
                        this.logDesc = "%s enviou %n diamantes para o quarto"
                                .replace("%n", Integer.toString(amount));

                        RoomMassCurrencyWebhook.send(client.getPlayer().getData().getUsername(), amount, "diamantes", room.getId());
                    } else if (this instanceof RoomMassSeasonalCommand) {
                        playerEntity.getPlayer().getData().increaseSeasonalPoints(amount);
                        currencyType = "seasonal";
                        this.logDesc = "%s enviou %n rubis para o quarto"
                                .replace("%n", Integer.toString(amount));

                        RoomMassCurrencyWebhook.send(client.getPlayer().getData().getUsername(), amount, "rubis", room.getId());
                    }

                    if (!currencyType.equals("coins")) {
                        playerEntity.getPlayer().getSession().send(playerEntity.getPlayer().composeCurrenciesBalance());
                        this.logDesc = "%s enviou %n moedas para o quarto"
                                .replace("%n", Integer.toString(amount));
                    }

                    playerEntity.getPlayer().getSession().send(new AdvancedAlertMessageComposer(
                            Locale.get("command.points.successtitle"),
                            Locale.get("command.points.successmessage").replace("%amount%", String.valueOf(amount))
                                    .replace("%type%", Locale.get(currencyType + ".name"))
                    ));

                    playerEntity.getPlayer().getData().save();
                    playerEntity.getPlayer().sendBalance();
                } catch (Exception ignored) {

                }
            }
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return CometExternalSettings.enableStaffMessengerLogs;
    }
}
