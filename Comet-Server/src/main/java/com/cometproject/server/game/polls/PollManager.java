package com.cometproject.server.game.polls;

import com.cometproject.api.utilities.Initialisable;
import com.cometproject.server.game.polls.types.Poll;
import com.cometproject.server.game.polls.types.PollQuestion;
import com.cometproject.server.game.polls.types.questions.InfobusQuestion;
import com.cometproject.server.game.polls.types.questions.MultipleChoiceQuestion;
import com.cometproject.server.storage.queries.polls.PollDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PollManager implements Initialisable {
    private static PollManager pollManagerInstance;
    private static final Logger log = LogManager.getLogger(PollManager.class.getName());
    private final Map<Integer, Integer> roomIdToPollId;
    private Map<Integer, Poll> polls;

    public PollManager() {
        this.polls = new ConcurrentHashMap<>();
        this.roomIdToPollId = new ConcurrentHashMap<>();
    }

    public static PollManager getInstance() {
        if (pollManagerInstance == null) {
            pollManagerInstance = new PollManager();
        }

        return pollManagerInstance;
    }

    @Override
    public void initialize() {
        if (this.polls != null) {
            for (final Poll poll : this.polls.values()) {
                for (final PollQuestion pollQuestion : poll.getPollQuestions().values()) {
                    if (pollQuestion instanceof MultipleChoiceQuestion) {
                        ((MultipleChoiceQuestion) pollQuestion).getChoices().clear();
                    }
                }

                poll.getPollQuestions().clear();
            }

            this.polls.clear();
            this.roomIdToPollId.clear();
        }

        this.polls = PollDao.getAllPolls();

        for (final Poll poll : this.getPolls().values()) {
            if (!this.roomIdToPollId.containsKey(poll.getRoomId())) {
                this.roomIdToPollId.put(poll.getRoomId(), poll.getPollId());
            }
        }

        log.info("Loaded " + this.getPolls().size() + " poll(s)");
    }

    public boolean roomHasPoll(int roomId) {
        final Poll poll = this.getPollByRoomId(roomId);

        if(poll == null)
            return false;

        for (final Map.Entry<Integer, PollQuestion> pollQuestion : poll.getPollQuestions().entrySet()) {
            if(pollQuestion.getValue() instanceof InfobusQuestion)
                return false;
        }

        return this.roomIdToPollId.containsKey(roomId);
    }
    public Poll getPollByRoomId(int roomId) {
        if (!this.roomIdToPollId.containsKey(roomId)) {
            return null;
        }

        return this.getPollbyId(this.roomIdToPollId.get(roomId));
    }

    public Poll getPollbyId(int pollId) {
        return this.polls.get(pollId);
    }

    public Map<Integer, Poll> getPolls() {
        return polls;
    }
}
