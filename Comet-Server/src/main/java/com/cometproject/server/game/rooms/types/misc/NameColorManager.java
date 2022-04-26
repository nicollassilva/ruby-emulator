package com.cometproject.server.game.rooms.types.misc;

import com.cometproject.server.storage.queries.chat.NameColorDao;

import java.util.ArrayList;

public class NameColorManager {
    private ArrayList<NameColor> colors;

    public NameColorManager() {
        this.load();
    }

    private void load() {
        this.colors = NameColorDao.getNameColorList();
    }

    public final ArrayList<NameColor> getColors() {
        return colors;
    }

    public NameColor getColor(String color) {
        for(NameColor color1: colors) {
            if(color1.getName().equalsIgnoreCase(color))
                return color1;
        }
        return null;
    }
}
