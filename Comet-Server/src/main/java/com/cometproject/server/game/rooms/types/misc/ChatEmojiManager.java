package com.cometproject.server.game.rooms.types.misc;

import com.cometproject.server.storage.queries.chat.ChatEmojiDao;

import java.util.ArrayList;

public class ChatEmojiManager {
    private ArrayList<ChatEmoji> emojis;

    public ChatEmojiManager() {
        this.load();
    }

    private void load() {
        this.emojis = ChatEmojiDao.getEmojiList();
    }

    public String getEmojiChat(String message, int userRank) {
        String result = "";
        for(ChatEmoji emoji : emojis) {
            if(emoji.getKeys().contains(message.toLowerCase()) && emoji.getMinRank() <= userRank) {
                result = emoji.getEmojiChat();
                break;
            }
        }
        return result;
    }
}
