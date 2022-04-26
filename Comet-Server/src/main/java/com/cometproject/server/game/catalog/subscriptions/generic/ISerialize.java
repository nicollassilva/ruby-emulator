package com.cometproject.server.game.catalog.subscriptions.generic;

import com.cometproject.api.networking.messages.IComposer;

public interface ISerialize {
    void serialize(IComposer message);
}
