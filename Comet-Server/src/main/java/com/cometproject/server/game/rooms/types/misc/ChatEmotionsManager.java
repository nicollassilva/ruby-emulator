package com.cometproject.server.game.rooms.types.misc;

import com.cometproject.server.game.rooms.RoomManager;

import java.util.HashMap;
import java.util.Map;


public class ChatEmotionsManager {
    private final Map<String, ChatEmotion> emotions;

    public ChatEmotionsManager() {
        emotions = new HashMap<String, ChatEmotion>() {{
            put(":)", ChatEmotion.SMILE);
            put(";)", ChatEmotion.SMILE);
            put(":]", ChatEmotion.SMILE);
            put(";]", ChatEmotion.SMILE);
            put("=)", ChatEmotion.SMILE);
            put("=]", ChatEmotion.SMILE);
            put(":-)", ChatEmotion.SMILE);
            put("Comet", ChatEmotion.SMILE);
            put("Dann", ChatEmotion.SMILE);
            put("Leon", ChatEmotion.SMILE);
            put("Endritt", ChatEmotion.SMILE);
            put("Skeletor", ChatEmotion.SMILE);

            put(">:(", ChatEmotion.ANGRY);
            put(">:[", ChatEmotion.ANGRY);
            put(">;[", ChatEmotion.ANGRY);
            put(">;(", ChatEmotion.ANGRY);
            put(">=(", ChatEmotion.ANGRY);

            put(":o", ChatEmotion.SHOCKED);
            put(";o", ChatEmotion.SHOCKED);
            put(">;o", ChatEmotion.SHOCKED);
            put(">:o", ChatEmotion.SHOCKED);
            put(">=o", ChatEmotion.SHOCKED);
            put("=o", ChatEmotion.SHOCKED);

            put(";'(", ChatEmotion.SAD);
            put(";[", ChatEmotion.SAD);
            put(":[", ChatEmotion.SAD);
            put(";(", ChatEmotion.SAD);
            put("=(", ChatEmotion.SAD);
            put("='(", ChatEmotion.SAD);
            put(":(", ChatEmotion.SAD);
            put(":-(", ChatEmotion.SAD);

            put(";D", ChatEmotion.LAUGH);
            put(":D", ChatEmotion.LAUGH);
            put(":L", ChatEmotion.LAUGH);
        }};

        RoomManager.log.info("Loaded " + this.emotions.size() + " chat emotions");
    }

    public ChatEmotion getEmotion(String message) {
        for (Map.Entry<String, ChatEmotion> emotion : emotions.entrySet()) {
            if (message.toLowerCase().contains(emotion.getKey().toLowerCase())) {
                return emotion.getValue();
            }
        }
        return ChatEmotion.NONE;
    }
}
