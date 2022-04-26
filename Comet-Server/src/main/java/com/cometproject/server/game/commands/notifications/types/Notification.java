package com.cometproject.server.game.commands.notifications.types;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Notification {
    private final String trigger;
    private final String text;
    private final NotificationType type;
    private final int minRank;
    private final int coolDown;
    private final String image;

    public Notification(ResultSet data) throws SQLException {
        this.trigger = data.getString("name");
        this.text = data.getString("text");
        this.type = NotificationType.valueOf(data.getString("type").toUpperCase());
        this.minRank = data.getInt("min_rank");
        this.coolDown = data.getInt("cooldown");
        this.image = data.getString("image");
    }

    public void execute(Player player) {
        if ((player.getNotifCooldown() + coolDown) >= Comet.getTime()) {
            return;
        }

        switch (this.type) {
            case GLOBAL:
                NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer(this.image, this.text));
                break;

            case LOCAL:
                player.sendNotif(this.image, this.text);
                break;

            case ROOM:
                player.getEntity().getRoom().getEntities().broadcastMessage(new NotificationMessageComposer(this.image, this.text));
        }

        player.setNotifCooldown((int) Comet.getTime());
    }

    public String getTrigger() {
        return trigger;
    }

    public String getText() {
        return text;
    }

    public NotificationType getType() {
        return type;
    }

    public int getMinRank() {
        return minRank;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public String getImage() {
        return image;
    }
}
