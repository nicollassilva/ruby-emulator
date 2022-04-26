package com.cometproject.server.network.messages.outgoing.messenger;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class FriendToolbarNotificationMessageComposer extends MessageComposer {
    public final static int INSTANT_MESSAGE = -1;
    public final static int ROOM_EVENT = 0;
    public final static int ACHIEVEMENT_COMPLETED = 1;
    public final static int QUEST_COMPLETED = 2;
    public final static int IS_PLAYING_GAME = 3;
    public final static int FINISHED_GAME = 4;
    public final static int INVITE_TO_PLAY_GAME = 5;

    private final int toId;
    private final int type;
    private final String data;

    public FriendToolbarNotificationMessageComposer(final int toId, final int type, final String data) {
        this.type = type;
        this.toId = toId;
        this.data = data;
    }

    @Override
    public short getId() {
        return Composers.FriendToolbarNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.toId + "");
        msg.writeInt(this.type);
        msg.writeString(this.data);
            /*_-1xC[3] = "${messenger.error.receivermuted}";
            _-1xC[4] = "${messenger.error.sendermuted}";
            _-1xC[5] = "${messenger.error.offline}";
            _-1xC[6] = "${messenger.error.notfriend}";
            _-1xC[7] = "${messenger.error.busy}";
            _-1xC[8] = "${messenger.error.receiverhasnochat}";
            _-1xC[9] = "${messenger.error.senderhasnochat}";
            _-1xC[10] = "${messenger.error.offline_failed}";*/

    }
}
