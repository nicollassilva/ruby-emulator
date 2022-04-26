package com.cometproject.server.game.fastfood.messages.outgoing;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.gamecenter.GameCenterInfo;
import com.cometproject.server.game.gamecenter.GameCenterManager;
import com.cometproject.server.game.snowwar.Game;
import com.cometproject.server.game.snowwar.GameManager;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.List;

public class OverrideGameCenterGameListComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.GameListMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(GameCenterManager.getInstance().getGamesList().size());

        for (final GameCenterInfo gameInfo : GameCenterManager.getInstance().getGamesList()) {
            msg.writeInt(gameInfo.getGameId());
            msg.writeString(gameInfo.getGameName());
            msg.writeString("68bbd2");
            msg.writeString("ffffff");
            msg.writeString(gameInfo.getGamePath());
            msg.writeString("");
        }
    }
}
