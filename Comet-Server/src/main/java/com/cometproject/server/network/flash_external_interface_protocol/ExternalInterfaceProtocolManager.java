package com.cometproject.server.network.flash_external_interface_protocol;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.common.*;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.jukebox.*;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.tools.RoomBackgroundEditEvent;
import com.cometproject.server.network.flash_external_interface_protocol.utils.JsonFactory;

import java.util.HashMap;

public class ExternalInterfaceProtocolManager {
    private static ExternalInterfaceProtocolManager _instance;
    static {
        try {
            _instance = new ExternalInterfaceProtocolManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final HashMap<String, Class<? extends IncomingExternalInterfaceMessage>> _incomingMessages;

    public ExternalInterfaceProtocolManager() {
        this._incomingMessages = new HashMap<>();
        initializeMessages();
    }

    public void initializeMessages() {
        this.registerMessage("move_avatar", MoveAvatarEvent.class);
        this.registerMessage("request_credits", RequestCreditsEvent.class);
        this.registerMessage("spin_slot_machine", RequestSpinSlotMachineEvent.class);
        this.registerMessage("flash_external_interface", OperationFUEvent.class);
        this.registerMessage("add_song", AddSongEvent.class);
        this.registerMessage("next_song", NextSongEvent.class);
        this.registerMessage("prev_song", PreviousSongEvent.class);
        this.registerMessage("play_stop", PlayStopEvent.class);
        this.registerMessage("remove_song", RemoveSongEvent.class);
        this.registerMessage("song_ended", SongEndedEvent.class);
        this.registerMessage("edit_tv", EditTVEvent.class);
        this.registerMessage("edit_bg", RoomBackgroundEditEvent.class);
    }

    public void registerMessage(String key, Class<? extends IncomingExternalInterfaceMessage> message) {
        this._incomingMessages.put(key, message);
    }

    public HashMap<String, Class<? extends IncomingExternalInterfaceMessage>> getIncomingMessages() {
        return this._incomingMessages;
    }

    public static ExternalInterfaceProtocolManager getInstance(){
        if (_instance == null) {
            _instance = new ExternalInterfaceProtocolManager();
        }
        return _instance;
    }

    public void OnMessage(String jsonPayload, Session sender) {
        try {
            IncomingExternalInterfaceMessage.JSONIncomingEvent heading = JsonFactory.getInstance().fromJson(jsonPayload, IncomingExternalInterfaceMessage.JSONIncomingEvent.class);
            Class<? extends IncomingExternalInterfaceMessage> message = ExternalInterfaceProtocolManager.getInstance().getIncomingMessages().get(heading.header);
            IncomingExternalInterfaceMessage webEvent = message.getDeclaredConstructor().newInstance();
            webEvent.handle(sender, JsonFactory.getInstance().fromJson(heading.data.toString(), webEvent.type));
        } catch(Exception e) {
            Comet.getServer().getLogger().debug("unknown message: " + jsonPayload);
        }
    }

    public void Dispose() {
        _incomingMessages.clear();
        _instance = null;
    }
}
