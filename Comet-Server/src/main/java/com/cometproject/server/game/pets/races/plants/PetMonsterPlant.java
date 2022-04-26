package com.cometproject.server.game.pets.races.plants;

public class PetMonsterPlant {
    private String name;
    private final int rarity;
    private final int lifeTime;
    private final int id;
    private final int growthTime;

    public PetMonsterPlant(int id, String name, int rarity, int lifeTime, int growthTime) {
        this.name = name;
        this.rarity = rarity;
        this.lifeTime = lifeTime;
        this.id = id;
        this.growthTime = growthTime;
    }

    public int getGrowthTime() {
        return this.growthTime;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLifeTime() {
        return this.lifeTime;
    }

    public int getId() {
        return this.id;
    }

    public int getRarity() {
        return this.rarity;
    }
}

