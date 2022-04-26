package com.cometproject.server.game.players.components;

import com.cometproject.api.game.players.data.components.PlayerPermissions;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.permissions.types.CommandPermission;
import com.cometproject.server.game.permissions.types.OverrideCommandPermission;
import com.cometproject.server.game.permissions.types.Rank;
import com.cometproject.server.game.players.types.Player;


public class PermissionComponent implements PlayerPermissions {
    private final Player player;

    public PermissionComponent(Player player) {
        this.player = player;
    }

    @Override
    public Rank getRank() {
        return PermissionsManager.getInstance().getRank(this.player.getData().getRank());
    }

    @Override
    public boolean hasCommand(String key) {

        if (PermissionsManager.getInstance().getOverrideCommands().containsKey(key)) {
            OverrideCommandPermission permission = PermissionsManager.getInstance().getOverrideCommands().get(key);

            if (permission.getPlayerId() == this.getPlayer().getData().getId() && permission.isEnabled() || permission.getRankId() == this.getPlayer().getData().getRank() && permission.isEnabled()) {
                return true;
            }
        }

        if (PermissionsManager.getInstance().getCommands().containsKey(key)) {
            CommandPermission permission = PermissionsManager.getInstance().getCommands().get(key);

            if(permission.getOverride().equals(CommandPermission.RIGHTS_OVERRIDE.OWNER) && (this.player.getEntity().getRoom().getData().getOwnerId() == this.player.getId()))
                return true;

            if(permission.getOverride().equals(CommandPermission.RIGHTS_OVERRIDE.RIGHTS) && (this.player.getEntity().hasRights()))
                return true;

            if (permission.getMinimumRank() <= this.getPlayer().getData().getRank()) {
                return (!permission.isVipOnly() || player.getData().isVip()) && (!permission.isRightsOnly() || player.getEntity().hasRights() || player.getPermissions().getRank().roomFullControl());
            }
        } else if (key.equals("debug") && Comet.isDebugging) {
            return true;
        } else return key.equals("dev");

        return false;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void dispose() {

    }
}