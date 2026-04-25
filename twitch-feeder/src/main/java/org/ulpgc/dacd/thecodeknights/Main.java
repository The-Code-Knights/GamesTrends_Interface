package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.control.*;
import org.ulpgc.dacd.thecodeknights.model.*;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Uso: <DB_URL> <TOKEN> <CLIENT_ID>");
            return;
        }

        String dbUrl = args[0];
        String token = args[1];
        String clientId= args[2];

        TwitchParser parser = new TwitchParser();
        TwitchConsumer consumer = new TwitchApiConsumer(token, clientId, parser);
        TwitchStore store = new SQLiteTwitchStore(dbUrl);
        TwitchController controller = new TwitchController(consumer, store);

        long initialDelay = 0;
        long period = 8;
        controller.start(initialDelay, period, TimeUnit.HOURS);
    }
}