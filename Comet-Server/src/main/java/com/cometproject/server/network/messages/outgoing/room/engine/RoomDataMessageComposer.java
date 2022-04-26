package com.cometproject.server.network.messages.outgoing.room.engine;

import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomWriter;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class RoomDataMessageComposer extends MessageComposer {
    private final IRoomData roomData;
    private final boolean checkEntry;
    private final boolean canMute;
    private final boolean isLoading;
    private final boolean skipAuth;

    public RoomDataMessageComposer(final IRoomData room, boolean checkEntry, boolean canMute) {
        this.roomData = room;
        this.checkEntry = checkEntry;
        this.canMute = canMute;
        this.isLoading = false;
        this.skipAuth = false;
    }

    public RoomDataMessageComposer(final IRoomData room, boolean checkEntry, boolean canMute, boolean isLoading) {
        this.roomData = room;
        this.checkEntry = checkEntry;
        this.canMute = canMute;
        this.isLoading = isLoading;
        this.skipAuth = false;
    }

    public RoomDataMessageComposer(final IRoomData room, boolean isLoading, boolean checkEntry, boolean skipAuth, boolean canMute) {
        this.roomData = room;
        this.isLoading = isLoading;
        this.checkEntry = checkEntry;
        this.canMute = canMute;
        this.skipAuth = skipAuth;
    }

    public RoomDataMessageComposer(final IRoomData room) {
        this.roomData = room;
        this.checkEntry = true;
        this.canMute = false;
        this.isLoading = true;
        this.skipAuth = false;
    }

    @Override
    public short getId() {
        return Composers.GetGuestRoomResultMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        RoomWriter.entryData(this.roomData, msg, this.isLoading, this.checkEntry, this.skipAuth, this.canMute);
    }
}
