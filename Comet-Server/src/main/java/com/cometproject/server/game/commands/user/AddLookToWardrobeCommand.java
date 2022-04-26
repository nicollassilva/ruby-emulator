package com.cometproject.server.game.commands.user;

import com.cometproject.api.game.players.data.types.IWardrobeItem;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.components.types.settings.WardrobeItem;
import com.cometproject.server.network.messages.outgoing.user.wardrobe.WardrobeMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

import java.util.List;

public class AddLookToWardrobeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final String slot = params[0];
        final List<IWardrobeItem> wardrobe = client.getPlayer().getSettings().getWardrobe();

        boolean wardrobeUpdate = false;

        for(final IWardrobeItem item : wardrobe) {
            if(item.getSlot() == Integer.parseInt(slot)) {
                item.setFigure(client.getPlayer().getData().getFigure());
                item.setGender(client.getPlayer().getData().getGender());

                wardrobeUpdate = true;
            }
        }

        if(!wardrobeUpdate) {
            wardrobe.add(new WardrobeItem(Integer.parseInt(slot), client.getPlayer().getData().getGender(), client.getPlayer().getData().getFigure()));
        }

        client.getPlayer().getSettings().setWardrobe(wardrobe);
        PlayerDao.saveWardrobe(JsonUtil.getInstance().toJson(wardrobe), client.getPlayer().getId());
        client.send(new WardrobeMessageComposer(client.getPlayer().getSettings().getWardrobe()));
        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "look_command";
    }

    @Override
    public String getParameter() {
        return "(número de slot para guardar el look)";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
