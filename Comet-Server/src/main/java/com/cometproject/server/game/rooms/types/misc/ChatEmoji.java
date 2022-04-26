package com.cometproject.server.game.rooms.types.misc;

import com.cometproject.api.config.CometSettings;

import java.util.ArrayList;

public class ChatEmoji {
    private final int id;
    private final int minRank;
    private final ArrayList<String> keys;

    public ChatEmoji(int id, int minRank, ArrayList<String> keys) {
        this.id = id;
        this.minRank = minRank;
        this.keys = keys;
    }

    public ArrayList<String> getKeys() {
        return keys;
    }

    public int getId() {
        return id;
    }

    public int getMinRank() {
        return minRank;
    }

    public String getEmojiImg() {
        return  CometSettings.emojiImagePath + "emoji" + this.id + ".png";
    }

    public String getEmojiChat() {
        return "<br><img src=\"" + getEmojiImg() + "\" height=\"27px\" width=\"27px\" /><br><br><br>";
    }
}
