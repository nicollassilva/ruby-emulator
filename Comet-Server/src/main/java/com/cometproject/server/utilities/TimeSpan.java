package com.cometproject.server.utilities;

public class TimeSpan {
    private final long difference;

    public TimeSpan(long start, long finish) {
        this.difference = finish - start;
    }

    public static String millisecondsToDate(long time) {

        int SECOND = 1000;
        int MINUTE = 60 * SECOND;
        int HOUR = 60 * MINUTE;
        int DAY = 24 * HOUR;

        StringBuilder text = new StringBuilder();
        if (time > DAY) {
            text.append(time / DAY).append("d ");
        } else if (time > HOUR) {
            text.append(time / HOUR).append("h ");
        } else if (time > MINUTE) {
            text.append(time / MINUTE).append("min ");
        } else if (time > SECOND) {
            text.append(time / SECOND).append("sec ");
        }

        return text.toString();
    }

    public long toSeconds() {
        return this.difference / 1000;
    }

    public long toMilliseconds() {
        return this.difference;
    }

    public long toMinutes() {
        return (this.difference / 1000) / 60;
    }

    public long toHours() {
        return ((this.difference / 1000) / 60) / 60;
    }

    public long toDays() {
        return (((this.difference / 1000) / 60) / 60) / 24;
    }

    public long toWeeks() {
        return ((((this.difference / 1000) / 60) / 60) / 24) / 7;
    }
}
