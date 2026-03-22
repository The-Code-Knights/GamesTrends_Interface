package org.ulpgc.dacd.thecodeknights;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigLoader {

    private static final Dotenv dotenv = Dotenv.load();

    public static String getToken() {
        return dotenv.get("TWITCH_TOKEN");
    }

    public static String getClientId() {
        return dotenv.get("TWITCH_CLIENT_ID");
    }
}
