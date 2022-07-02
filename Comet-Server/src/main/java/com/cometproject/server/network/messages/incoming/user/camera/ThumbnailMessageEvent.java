package com.cometproject.server.network.messages.incoming.user.camera;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.IRoomData;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.ThumbnailTakenMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.imageio.ImageIO;

public class ThumbnailMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getId() != client.getPlayer().getEntity().getRoom().getData().getOwnerId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        if (client.getPlayer().antiSpam("UpdateRoomThumbnail", 5)) {
            client.send(new NotificationMessageComposer("generic", "Por favor, aguarde 5 segundos para colocar um novo thumbnail."));
            return;
        }

        final int length = msg.readInt();
        final byte[] payload = msg.readBytes(length);
        final IRoomData roomData = client.getPlayer().getEntity().getRoom().getData();
        final String thumbnailName = UUID.randomUUID().toString();

        if (RenderRoomMessageEvent.isPngFile(payload)) {
            try {
                ByteBuf test = Unpooled.copiedBuffer(payload);
                BufferedImage image = ImageIO.read(new ByteBufInputStream(test));
                ImageIO.write(image, "png", new File(CometSettings.thumbnailUploadUrl + thumbnailName + ".png"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }

            if(!roomData.getThumbnail().isEmpty() && roomData.getThumbnail().startsWith("camera")) {
                final String[] oldThumbnailName = roomData.getThumbnail().split("/");

                if(oldThumbnailName.length > 0) {
                    final File thumbnailFile = new File(CometSettings.thumbnailUploadUrl + oldThumbnailName[oldThumbnailName.length - 1]);

                    if(thumbnailFile.exists()) {
                        try {
                            thumbnailFile.delete();
                        } catch (SecurityException ignored) {}
                    }
                }
            }

            roomData.setThumbnail("camera/thumbnails/" + thumbnailName + ".png");
            GameContext.getCurrent().getRoomService().saveRoomData(roomData);

            client.send(new RoomDataMessageComposer(roomData));
            client.send(new ThumbnailTakenMessageComposer());
        }
    }
}

