package com.cometproject.server.game.rooms.types.components;

import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.types.Trade;

import java.util.ArrayList;
import java.util.List;


public class TradeComponent {
    private Room room;
    private final List<Trade> trades;

    public void dispose(){
        this.room = null;
        trades.clear();
    }
    public TradeComponent(Room room) {
        this.room = room;
        this.trades = new ArrayList<>();
    }

    public void add(Trade trade) {
        trade.setTradeComponent(this);

        this.trades.add(trade);
    }

    public Trade get(PlayerEntity client) {
        for (Trade trade : this.getTrades()) {
            if (trade.getUser1() == client || trade.getUser2() == client)
                return trade;
        }

        return null;
    }

    public void remove(Trade trade) {
        this.trades.remove(trade);
    }

    public synchronized List<Trade> getTrades() {
        return this.trades;
    }

    public Room getRoom() {
        return room;
    }
}
