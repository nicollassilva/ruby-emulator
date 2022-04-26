package com.cometproject.server.game.pets.data;

import com.cometproject.api.game.pets.IPetData;
import com.cometproject.api.game.rooms.entities.RoomEntityStatus;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.ai.pets.PetAI;
import com.cometproject.server.game.rooms.objects.entities.types.ai.pets.PetGesture;
import com.cometproject.server.storage.StorageManager;
import com.cometproject.server.storage.queries.pets.PetDao;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PetData implements IPetData {
    private int id;
    private final String name;

    private int scratches;

    private int level;

    private int happiness;
    private int experience;
    private int energy;
    private int hunger;
    private final int ownerId;
    private int roomId;
    private String ownerName;
    private final String colour;
    private String extraData;
    private int raceId;
    private final int typeId;
    private int hairDye = 0;

    private int hair = -1;
    private boolean anyRider = false;

    private boolean saddled = false;
    private int birthday;

    private Position roomPosition;

    public PetData(int id, String name, int scratches, int level, int happiness, int experience, int energy, int hunger,
                   int ownerId, String ownerName, String colour, int raceId, int typeId, int hairDye, int hair,
                   boolean anyRider, boolean saddled, int birthday, String extraData, Position roomPosition) {
        this.id = id;
        this.name = name;
        this.scratches = scratches;
        this.level = level;
        this.happiness = happiness;
        this.experience = experience;
        this.energy = energy;
        this.hunger = hunger;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.colour = colour;
        this.raceId = raceId;
        this.typeId = typeId;
        this.hairDye = hairDye;
        this.hair = hair;
        this.anyRider = anyRider;
        this.saddled = saddled;
        this.birthday = birthday;
        this.extraData = extraData;
        this.roomPosition = roomPosition;
    }

    public PetData(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.name = data.getString("pet_name");
        this.scratches = data.getInt("scratches");
        this.level = data.getInt("level");
        this.happiness = data.getInt("happiness");
        this.experience = data.getInt("experience");
        this.energy = data.getInt("energy");
        this.hunger = data.getInt("hunger");
        this.ownerId = data.getInt("owner_id");
        this.roomId = data.getInt("room_id");
        this.ownerName = data.getString("owner_name");
        this.colour = data.getString("colour");
        this.raceId = data.getInt("race_id");
        this.typeId = data.getInt("type");
        this.saddled = data.getBoolean("saddled");
        this.hair = data.getInt("hair_style");
        this.hairDye = data.getInt("hair_colour");
        this.anyRider = data.getBoolean("any_rider");
        this.birthday = data.getInt("birthday");
        this.extraData = data.getString("extra_data");
        this.roomPosition = new Position(data.getInt("x"), data.getInt("y"), data.getDouble("z"));
    }

    public PetData(int id, String name, int scratches, int level, int happiness, int experience, int energy, int hunger,
                   int ownerId, String ownerName, String colour, int raceId, int typeId) {
        this.id = id;
        this.name = name;
        this.scratches = scratches;
        this.level = level;
        this.happiness = happiness;
        this.experience = experience;
        this.energy = energy;
        this.hunger = hunger;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.colour = colour;
        this.raceId = raceId;
        this.typeId = typeId;
        this.birthday = (int) Comet.getTime();
        this.extraData = "";
    }

    public PetData(int id, String name, int ownerId, String ownerName, int raceId, int typeId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.raceId = raceId;
        this.typeId = typeId;
        this.colour = "FFFFFF";
        this.level = StaticPetProperties.DEFAULT_LEVEL;
        this.happiness = StaticPetProperties.DEFAULT_HAPPINESS;
        this.experience = StaticPetProperties.DEFAULT_EXPERIENCE;
        this.energy = StaticPetProperties.DEFAULT_ENERGY;
        this.hunger = StaticPetProperties.DEFAULT_HUNGER;
        this.extraData = "";
    }

    public PetData(int type, int race, String color, String name, int userId) {
        this.id = 0;
        this.ownerId = userId;
        this.name = name;
        this.typeId = type;
        this.raceId = race;
        this.colour = color;
        this.experience = 0;
        this.happiness = 100;
        this.energy = 100;
        this.scratches = 0;
        this.level = 0;
        this.hunger = 0;
        this.birthday = (int)Comet.getTime();
        this.level = 1;
        this.extraData = "";
    }


    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", this.id);
        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("scratches", this.scratches);
        jsonObject.addProperty("level", this.level);
        jsonObject.addProperty("happiness", this.happiness);
        jsonObject.addProperty("experience", this.experience);
        jsonObject.addProperty("energy", this.energy);
        jsonObject.addProperty("ownerId", this.ownerId);
        jsonObject.addProperty("ownerName", this.ownerName);
        jsonObject.addProperty("colour", this.colour);
        jsonObject.addProperty("raceId", this.raceId);
        jsonObject.addProperty("typeId", this.typeId);
        jsonObject.addProperty("hairDye", this.hairDye);
        jsonObject.addProperty("hair", this.hair);
        jsonObject.addProperty("anyRider", this.anyRider);
        jsonObject.addProperty("saddled", this.saddled);
        jsonObject.addProperty("birthday", this.birthday);
        jsonObject.addProperty("extra_data", this.birthday);

        final JsonObject roomPosition = new JsonObject();

        if (this.roomPosition != null) {
            roomPosition.addProperty("x", this.roomPosition.getX());
            roomPosition.addProperty("y", this.roomPosition.getY());
            roomPosition.addProperty("z", this.roomPosition.getZ());
        }

        jsonObject.add("roomPosition", roomPosition);

        return jsonObject;
    }

    @Override
    public void saveStats() {
//        MySQLStorageQueues.instance().getPetStatsUpdateQueue().add(this.getId(), this);
        PetDao.saveStats(this.scratches, this.level, this.happiness, this.experience, this.energy, this.hunger, this.id);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void saveHorseData() {
        PetDao.saveHorseData(this.getId(), this.isSaddled(), this.hair, this.hairDye, this.anyRider, this.raceId);
    }

    public void savePlantsData() {
        StorageManager.getInstance().getQueues().getSavePetsQueue().savePet(this);
    }

    @Override
    public void increaseExperience(int amount) {
        this.experience += amount;
    }

    @Override
    public void increaseHappiness(int amount) {
        this.happiness += amount;

        if (this.happiness > 100)
            this.happiness = 100;

         if (this.happiness < 0)
            this.happiness = 0;
    }

    @Override
    public void incrementLevel() {
        this.level += 1;
    }

    @Override
    public void incrementScratches() {
        this.scratches += 1;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getHappiness() {
        return happiness;
    }

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public int getExperienceGoal() {
        return this.level > (PetAI.levelBoundaries.size() - 1) ? PetAI.levelBoundaries.get(19) : PetAI.levelBoundaries.get(this.level);
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public String getExtradata() {
        return this.extraData;
    }

    @Override
    public void setExtradata(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public void decreaseEnergy(int amount) {
        this.energy -= amount;

        if (this.energy < 0) {
            this.energy = 0;
        }
    }

    public int getRoomId() {
        return this.roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public void increaseEnergy(int amount) {
        this.energy += amount;

        if(this.energy > PetManager.getInstance().maxEnergy(this.level))
            this.energy = PetManager.maxEnergy(this.level);

        if(this.energy < 0)
            this.energy = 0;
    }

    @Override
    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public String getColour() {
        return colour;
    }

    @Override
    public int getRaceId() {
        return raceId;
    }

    @Override
    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    @Override
    public String getLook() {
        return this.getTypeId() + " " + this.getRaceId() + " " + this.getColour();
    }

    @Override
    public int getHairDye() {
        return this.hairDye;
    }

    @Override
    public void setHairDye(int hairDye) {
        this.hairDye = hairDye;
    }

    @Override
    public int getHair() {
        return this.hair;
    }

    @Override
    public void setHair(int hair) {
        this.hair = hair;
    }

    @Override
    public int getTypeId() {
        return typeId;
    }

    @Override
    public Position getRoomPosition() {
        return this.roomPosition;
    }

    @Override
    public void setRoomPosition(Position position) {
        this.roomPosition = position;
    }

    @Override
    public boolean isSaddled() {
        return saddled;
    }

    @Override
    public void setSaddled(boolean saddled) {
        this.saddled = saddled;
    }

    @Override
    public boolean isAnyRider() {
        return anyRider;
    }

    @Override
    public void setAnyRider(boolean anyRider) {
        this.anyRider = anyRider;
    }

    @Override
    public int getScratches() {
        return scratches;
    }

    @Override
    public int getBirthday() {
        return birthday;
    }

    @Override
    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public void increaseHunger(int amount) {
        this.hunger += amount;

        if (this.hunger >= 100) {
            this.hunger = 100;
        } else if (this.hunger < 0) {
            this.hunger = 0;
        }
    }

    @Override
    public int getHunger() {
        return hunger;
    }

    public void addHappyness(int amount) {
        this.happiness += amount;

        if (this.happiness > 100)
            this.happiness = 100;

        if (this.happiness < 0)
            this.happiness = 0;
    }

    public void addEnergy(int amount) {
        this.energy += amount;

        if (this.energy > PetManager.getInstance().maxEnergy(this.level))
            this.energy = PetManager.getInstance().maxEnergy(this.level);

        if (this.energy < 0)
            this.energy = 0;
    }
}
