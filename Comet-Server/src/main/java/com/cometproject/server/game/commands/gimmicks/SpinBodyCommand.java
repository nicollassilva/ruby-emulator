package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.sessions.Session;

public class SpinBodyCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final PlayerEntity entity = client.getPlayer().getEntity();
        if (entity == null)
            return;

        final boolean status = !entity.isSpinBody();
        if (!status)
            entity.setSpinBodyRotation(2);

        entity.setIsSpinBody(status);

        sendWhisper((status ? "O seu corpo está a girar!" : "Comando desativado com sucesso!"), client);
    }

    @Override
    public String getPermission() {
        return "spin_body_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.spin_body.description", "Girar o corpo.");
    }
}
