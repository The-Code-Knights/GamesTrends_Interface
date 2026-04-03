package org.ulpgc.dacd.thecodeknights;

import io.github.cdimascio.dotenv.Dotenv;

public class TwitchConfigProvider implements ConfigProviderInterface {

    private final Dotenv dotenv;

    public TwitchConfigProvider() {
        this.dotenv = Dotenv.load();
    }

    @Override
    public String getToken() {
        return dotenv.get("TWITCH_TOKEN");
    }

    @Override
    public String getClientId() {
        return dotenv.get("TWITCH_CLIENT_ID");
    }
}
