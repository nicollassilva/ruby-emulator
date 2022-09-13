package com.cometproject.server.game.commands.user.building;

import com.cometproject.api.config.CometSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.RoomReloadListener;
import com.cometproject.server.game.rooms.types.components.BuildingComponent;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class FillCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        client.getPlayer().getEntity().setSelectionType(SelectionType.NONE);
        client.getPlayer().getEntity().setBuildingType(BuildingType.NONE);
        client.getPlayer().getEntity().setStackCount(0);

        BuildingComponent buildings = client.getPlayer().getEntity().getRoom().getBuilderComponent();
        if (buildings.getBuilderId() != client.getPlayer().getId() && buildings.getBuilderId() != -1 && !buildings.getBuilderName().isEmpty()) {
            sendWhisper(Locale.getOrDefault("command.fill.already_in_use","O usuário {} já está usando esse comando.").replace("{}",buildings.getBuilderName()), client);
            return;
        }

        if (params.length == 0) {
            sendWhisper(Locale.getOrDefault("command.fill.missing_args","Você precisa definir o tipo de preenchimento! Para saber mais, diga ':fill ?'"), client);
            return;
        }

        if(params[0].equals("?")){
            sendAlert(Locale.getOrDefault("command.fill.help", "Tipos de preenchimento:\n\narea: uma região de quadrados será preenchida pelo proximo bloco que colocar, os itens serão colocados em relação a seu Ruby e onde clicar para colocar o item.\n\nstack: os mobis serão empilhados no quadrado em que clicar (caso sejam empilháveis)."), client);
            return;
        }

        switch (params[0].toLowerCase()) {
            default: {
                client.getPlayer().getEntity().setSelectionType(SelectionType.Region);
                sendNotif(Locale.getOrDefault("command.fill.off","Preenchimento desativado."), client);
                buildings.setBuilder(null);
                return;
            }

            case "stack":
            case "pilha": {
                final int stackCount = params.length >= 2 ? Math.max(Math.min(Integer.parseInt(params[1]), CometSettings.FILL_STACK_MAX_HEIGHT), 0) : 0;
                if (stackCount == 0) {
                    sendWhisper(Locale.getOrDefault("command.fill.stack.missing_arg", "Você precisa definir quantos itens serão colocados (1-{})").replace("{}",String.valueOf(CometSettings.FILL_STACK_MAX_HEIGHT)), client);
                    return;
                }

                client.getPlayer().getEntity().setStackCount(stackCount);
                client.getPlayer().getEntity().setSelectionType(SelectionType.Stack);
                client.getPlayer().getEntity().setBuildingType(BuildingType.FILL);
                sendNotif(Locale.getOrDefault("command.fill.stack.on","Preenchimento de pilha definido para {} items.").replace("{}",String.valueOf(stackCount)), client);
                client.getPlayer().getEntity().getRoom().getBuilderComponent().setBuilder(client.getPlayer().getEntity());
                break;
            }

            case "area":
            case "all": {
                client.getPlayer().getEntity().setSelectionType(SelectionType.Region);
                client.getPlayer().getEntity().setBuildingType(BuildingType.FILL);
                sendNotif(Locale.getOrDefault("command.fill.area.on","Preenchimento por área ativo. Para preencher uma área, clique 2x em um mobi!"), client);
                client.getPlayer().getEntity().getRoom().getBuilderComponent().setBuilder(client.getPlayer().getEntity());
                break;
            }
        }

        final RoomReloadListener reloadListener = new RoomReloadListener(client.getPlayer().getEntity().getRoom(), (players, newRoom) -> {
            newRoom.getBuilderComponent().setBuilder(null);
        });
        RoomManager.getInstance().addReloadListener(client.getPlayer().getEntity().getRoom().getId(), reloadListener);
    }

    @Override
    public String getPermission() {
        return "fillarea_command";
    }

    @Override
    public String getParameter() {
        return "(area/pilha/desativar)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.fill.description", "Preenche uma região com mobis.");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
