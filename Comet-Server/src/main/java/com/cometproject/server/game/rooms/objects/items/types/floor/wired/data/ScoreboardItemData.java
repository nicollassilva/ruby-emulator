package com.cometproject.server.game.rooms.objects.items.types.floor.wired.data;

import com.cometproject.server.utilities.comparators.HighscoreComparator;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreboardItemData {
    private final static HighscoreComparator comparator = new HighscoreComparator();

    private long lastClear;
    private final CopyOnWriteArrayList<HighscoreEntry> entries;
    private final Map<String, Integer> points;

    public ScoreboardItemData(long lastClear, CopyOnWriteArrayList<HighscoreEntry> entries, Map<String, Integer> points) {
        this.lastClear = lastClear;
        this.entries = entries;

        this.points = points;
    }

    public List<HighscoreEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(List<String> users, int score) {
        this.entries.add(new HighscoreEntry(users, score));
    }

    public void addPoint(String identifier, int score) {
        if (!this.points.containsKey(identifier)) {
            this.points.put(identifier, score);
            return;
        }

        this.points.replace(identifier, this.points.get(identifier) + score);
    }

    public void addSingleEntry(String user, int score) {
        synchronized (this.entries) {
            List<String> users = new LinkedList<>();
            users.add(user);
            this.entries.add(new HighscoreEntry(users, score));

            Collections.sort(this.entries, comparator);
        }
    }

    public void sortScores() {
        this.entries.sort(comparator);
    }

    public HighscoreEntry getEntryByTeam(final List<String> users) {
        for (HighscoreEntry entry : this.entries) {
            if (entry.getUsers().size() == users.size()) {
                final List<String> team = Lists.newArrayList(entry.getUsers());

                team.removeAll(users);

                if (team.size() == 0) {
                    return entry;
                }
            }
        }

        return null;
    }

    public long getLastClear() {
        return lastClear;
    }

    public void setLastClear(long lastClear) {
        this.lastClear = lastClear;
    }

    public static class HighscoreEntry {
        private List<String> users;
        private int score;

        public HighscoreEntry(List<String> users, int score) {
            this.users = users;
            this.score = score;
        }

        public void incrementScore(int score) {
            this.score += score;
        }

        public void decreaseScore(int score) { this.score-= score; }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}