package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.eventcontroller.EventMessageController;
import org.ulpgc.dacd.thecodeknights.eventstore.EventPathBuilder;
import org.ulpgc.dacd.thecodeknights.eventstore.EventStore;
import org.ulpgc.dacd.thecodeknights.eventstore.FileEventStore;
import org.ulpgc.dacd.thecodeknights.subscriber.ActiveEventSubscriber;
import org.ulpgc.dacd.thecodeknights.subscriber.EventSubscriber;

import java.nio.file.Path;

public class Main {

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MILLIS = 3000;

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Uso: java Main <brokerUrl> <steamTopic> <twitchTopic>");
            return;
        }

        String brokerUrl = args[0];
        String steamTopic = args[1];
        String twitchTopic = args[2];

        EventPathBuilder pathBuilder = new EventPathBuilder(Path.of("eventstore"));
        EventStore eventStore = new FileEventStore(pathBuilder);
        EventMessageController controller = new EventMessageController(eventStore);

        EventSubscriber steamSubscriber = new ActiveEventSubscriber(
                brokerUrl,
                steamTopic,
                "event-store-builder-steam",
                "steam-games-subscription",
                controller
        );

        EventSubscriber twitchSubscriber = new ActiveEventSubscriber(
                brokerUrl,
                twitchTopic,
                "event-store-builder-twitch",
                "twitch-streams-subscription",
                controller
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            steamSubscriber.stop();
            twitchSubscriber.stop();
        }));

        startWithRetries(steamSubscriber);
        startWithRetries(twitchSubscriber);

        System.out.println("Event Store Builder ejecutándose. Pulsa Ctrl+C para detener.");

        Thread.currentThread().join();
    }

    private static void startWithRetries(EventSubscriber subscriber) throws Exception {
        int attempts = 0;

        while (true) {
            try {
                subscriber.start();
                return;
            } catch (Exception e) {
                attempts++;

                System.err.println("No se pudo conectar con ActiveMQ. Intento "
                        + attempts + "/" + MAX_RETRIES);
                System.err.println("Detalle: " + e.getMessage());

                if (attempts >= MAX_RETRIES) {
                    throw new IllegalStateException(
                            "No se pudo iniciar Event Store Builder tras varios intentos",
                            e
                    );
                }

                Thread.sleep(RETRY_DELAY_MILLIS);
            }
        }
    }
}
