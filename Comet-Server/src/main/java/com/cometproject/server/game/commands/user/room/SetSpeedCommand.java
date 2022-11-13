package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomDao;


public class SetSpeedCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper(Locale.getOrDefault("command.setspeed.none", "Para mudar a velocidade digite :setspeed (número)"), client);
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
                sendNotif(Locale.getOrDefault("command.need.rights", "You need rights to use this command!"), client);
                return;
            }

            try {
                int speed = Integer.parseInt(params[0]);

                if (speed < 0) {
                    speed = 3;
                } else if (speed > 20) {
                    speed = 20;
                }

                //client.getPlayer().getEntity().getRoom().setAttribute("customRollerSpeed", speed);
                client.getPlayer().getEntity().getRoom().getData().setRollerSpeedLevel(speed);

                sendNotif(Locale.get("command.setspeed.set").replace("%s", speed + ""), client);

                RoomDao.rollerSpeedRoom(speed, client.getPlayer().getEntity().getRoom().getId());
                RoomDao.rollerSpeed(client.getPlayer().getEntity().getRoom().getId());
            } catch (Exception e) {
                sendNotif(Locale.getOrDefault("command.setspeed.invalid", "Por favor, utilize somente números!"), client);
            }
        }
    }

    @Override
    public String getPermission() {
        return "setspeed_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.number", "(número)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.setspeed.description", "Establece la velocidad de los roller en tu sala");
    }
}
