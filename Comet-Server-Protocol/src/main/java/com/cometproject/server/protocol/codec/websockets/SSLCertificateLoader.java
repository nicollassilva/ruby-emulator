package com.cometproject.server.protocol.codec.websockets;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SSLCertificateLoader {
    private static final String filePath = "ssl";
    private static final Logger logger = LogManager.getLogger(SSLCertificateLoader.class.getName());

    public static SslContext getContext() {
        SslContext context;

        try {
            context = SslContextBuilder.forServer(new File( filePath + File.separator + "cert.pem" ), new File( filePath + File.separator + "privkey.pem" )).build();
        } catch (Exception e) {
            logger.info("Unable to load ssl: "+ e.getMessage());
            context = null;
        }
        return context;
    }
}
