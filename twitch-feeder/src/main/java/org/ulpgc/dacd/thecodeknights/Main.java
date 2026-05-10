package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.control.TwitchController;
import org.ulpgc.dacd.thecodeknights.control.TwitchParser;
import org.ulpgc.dacd.thecodeknights.control.provider.GameNameCache;
import org.ulpgc.dacd.thecodeknights.control.provider.TwitchApiConsumer;
import org.ulpgc.dacd.thecodeknights.control.provider.TwitchConsumer;
import org.ulpgc.dacd.thecodeknights.control.store.TwitchEventPublisher;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        if (args.length < 5) {
            System.err.println("Uso: java Main <TwitchToken> <TwitchClientId> <brokerUrl> <TwitchTopicName> <gameNameCacheDb>");
            return;
        }

        String token = args[0];
        String clientId = args[1];
        String brokerUrl = args[2];
        String topicName = args[3];
        String gameNameCacheDb = args[4];

        TwitchParser parser = new TwitchParser();
        GameNameCache gameNameCache = new GameNameCache(gameNameCacheDb);
        TwitchConsumer consumer = new TwitchApiConsumer(token, clientId, parser, gameNameCache);

        TwitchEventPublisher publisher = new TwitchEventPublisher(brokerUrl, topicName);

        try {
            publisher.start();
        } catch (JMSException e) {
            System.err.println("No se pudo iniciar TwitchEventPublisher");
            return;
        }

        TwitchController controller = new TwitchController(consumer, publisher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            controller.stop();
            publisher.stop();
        }));

        try {
            controller.start(0, 8, TimeUnit.HOURS);

            System.out.println("Ejecución en proceso...");

        } catch (Exception e) {
            System.err.println("Error durante la ejecución del Twitch Feeder:");
            System.err.println(e.getMessage());
        }
    }
}
