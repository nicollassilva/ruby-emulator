package com.cometproject.server.game.pets.races.plants;

public class PetMonsterPlantColor {
    private final String name;
    private final int id;

    public PetMonsterPlantColor(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
}

