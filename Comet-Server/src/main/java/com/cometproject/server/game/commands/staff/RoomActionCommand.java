package com.cometproject.server.game.commands.staff;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.game.bots.BotMode;
import com.cometproject.api.game.bots.BotType;
import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.api.stats.CometStats;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.bots.BotData;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.entities.types.data.PlayerBotData;
import com.cometproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.SqlHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class RoomActionCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            return;
        }

        final String action = params[0];

        switch (action) {
            case "lista":
                sendAlert("- efeito (número) \n- fala (mensagem) \n- danca (número)\n- sinal (número)\n- bots %count% <i>(Para remover, diga \"saiam minions\")</i>\n- item (número)", client);
                break;
            case "efeito":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                final int effectId = Integer.parseInt(params[1]);

                for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.applyEffect(new PlayerEffect(effectId, 0));
                }
                break;

            case "fala":
                String msg = this.merge(params, 1);

                for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.getRoom().getEntities().broadcastMessage(new TalkMessageComposer(playerEntity.getId(), msg, RoomManager.getInstance().getEmotions().getEmotion(msg), 0));
                }
                break;

            case "danca":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                final int danceId = Integer.parseInt(params[1]);

                for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.setDanceId(danceId);
                    playerEntity.getRoom().getEntities().broadcastMessage(new DanceMessageComposer(playerEntity.getId(), danceId));
                }
                break;

            case "sinal":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.addStatus(RoomEntityStatus.SIGN, String.valueOf(params[1]));

                    playerEntity.markDisplayingSign();
                    playerEntity.markNeedsUpdate();
                }
                break;

            case "bots":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                int count = Integer.parseInt(params[1]);
                final Position entityPosition = client.getPlayer().getEntity().getPosition();

                if (count > 10) { // Crash Security
                    count = 10;
                } else if (count < 0) {
                    count = 1;
                }

                final List<RoomEntity> addedEntities = Lists.newArrayList();

                for (int i = 0; i < count; i++) {
                    final int id = -(i + 1);
                    final String username = client.getPlayer().getData().getUsername() + "Minion" + i;
                    final String motto = "";

                    final BotData botData = new PlayerBotData(
                            id,
                            username,
                            motto,
                            client.getPlayer().getData().getFigure(),
                            client.getPlayer().getData().getGender(),
                            client.getPlayer().getData().getUsername(),
                            client.getPlayer().getId(),
                            "[]",
                            false,
                            false,
                            7,
                            BotType.MIMIC,
                            BotMode.DEFAULT, "");

                    final BotEntity botEntity = client.getPlayer().getEntity().getRoom().getBots().addBot(botData,
                            entityPosition.getX(), entityPosition.getY(), entityPosition.getZ());

                    if (botEntity != null) {
                        addedEntities.add(botEntity);
                    }
                }

                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(addedEntities));
                break;

            case "item":
                final int handItem = Integer.parseInt(params[1]);

                for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.carryItem(handItem, false);
                }

                break;
        }

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s has performed roomaction in room '%b' with parameter %p"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%b", client.getPlayer().getEntity().getRoom().getData().getName())
                .replace("%p", action);
    }

    @Override
    public String getPermission() {
        return "roomaction_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.roomaction.parameters", "%evento%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roomaction.description");
    }

    @Override
    public String getLoggableDescription() {
        return this.logDesc;
    }

    @Override
    public boolean isLoggable() {
        return true;
    }
}
