package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.CommandManager;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.data.ScoreboardItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.cometproject.api.game.rooms.entities.RoomEntityStatus.SIT;


public class WiredCustomSuperWired extends WiredActionItem {

    public WiredCustomSuperWired(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        if (this.getWiredData() == null || this.getWiredData().getText() == null) {
            return;
        }

        String str = this.getWiredData().getText();
        String[] finalText = str.split(":");
        String comando = finalText[1];

        switch(finalText[0]) {

            case "enable":
                if (StringUtils.isNumeric(comando)) {
                    playerEntity.applyEffect(new PlayerEffect(Integer.parseInt(comando), 0));
                } else {
                    return;
                }
                break;

            case "handitem":
                if (StringUtils.isNumeric(comando)) {
                    playerEntity.carryItem(Integer.parseInt(comando));
                } else {
                    return;
                }
                break;

            case "dance":
                if (StringUtils.isNumeric(comando)) {
                    playerEntity.setDanceId(Integer.parseInt(comando));
                    playerEntity.getRoom().getEntities().broadcastMessage(new DanceMessageComposer(playerEntity.getId(), Integer.parseInt(comando)));

                } else if (Integer.parseInt(comando) < 1 || Integer.parseInt(comando) > 4) {
                    return;
                } else {
                    return;
                }
                break;

            case "toroom":
                if (StringUtils.isNumeric(comando)) {
                    playerEntity.getPlayer().bypassRoomAuth(true);
                    playerEntity.getPlayer().getSession().send(new RoomForwardMessageComposer(Integer.parseInt(comando)));
                } else {
                    return;
                }
                break;
            case "setspeed":
                playerEntity.getRoom().setAttribute("customRollerSpeed", Integer.parseInt(comando));
                break;

            case "freeze":
                if (Integer.parseInt(comando) == 1) {
                    playerEntity.getPlayer().getEntity().setCanWalk(false);
                    playerEntity.getPlayer().getEntity().cancelWalk();
                } else if (Integer.parseInt(comando) == 0) {
                    playerEntity.getPlayer().getEntity().setCanWalk(true);
                } else {
                    return;
                }
                break;

            case "moonwalk":
                if (Integer.parseInt(comando) == 1) {
                    playerEntity.getPlayer().getEntity().setIsMoonwalking(true);
                } else if (Integer.parseInt(comando) == 0) {
                    playerEntity.getPlayer().getEntity().setIsMoonwalking(false);
                } else {
                    return;
                }
                break;

            case "fastwalk":
                if (StringUtils.isNumeric(comando)) {
                    if (Integer.parseInt(comando) == 1) {
                        playerEntity.getPlayer().getEntity().setFastWalkEnabled(true);
                    } else if (Integer.parseInt(comando) == 0) {
                        playerEntity.getPlayer().getEntity().setFastWalkEnabled(false);
                    } else {
                        return;
                    }
                }
                break;

            case "disable":
                if (CommandManager.getInstance().isCommand(comando) && CommandManager.getInstance().getChatCommands().get(comando).canDisable()) {
                    playerEntity.getRoom().getData().getDisabledCommands().add(comando);
                    GameContext.getCurrent().getRoomService().saveRoomData(playerEntity.getRoom().getData());
                }
                break;

            case "roommute":
                if (comando.equals("true")) {
                    playerEntity.getRoom().setRoomMute(true);
                    playerEntity.getPlayer().getSession().send(new AlertMessageComposer("la sala está muteada", null));
                } else if (comando.equals("false")) {
                    playerEntity.getRoom().setRoomMute(false);
                } else {
                    return;
                }
                break;

            case"addpoint":
                if(StringUtils.isNumeric(comando)) {
                    playerEntity.increasePoints(Integer.parseInt(comando));
                } else {
                    return;
                }
                break;

            case "setpoint":
                if(StringUtils.isNumeric(comando)) {
                    playerEntity.setPoints(Integer.parseInt(comando));
                } else {
                    return;
                }
                break;

            case "removepoint":
                if(StringUtils.isNumeric(comando)) {
                    playerEntity.decreasePoints(Integer.parseInt(comando));
                } else {
                    return;
                }
                break;

            case "jointeam":
                if(finalText[1].equalsIgnoreCase("red")) {
                    playerEntity.setGameTeam(GameTeam.RED);
                    this.getRoom().getGame().joinTeam(GameTeam.RED, playerEntity);
                } else if(finalText[1].equalsIgnoreCase("blue")) {
                    playerEntity.setGameTeam(GameTeam.BLUE);
                    this.getRoom().getGame().joinTeam(GameTeam.BLUE, playerEntity);
                } else if(finalText[1].equalsIgnoreCase("yellow")) {
                    playerEntity.setGameTeam(GameTeam.YELLOW);
                    this.getRoom().getGame().joinTeam(GameTeam.YELLOW, playerEntity);
                } else if(finalText[1].equalsIgnoreCase("green")) {
                    playerEntity.setGameTeam(GameTeam.GREEN);
                    this.getRoom().getGame().joinTeam(GameTeam.GREEN, playerEntity);
                } else {
                    return;
                }

                break;
        }
    }
}
