package com.cometproject.server.game.items.crafting;

import com.cometproject.api.game.achievements.types.AchievementType;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipe {
    private final int id;
    private final CraftingRecipeMode mode;
    private final List<Integer> components = new ArrayList<>();
    private final String resultProductData;
    private final int resultBaseId;
    private final int resultLimitedSells;
    private int resultTotalCrafted;
    private final String badge;
    private final AchievementType achievement;

    public CraftingRecipe(int recipeId, String components, String result, int resultLimitedSells, int resultTotalCrafted, String badge, AchievementType achievement, CraftingRecipeMode mode) {
        this.id = recipeId;

        final String[] allComponents = components.split(",");

        for(final String completeComponent : allComponents) {
            final String[] splitComponent = completeComponent.split(":");

            for(int i = 0; i < Integer.parseInt(splitComponent[1]); i++) {
                this.components.add(Integer.valueOf(splitComponent[0]));
            }
        }

        final String[] splitResult = result.split(":");

        this.resultProductData = splitResult[0]; // item_name
        this.resultBaseId = Integer.parseInt(splitResult[1]); // id
        this.resultLimitedSells = resultLimitedSells;
        this.resultTotalCrafted = resultTotalCrafted;
        this.badge = badge;
        this.achievement = achievement;
        this.mode = mode;
    }

    public int getId() { return this.id; }

    public CraftingRecipeMode getMode() { return this.mode; }

    public List<Integer> getComponents() { return this.components; }

    public String getResultProductData() { return this.resultProductData; }

    public int getResultBaseId() { return this.resultBaseId; }

    public int getResultLimitedSells() { return this.resultLimitedSells; }

    public int getResultTotalCrafted() { return this.resultTotalCrafted; }

    public void increateTotalCrafted() { this.resultTotalCrafted++; }

    public String getBadge() { return this.badge; }

    public AchievementType getAchievement() { return this.achievement; }
}
