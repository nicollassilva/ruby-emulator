package com.cometproject.server.game.pets.data;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.pets.PetManager;
import com.cometproject.server.game.pets.races.plants.PetMonsterPlant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PetMonsterPlantData extends PetData {
    private final int bodyRarity;
    private final int colorRarity;
    private final int seedRarity;
    private final PetMonsterPlant body;
    private final int nose;
    private final int noseColor;
    private final int eyes;
    private final int eyesColor;
    private final int mouth;
    private final int mouthColor;
    public static int timeToLive = (3 * 24 * 60 * 60);
    public static int growTime = (30 * 60);
    private int deathTimestamp = (int)Comet.getTime() + timeToLive;
    private final int hue;
    private final int lastFood;
    private int lastDiamonds;
    private boolean isDead;
    private boolean hasBreed;
    private boolean isActive;
    private final int plantType;
    private int growthStage;

    public PetMonsterPlantData(ResultSet set) throws SQLException {
        super(set);
        JsonObject object = new Gson().fromJson(this.getExtradata(), JsonObject.class);
        this.seedRarity = object.get("se").getAsInt();
        this.plantType = object.get("t").getAsInt();
        this.hue = object.get("c").getAsInt();
        this.nose = object.get("n").getAsInt();
        this.noseColor = object.get("nc").getAsInt();
        this.eyes = object.get("e").getAsInt();
        this.eyesColor = object.get("ec").getAsInt();
        this.mouth = object.get("m").getAsInt();
        this.mouthColor = object.get("mc").getAsInt();
        this.bodyRarity = object.get("mb").getAsInt();
        this.colorRarity = object.get("mcr").getAsInt();
        this.lastFood = object.get("ml").getAsInt();
        this.isDead = object.get("id").getAsBoolean();
        this.hasBreed = object.get("hb").getAsBoolean();
        this.lastDiamonds = object.get("lg").getAsInt();
        this.isActive = object.get("pb").getAsBoolean();
        this.body = PetManager.getInstance().getMonsterPlantBodies().get(this.bodyRarity);
        this.growthStage = this.getRealGrowthStage();
        this.setExtradata(this.toJson());
    }

    public PetMonsterPlantData(int seedRarity, int bodyRarity, int colorRarity, int userId, int type, int hue, int nose, int noseColor, int mouth, int mouthColor, int eyes, int eyesColor) {
        super(16, 0, "", "", userId);
        this.seedRarity = seedRarity;
        this.plantType = type;
        this.hue = hue;
        this.nose = nose;
        this.noseColor = noseColor;
        this.bodyRarity = bodyRarity;
        this.hasBreed = false;
        this.colorRarity = colorRarity;
        this.mouth = mouth;
        this.mouthColor = mouthColor;
        this.eyes = eyes;
        this.eyesColor = eyesColor;
        this.growthStage = 1;
        this.isActive = false;
        this.lastFood = (int)Comet.getTime();
        this.isDead = false;
        this.body = PetManager.getInstance().getMonsterPlantBodies().get(this.bodyRarity);
        this.lastDiamonds = (int)Comet.getTime();
        this.setExtradata(this.toJson());
    }

    @Override
    public String getName() {
        String name = " " + PetManager.getInstance().getMonsterPlantColors().get(this.colorRarity).getName();
        name += " " + this.body.getName();

        return name;
    }

    public int getRarity() {
        int max = this.bodyRarity + this.colorRarity;
        return this.isDead() ? 0 : (!this.isFullyGrown() ? Math.min(this.growthStage, max) : max);
    }

    public String getLook() {
        return "16 0 FFFFFF " +
                "10 " +
                "0 -1 2 " +
                "1 " + this.plantType + " " + this.hue + " " +
                "2 " + this.mouth + " " + this.mouthColor + " " +
                "3 " + this.nose + " " + this.noseColor + " " +
                "4 " + this.eyes + " " + this.eyesColor + " " + "2";
    }

    public int remainingTimeToLive() {
        return Math.max(0, this.deathTimestamp - (int)Comet.getTime());
    }

    public int getEyesColor() {
        return this.eyesColor;
    }

    public int getEyes() {
        return this.eyes;
    }

    public int getNose() {
        return this.nose;
    }

    public int getNoseColor() {
        return this.noseColor;
    }

    public int getPlantColor() {
        return this.hue;
    }

    public int getMouthColor() {
        return this.mouthColor;
    }

    public int getMouth() {
        return this.mouth;
    }

    public int getRealGrowthStage() {
        int stage = 7 - this.remainingGrowTime() / (this.getGrowTime() / 7) - 1;
        if (stage <= 0) {
            stage = 1;
        }
        if (this.remainingGrowTime() <= 0) {
            stage = 7;
        }
        return stage;
    }

    public int getLastDiamonds() {
        return this.lastDiamonds;
    }

    public void setLastDiamonds(int lastDiamonds) {
        this.lastDiamonds = lastDiamonds;
    }

    public int getGrowthStage() {
        return this.growthStage;
    }

    public String getPlantName() {
        return this.getName();
    }

    public int remainingGrowTime() {
        if (this.growthStage == 7 || this.isDead()) {
            return 0;
        }

        return (int)((long)(this.getBirthday() + this.getGrowTime()) - Comet.getTime());
    }

    public boolean isFullyGrown() {
        return this.growthStage == 7 && !this.isDead();
    }

    public int getGrowTime() {
        return this.body.getGrowthTime();
    }

    public String toJson() {
        final JsonObject object = new JsonObject();
        object.addProperty("t", (Number)this.plantType);
        object.addProperty("c", (Number)this.hue);
        object.addProperty("n", (Number)this.nose);
        object.addProperty("nc", (Number)this.noseColor);
        object.addProperty("e", (Number)this.eyes);
        object.addProperty("ec", (Number)this.eyesColor);
        object.addProperty("m", (Number)this.mouth);
        object.addProperty("mc", (Number)this.mouthColor);
        object.addProperty("mg", (Number)this.growthStage);
        object.addProperty("mb", (Number)this.bodyRarity);
        object.addProperty("mcr", (Number)this.colorRarity);
        object.addProperty("ml", (Number)this.lastFood);
        object.addProperty("id", this.isDead);
        object.addProperty("lg", (Number)this.lastDiamonds);
        object.addProperty("se", (Number)this.seedRarity);
        object.addProperty("hb", this.hasBreed);
        object.addProperty("pb", this.isActive);

        return object.toString();
    }

    public int diamondsStocked() {
        if (this.getRarity() >= 20) {
            // empty if block
        }

        return 0;
    }

    public boolean isDead() {
        return this.getLastFood() <= 0;
    }

    public void setDeathTimestamp(int deathTimestamp) {
        this.deathTimestamp = deathTimestamp;
    }

    public boolean asDead() {
        return this.isDead;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    public int getSeedRarity() {
        return this.seedRarity;
    }

    public boolean canBreed() {
        return !this.hasBreed() && this.isFullyGrown() && !this.isDead();
    }

    public int getLastFood() {
        int time = this.lastFood + this.body.getLifeTime() - (int)Comet.getTime();

        if (time < 0) {
            time = 0;
        }

        return time;
    }

    public PetMonsterPlant getBody() {
        return this.body;
    }

    public void save() {
        this.setExtradata(this.toJson());

        super.savePlantsData();
    }

    public void setGrowthStage(int growthStage) {
        this.growthStage = growthStage;
    }

    public boolean hasBreed() {
        return this.hasBreed;
    }

    public void setHasBreed(boolean hasBreed) {
        this.hasBreed = hasBreed;
    }

    public boolean isActive() {
        return this.isActive && !this.isDead();
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}