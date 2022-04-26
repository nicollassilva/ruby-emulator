package com.cometproject.server.network.flash_external_interface_protocol.incoming.common;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.incoming.IncomingExternalInterfaceMessage;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.common.SpinResultComposer;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.common.UpdateCreditsComposer;
import com.cometproject.server.tasks.CometThreadManager;
import com.cometproject.server.utilities.RandomUtil;

import java.util.concurrent.TimeUnit;

public class RequestSpinSlotMachineEvent extends IncomingExternalInterfaceMessage<RequestSpinSlotMachineEvent.JSONRequestSpinSlotMachineEvent> {
    private static final int LEMON = 0;
    private static final int MELON = 1;
    private static final int GRAPES = 2;
    private static final int CHERRY = 3;
    private static final int BAR = 4;

    public RequestSpinSlotMachineEvent() {
        super(JSONRequestSpinSlotMachineEvent.class);
    }

    @Override
    public void handle(Session client, JSONRequestSpinSlotMachineEvent message) {
        Room room = client.getPlayer().getEntity().getRoom();
        if(room == null) return;
        RoomItemFloor item = room.getItems().getFloorItem(message.itemId);
        if(item == null) return;

        if(message.bet <= 0 || message.bet > client.getPlayer().getData().getCredits())
            return;
        client.getPlayer().getData().decreaseCredits(message.bet);
        client.send(client.getPlayer().composeCreditBalance());
        room.getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("spin.slots.machine", "* Bets %amount% on Slots Machine *").replace("%amount%", message.bet + ""), ChatEmotion.NONE, 1));
        client.send(new JavascriptCallbackMessageComposer(new UpdateCreditsComposer(client.getPlayer().getData().getCredits())));
        int result1 = RandomUtil.getRandomInt(LEMON, BAR);
        int result2 = RandomUtil.getRandomInt(LEMON, BAR);
        int result3 = RandomUtil.getRandomInt(LEMON, BAR);

        int amountWon = 0;
        boolean won = false;
        if(result1 == result2 && result2 == result3) {
            won = true;
            switch (result1) {
                case LEMON:
                    amountWon = 5 * message.bet;
                    break;
                case MELON:
                    amountWon = 6 * message.bet;
                    break;
                case GRAPES:
                    amountWon = 10 * message.bet;
                    break;
                case CHERRY:
                    amountWon = 15 * message.bet;
                    break;
                case BAR:
                    amountWon = 20 * message.bet;
                    break;
            }
            client.getPlayer().getData().increaseCredits(amountWon);
        }
        else if(result1 == BAR && result2 == BAR) {
            won = true;
            amountWon = 4 * message.bet;
            client.getPlayer().getData().increaseCredits(amountWon);
        }
        else if(result1 == CHERRY && result2 == CHERRY) {
            won = true;
            amountWon = 3 * message.bet;
            client.getPlayer().getData().increaseCredits(amountWon);
        }
        else if(result1 == CHERRY) {
            won = true;
            amountWon = 2 * message.bet;
            client.getPlayer().getData().increaseCredits(amountWon);
        }
        SpinResultComposer resultComposer = new SpinResultComposer(result1, result2, result3, won, amountWon);
        client.send(new JavascriptCallbackMessageComposer(resultComposer));
        final int finalAmount = amountWon;
        CometThreadManager.getInstance().executeSchedule(() -> room.getEntities().broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.getOrDefault("slot.machines.won", "* Won %amount% in Slots Machine *").replace("%amount%", finalAmount + ""), ChatEmotion.NONE, 1)), 5, TimeUnit.SECONDS);
    }

    static class JSONRequestSpinSlotMachineEvent {
        int itemId;
        int bet;
    }
}
