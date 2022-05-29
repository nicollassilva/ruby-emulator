package com.cometproject.server.game.commands.user.building;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.components.BuildingComponent;
import com.cometproject.server.network.sessions.Session;

public class FillCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        BuildingComponent buildings = client.getPlayer().getEntity().getRoom().getBuilderComponent();
        if (buildings.getBuilderId() != client.getPlayer().getId()) {
            sendWhisper("O usuário '" + buildings.getBuilderName() + "' já está usando esse comando.", client);
            return;
        }

        if (params.length == 0) {
            sendWhisper("Você precisa definir o tipo de preenchimento!", client);
            return;
        }

        switch (params[0].toLowerCase()) {
            default: {
                client.getPlayer().getEntity().setFillType(FillType.FILL_ALL_BLOCKS);
                sendNotif("Preenchimento desativado.", client);
                buildings.setBuilder(null);
                return;
            }

            case "stack":
            case "pilha": {
                final int stackCount = params.length >= 2 ? Math.max(Math.min(Integer.parseInt(params[1]), BuildingComponent.MAX_FILL_STACK_BLOCKS), 0) : 0;
                if (stackCount == 0) {
                    sendWhisper("Você precisa definir quantos itens será colocado (1-" + BuildingComponent.MAX_FILL_STACK_BLOCKS + ")", client);
                    return;
                }

                client.getPlayer().getEntity().setStackCount(stackCount);
                client.getPlayer().getEntity().setFillType(FillType.FILL_STACK);
                sendNotif("Preenchimento de pilha definido para '" + stackCount + "' items.", client);
                client.getPlayer().getEntity().getRoom().getBuilderComponent().setBuilder(client.getPlayer().getEntity());
                return;
            }

            case "area":
            case "all": {
                client.getPlayer().getEntity().setFillType(FillType.FILL_ALL_BLOCKS);
                sendNotif("Preenchimento por área ativo.", client);
                client.getPlayer().getEntity().getRoom().getBuilderComponent().setBuilder(client.getPlayer().getEntity());
                return;
            }
        }
    }

    @Override
    public String getPermission() {
        return "commands_command";
    }

    @Override
    public String getParameter() {
        return "(area/pilha/desativar)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.fill.description", "Preenche uma região com mobis.");
    }
}
