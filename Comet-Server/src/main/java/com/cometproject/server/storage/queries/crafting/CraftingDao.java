package com.cometproject.server.storage.queries.crafting;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.server.game.gamecenter.GameCenterInfo;
import com.cometproject.server.game.items.crafting.CraftingMachine;
import com.cometproject.server.game.items.crafting.CraftingRecipe;
import com.cometproject.server.game.items.crafting.CraftingRecipeMode;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CraftingDao {
    public static void loadRecipes(CraftingMachine machine) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM furniture_crafting_recipes WHERE machineBaseId = ?", sqlConnection);
            preparedStatement.setInt(1, machine.getBaseId());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final AchievementType achievement = AchievementType.getTypeByName(resultSet.getString("achievement"));

                if(resultSet.getString("mode").equals("public"))
                    machine.getPublicRecipes().add(new CraftingRecipe(resultSet.getInt("id"), resultSet.getString("items"), resultSet.getString("result"), resultSet.getInt("result_limit"), resultSet.getInt("result_crafted"), resultSet.getString("badge"), achievement, CraftingRecipeMode.PUBLIC));
                if(resultSet.getString("mode").equals("secret"))
                    machine.getSecretRecipes().add(new CraftingRecipe(resultSet.getInt("id"), resultSet.getString("items"), resultSet.getString("result"), resultSet.getInt("result_limit"), resultSet.getInt("result_crafted"), resultSet.getString("badge"), achievement, CraftingRecipeMode.PRIVATE));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void loadAllowedItems(CraftingMachine machine) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT itemName, itemId, machineBaseId FROM furniture_crafting_items WHERE machineBaseId = ?", sqlConnection);
            preparedStatement.setInt(1, machine.getBaseId());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                machine.getAllowedItems().put(resultSet.getInt("itemId"), resultSet.getString("itemName"));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updateLimitedRecipe(CraftingRecipe recipe) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("UPDATE rewards_crafting_recipes SET result_crafted = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, recipe.getResultTotalCrafted());
            preparedStatement.setInt(1, recipe.getId());

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static List<GameCenterInfo> getGames() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet config = null;
        final List<GameCenterInfo> gameList = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("SELECT * FROM gamecenter_list WHERE visible = '1'", sqlConnection);
            config = preparedStatement.executeQuery();

            while (config.next()) {
                final GameCenterInfo game = new GameCenterInfo(config.getInt("id"), config.getString("name"), config.getString("path"), config.getInt("roomId"));
                gameList.add(game);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(config);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return gameList;
    }
}
