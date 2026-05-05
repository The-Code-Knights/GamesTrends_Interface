package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.eventcontroller.EventMessageController;
import org.ulpgc.dacd.thecodeknights.eventstore.EventPathBuilder;
import org.ulpgc.dacd.thecodeknights.eventstore.EventStore;
import org.ulpgc.dacd.thecodeknights.eventstore.FileEventStore;
import org.ulpgc.dacd.thecodeknights.subscriber.ActiveEventSubscriber;
import org.ulpgc.dacd.thecodeknights.subscriber.EventSubscriber;

import java.nio.file.Path;

public class Main {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String STEAM_TOPIC = "steam.games";

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MILLIS = 3000;

    public static void main(String[] args) throws Exception {
        EventPathBuilder pathBuilder = new EventPathBuilder(Path.of("eventstore"));
        EventStore eventStore = new FileEventStore(pathBuilder);
        EventMessageController controller = new EventMessageController(eventStore);

        EventSubscriber steamSubscriber = new ActiveEventSubscriber(
                BROKER_URL,
                STEAM_TOPIC,
                "event-store-builder",
                "steam-games-subscription",
                controller
        );

        Runtime.getRuntime().addShutdownHook(new Thread(steamSubscriber::stop));

        startWithRetries(steamSubscriber);

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
