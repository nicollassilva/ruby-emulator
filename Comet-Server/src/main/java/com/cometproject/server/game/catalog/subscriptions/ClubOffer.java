package com.cometproject.server.game.catalog.subscriptions;

import com.cometproject.api.game.catalog.types.subscriptions.IClubOffer;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.catalog.subscriptions.generic.ISerialize;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

public class ClubOffer implements ISerialize, IClubOffer {
    private final int id;
    private final String name;
    private final int days;
    private final int credits;
    private final int points;
    private final int pointsType;
    private final boolean vip;
    private final boolean deal;

    public ClubOffer(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.days = set.getInt("days");
        this.credits = set.getInt("credits");
        this.points = set.getInt("points");
        this.pointsType = set.getInt("points_type");
        this.vip = set.getString("type").equalsIgnoreCase("vip");
        this.deal = set.getString("deal").equals("1");
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getDays() {
        return this.days;
    }

    public int getCredits() {
        return this.credits;
    }

    public int getPoints() {
        return this.points;
    }

    public int getPointsType() {
        return this.pointsType;
    }

    public boolean isVip() {
        return this.vip;
    }

    public boolean isDeal() {
        return this.deal;
    }

    @Override
    public void serialize(IComposer message) {
        serialize(message, (int) Comet.getTime());
    }

    public void serialize(IComposer message, int hcExpireTimestamp) {
        hcExpireTimestamp = Math.max((int) Comet.getTime(), hcExpireTimestamp);

        message.writeInt(this.id);
        message.writeString(this.name);
        message.writeBoolean(false);
        message.writeInt(this.credits);
        message.writeInt(this.points);
        message.writeInt(this.pointsType);
        message.writeBoolean(this.vip);

        long seconds = this.days * 86400L;

        long secondsTotal = seconds;

        int totalYears = (int) Math.floor((int) seconds / (86400.0 * 31 * 12));
        seconds -= totalYears * (86400 * 31 * 12);

        int totalMonths = (int) Math.floor((int) seconds / (86400.0 * 31));
        seconds -= totalMonths * (86400 * 31);

        int totalDays = (int) Math.floor((int) seconds / 86400.0);
        seconds -= totalDays * 86400L;

        message.writeInt((int) secondsTotal / 86400 / 31);
        message.writeInt((int) seconds);
        message.writeBoolean(false);
        message.writeInt((int) seconds);

        hcExpireTimestamp += secondsTotal;

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(hcExpireTimestamp * 1000L);

        message.writeInt(cal.get(Calendar.YEAR));
        message.writeInt(cal.get(Calendar.MONTH) + 1);
        message.writeInt(cal.get(Calendar.DAY_OF_MONTH));
    }
}
