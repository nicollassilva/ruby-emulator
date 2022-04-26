package com.cometproject.server.network.flash_external_interface_protocol.utils;

import com.google.gson.Gson;

public class JsonFactory {
    private static final Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }
}