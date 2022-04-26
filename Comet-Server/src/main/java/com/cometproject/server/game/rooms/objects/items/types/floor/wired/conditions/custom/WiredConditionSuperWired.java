package com.cometproject.server.game.rooms.objects.items.types.floor.wired.conditions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.data.ScoreboardItemData;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public class WiredConditionSuperWired extends WiredConditionItem {


    public WiredConditionSuperWired(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public int getInterface() {
        return 11;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {

        Room team = this.getRoom();

        String str = this.getWiredData().getText();
        String[] finalText = str.split(":");

        switch (finalText[0].toLowerCase()) {
            case "enable":
                if (entity == null) {
                    return false;
                }
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return entity.getCurrentEffect().getEffectId() == Integer.parseInt(finalText[1]);
            case "noenable":
                if (entity == null) {
                    return false;
                }
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return entity.getCurrentEffect().getEffectId() != Integer.parseInt(finalText[1]);
            case "handitem":
                if (entity == null) {
                    return false;
                }
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return entity.getHandItem() == Integer.parseInt(finalText[1]);
            case "nohanditem":
                if (entity == null) {
                    return false;
                }
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return entity.getHandItem() != Integer.parseInt(finalText[1]);
            case "mission":
                if (entity == null) {
                    return false;
                }

                return entity.getMotto().equalsIgnoreCase(finalText[1]);
            case "nomission":
                if (entity == null) {
                    return false;
                }

                return !entity.getMotto().equals(finalText[1]);
            case "dance":
                if (entity == null) {
                    return false;
                }

                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return entity.getDanceId() == Integer.parseInt(finalText[1]);
            case "teamred-max":
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.RED).size() <= Integer.parseInt(finalText[1]);
            case "teamred-min":
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.RED).size() >= Integer.parseInt(finalText[1]);
            case "teamblue-max":
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.BLUE).size() <= Integer.parseInt(finalText[1]);
            case "teamblue-min":
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.BLUE).size() >= Integer.parseInt(finalText[1]);
            case "teamyellow-max":
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.YELLOW).size() <= Integer.parseInt(finalText[1]);
            case "teamyellow-min":

                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }
                return team.getGame().getTeams().get(GameTeam.YELLOW).size() >= Integer.parseInt(finalText[1]);
            case "teamgreen-max":
                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.GREEN).size() <= Integer.parseInt(finalText[1]);
            case "teamgreen-min":

                if (!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.GREEN).size() >= Integer.parseInt(finalText[1]);

            case "allteams-max":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().size() < Integer.parseInt(finalText[1]);

            case "allteams-min":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().size() > Integer.parseInt(finalText[1]);

            case "teamredcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.RED).size() == Integer.parseInt(finalText[1]);
            case "teambluecount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.BLUE).size() == Integer.parseInt(finalText[1]);
            case "teamgreencount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.GREEN).size() == Integer.parseInt(finalText[1]);
            case "teamyellowcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.YELLOW).size() == Integer.parseInt(finalText[1]);
            case "teamrednotcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.RED).size() != Integer.parseInt(finalText[1]);
            case "teambluenotcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.BLUE).size() != Integer.parseInt(finalText[1]);
            case "teamgreennotcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.GREEN).size() != Integer.parseInt(finalText[1]);
            case "teamyellownotcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().get(GameTeam.YELLOW).size() == Integer.parseInt(finalText[1]);
            case "teamallcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().size() == Integer.parseInt(finalText[1]);
            case "teamallnotcount":
                if(!StringUtils.isNumeric(finalText[1])) {
                    return false;
                }

                return team.getGame().getTeams().size() != Integer.parseInt(finalText[1]);

            case "hastag":
                if(entity == null) {
                    return false;
                }

                return entity.getTagUser().contains(finalText[1]);

            case "nohastag":
                if(entity == null) {
                    return false;
                }

                return !entity.getTagUser().contains(finalText[1]);

            case "haspoints":
                if(entity == null) {
                    return false;
                }

                return nearestPlayerEntity().getPoints() == Integer.parseInt(finalText[1]);

            case "nohaspoints":
                if(entity == null) {
                    return false;
                }

                return nearestPlayerEntity().getPoints() != Integer.parseInt(finalText[1]);

            case "haspointsmin":
                if(entity == null) {
                    return false;
                }

                return nearestPlayerEntity().getPoints() >= Integer.parseInt(finalText[1]);

            case "haspointsmax":
                if(entity == null) {
                    return false;
                }

                return nearestPlayerEntity().getPoints() <= Integer.parseInt(finalText[1]);

            case "botusername":
                if(entity == null) {
                    return false;
                }

                return nearestBotEntity().getData().getUsername().equals(finalText[1]);

            case "nobotusername":
                if(entity == null) {
                    return false;
                }

                return !nearestBotEntity().getData().getUsername().equals(finalText[1]);
        }
        return false;
    }
}
