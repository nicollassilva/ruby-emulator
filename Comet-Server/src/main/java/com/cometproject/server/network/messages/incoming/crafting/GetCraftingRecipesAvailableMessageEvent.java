package com.cometproject.server.network.messages.incoming.crafting;

import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.items.crafting.CraftingMachine;
import com.cometproject.server.game.items.crafting.CraftingRecipe;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.crafting.CraftingRecipeResultMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class GetCraftingRecipesAvailableMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        CraftingMachine machine = client.getPlayer().getLastCraftingMachine();

        if(machine == null) { return; }

        final int MachineInRoomId = msg.readInt();
        final int totalItems = msg.readInt();

        final CraftingRecipe[] recipe = {null};
        List<Integer> items = new ArrayList<>();

        for(int i=0; i<totalItems; i++) {
            final int itemId = msg.readInt();
            items.add(client.getPlayer().getInventory().getItem(ItemManager.getInstance().getItemIdByVirtualId(itemId)).getDefinition().getId());
        }

        for(CraftingRecipe recipeX : machine.getSecretRecipes()){
            if(recipeX.getComponents().equals(items) && recipe[0] == null) {
                recipe[0] = recipeX;
            }
        }

        if(recipe[0] != null) {
            client.send(new CraftingRecipeResultMessageComposer(0, true)); //status:0 Receta secreta, status > 0 puedes hacer x veces más esta receta
        } else {
            client.send(new CraftingRecipeResultMessageComposer(0, false)); //status:0 Ninguna receta con este item, status > 0 x recetas llevan este item
        }
    }
}


