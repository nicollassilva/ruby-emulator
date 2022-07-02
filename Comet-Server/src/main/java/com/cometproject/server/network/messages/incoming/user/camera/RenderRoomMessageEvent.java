package com.cometproject.server.network.messages.incoming.user.camera;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.composers.camera.PhotoPreviewMessageComposer;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import javax.imageio.ImageIO;

public class RenderRoomMessageEvent
        implements Event {
    private static final byte[] signature = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int length = msg.readInt();
        final byte[] payload = msg.readBytes(length);
        final String photoName = UUID.randomUUID().toString();

        final String URL2 = photoName + ".png";
        final String URL_small = photoName + "_small.png";
        client.getPlayer().setLastPhoto(URL2);

        if (isPngFile(payload)) {
            try {
                final ByteBuf test = Unpooled.copiedBuffer(payload);
                final BufferedImage image = ImageIO.read(new ByteBufInputStream(test));

                ImageIO.write(image, "png", new File(CometSettings.cameraUploadUrl + URL2));
                ImageIO.write(image, "png", new File(CometSettings.cameraUploadUrl + URL_small));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }

            client.send(new PhotoPreviewMessageComposer(URL2));
        }
    }

    public static boolean isPngFile(byte[] file) {
        return Arrays.equals(Arrays.copyOfRange(file, 0, 8), signature);
    }
}

