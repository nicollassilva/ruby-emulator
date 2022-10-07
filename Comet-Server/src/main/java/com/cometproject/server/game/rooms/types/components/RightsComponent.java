package com.cometproject.server.game.rooms.types.components;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.groups.types.IGroup;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.types.RoomBan;
import com.cometproject.server.game.rooms.types.components.types.RoomMute;
import com.cometproject.server.storage.queries.rooms.RightsDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class RightsComponent {
    private final Room room;

    private List<Integer> rights;
    private final Map<Integer, RoomBan> bannedPlayers;
    private final List<RoomMute> mutedPlayers;

    public RightsComponent(Room room) {
        this.room = room;

        try {
            if (room.getCachedData() != null) {
                this.rights = room.getCachedData().getRights();
            } else {
                this.rights = RightsDao.getRightsByRoomId(room.getId());
            }
        } catch (Exception e) {
            this.rights = new CopyOnWriteArrayList<>();
            this.room.log.error("Error while loading room rights", e);
        }

        this.bannedPlayers = RightsDao.getRoomBansByRoomId(this.room.getId());
        this.mutedPlayers = new CopyOnWriteArrayList<>();
    }

    public void dispose() {
        this.rights.clear();
        this.mutedPlayers.clear();
        this.bannedPlayers.clear();
    }

    public boolean hasRights(int playerId) {
        return this.hasRights(playerId, true);
    }

    public boolean hasRights(int playerId, boolean checkGroup) {
        if (checkGroup && checkGroupRights(playerId)) return true;

        return this.room.getData().getOwnerId() == playerId || this.rights.contains(playerId);
    }

    private boolean checkGroupRights(int playerId) {
        final IGroup group = this.getRoom().getGroup();

        if (group != null && group.getData() != null && group.getMembers() != null && group.getMembers().getAll() != null) {
            if (group.getData().canMembersDecorate() && group.getMembers().getAll().containsKey(playerId)) {
                return true;
            }

            return group.getMembers().getAdministrators().contains(playerId);
        }
        return false;
    }

    public boolean canPlaceFurniture(final int playerId) {
        if (checkGroupRights(playerId)) return true;

        if (this.hasRights(playerId, false) && CometSettings.playerRightsItemPlacement) {
            return true;
        }

        return this.room.getData().getOwnerId() == playerId;
    }

    public void removeRights(int playerId) {
        if (this.rights.contains(playerId)) {
            this.rights.remove(rights.indexOf(playerId));
            RightsDao.delete(playerId, room.getId());
        }
    }

    public void addRights(int playerId) {
        this.rights.add(playerId);
        RightsDao.add(playerId, this.room.getId());
    }

    public void addBan(int playerId, String playerName, int expireTimestamp) {
        this.bannedPlayers.put(playerId, new RoomBan(playerId, playerName, expireTimestamp));
        RightsDao.addRoomBan(playerId, this.room.getId(), expireTimestamp);
    }

    public void updateMute(int playerId, int minutes) {
        for (RoomMute mute : this.mutedPlayers.stream().filter(x -> x != null && x.getPlayerId() == playerId).collect(Collectors.toList())) {
            mute.setTicksLeft(minutes);
        }
    }

    public void addMute(int playerId, int minutes) {
        this.mutedPlayers.add(new RoomMute(playerId, (minutes * 60) * 2));
    }

    public boolean hasBan(int userId) {
        return this.bannedPlayers.containsKey(userId);
    }

    public void removeBan(int playerId) {
        this.bannedPlayers.remove(playerId);

        // delete it from the db.
        RightsDao.deleteRoomBan(playerId, this.room.getId());
    }

    public boolean hasMute(int playerId) {
        for (RoomMute mute : this.mutedPlayers) {
            if (mute.getPlayerId() == playerId) {
                return true;
            }
        }

        return false;
    }

    public int getMuteTime(int playerId) {
        for (RoomMute mute : this.mutedPlayers) {
            if (mute.getPlayerId() == playerId) {
                return (mute.getTicksLeft() / 2);
            }
        }

        return 0;
    }

    public void tick() {
        final List<RoomBan> bansToRemove = new ArrayList<>();
        final List<RoomMute> mutesToRemove = new ArrayList<>();

        for (final RoomBan ban : this.bannedPlayers.values()) {
            if (ban.getExpireTimestamp() <= Comet.getTime() && !ban.isPermanent()) {
                bansToRemove.add(ban);
            }
        }

        for (final RoomMute mute : this.mutedPlayers) {
            if (mute.getTicksLeft() <= 0) {
                mutesToRemove.add(mute);
            }

            mute.decreaseTicks();
        }


        for (final RoomBan ban : bansToRemove) {
            this.bannedPlayers.remove(ban.getPlayerId());
        }

        for (final RoomMute mute : mutesToRemove) {
            this.mutedPlayers.remove(mute);
        }

        bansToRemove.clear();
        mutesToRemove.clear();
    }

    public Map<Integer, RoomBan> getBannedPlayers() {
        return this.bannedPlayers;
    }

    public List<Integer> getAll() {
        return this.rights;
    }

    public Room getRoom() {
        return this.room;
    }
}
