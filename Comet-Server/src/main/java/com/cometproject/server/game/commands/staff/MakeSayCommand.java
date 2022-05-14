package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class MakeSayCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) return;

        final String player = params[0];
        final String message = this.merge(params, 1);

        final Room room = client.getPlayer().getEntity().getRoom();
        final PlayerEntity playerEntity = (PlayerEntity) room.getEntities().getEntityByName(player, RoomEntityType.PLAYER);

        room.getEntities().broadcastMessage(new TalkMessageComposer(playerEntity.getId(), message, RoomManager.getInstance().getEmotions().getEmotion(message), 0));
    }

    @Override
    public String getPermission() {
        return "makesay_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username" + " " + "command.parameter.message", "(usuário) (mensagem)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.makesay.description");
    }
}
