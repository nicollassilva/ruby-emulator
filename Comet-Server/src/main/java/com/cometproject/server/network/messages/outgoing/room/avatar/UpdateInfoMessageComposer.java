package com.cometproject.server.network.messages.outgoing.room.avatar;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class UpdateInfoMessageComposer extends MessageComposer {

    private final int playerId;
    private final String figure;
    private final String gender;
    private final String motto;
    private final int achievementPoints;
    private final String banner;

    public UpdateInfoMessageComposer(final int playerId, final String figure, final String gender, final String motto, final int achievementPoints, final String banner) {
        this.playerId = playerId;
        this.figure = figure;
        this.gender = gender;
        this.motto = motto;
        this.achievementPoints = achievementPoints;
        this.banner = banner;
    }

    public UpdateInfoMessageComposer(RoomEntity entity) {
        this(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0, (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getBanner() : "");
    }

    public UpdateInfoMessageComposer(int id, RoomEntity entity) {
        this(id, entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0, ((entity instanceof PlayerEntity) ? ((PlayerEntity)entity).getPlayer().getData().getBanner() : ""));
    }

    @Override
    public short getId() {
        return Composers.UserChangeMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeString(figure);
        msg.writeString(gender.toLowerCase());
        msg.writeString(motto);
        msg.writeInt(achievementPoints);
        msg.writeString(banner);
    }

//    public static Composer compose(RoomEntity entity) {
//        return compose(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//    }
//
//    public static Composer compose(boolean isMe, RoomEntity entity) {
//        if (!isMe) {
//            return compose(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//        } else {
//            return compose(-1, entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//        }
//    }
}
