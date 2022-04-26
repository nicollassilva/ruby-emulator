package com.cometproject.server.network.messages.outgoing.misc;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.utils.JsonFactory;

public class JavascriptCallbackMessageComposer extends OpenLinkMessageComposer{
    private final OutgoingExternalInterfaceMessage message;

    public JavascriptCallbackMessageComposer(OutgoingExternalInterfaceMessage message) {
        super("");
        this.message = message;
    }

    @Override
    public void compose(IComposer msg) {
        //replace the / char so the string doesnt get cutoff by the swf
        String jsonMessage = JsonFactory.getInstance().toJson(message).replace("/", "&#47;");
        msg.writeString("habblet/open/" + jsonMessage);
    }
}
