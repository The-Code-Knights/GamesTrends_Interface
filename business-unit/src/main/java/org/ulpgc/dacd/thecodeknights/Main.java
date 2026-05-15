package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.control.BusinessSubscriber;
import org.ulpgc.dacd.thecodeknights.control.EventRouter;
import org.ulpgc.dacd.thecodeknights.control.HistoricalEventLoader;
import org.ulpgc.dacd.thecodeknights.datamart.DatamartRepository;
import org.ulpgc.dacd.thecodeknights.ui.DashboardServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MILLIS = 3000;

    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            logger.error("Uso: java Main <brokerUrl> <steamTopic> <twitchTopic> <eventStorePath> <datamartPath> <port>");
            return;
        }

        String brokerUrl      = args[0];
        String steamTopic     = args[1];
        String twitchTopic    = args[2];
        String eventStorePath = args[3];
        String datamartPath   = args[4];
        int port              = Integer.parseInt(args[5]);

        DatamartRepository datamart = new DatamartRepository(datamartPath);
        EventRouter router = new EventRouter(datamart);

        logger.info("Cargando eventos históricos desde: {}", eventStorePath);
        datamart.beginBatch();
        new HistoricalEventLoader(Path.of(eventStorePath), router).load();
        datamart.endBatch();
        logger.info("Eventos históricos cargados en el datamart.");

        BusinessSubscriber steamSubscriber = new BusinessSubscriber(
                brokerUrl, steamTopic,
                "business-unit-steam", "business-steam-subscription",
                router
        );
        BusinessSubscriber twitchSubscriber = new BusinessSubscriber(
                brokerUrl, twitchTopic,
                "business-unit-twitch", "business-twitch-subscription",
                router
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            steamSubscriber.stop();
            twitchSubscriber.stop();
        }));

        startWithRetries(steamSubscriber);
        startWithRetries(twitchSubscriber);

        new DashboardServer(datamart, port).start();

        logger.info("Business Unit iniciada. Dashboard disponible en http://localhost:{}", port);
        Thread.currentThread().join();
    }

    private static void startWithRetries(BusinessSubscriber subscriber) throws Exception {
        int attempts = 0;
        while (true) {
            try {
                subscriber.start();
                return;
            } catch (Exception e) {
                attempts++;
                logger.error("No se pudo conectar con ActiveMQ. Intento {}/{}", attempts, MAX_RETRIES);
                logger.error("Detalle: {}", e.getMessage());
                if (attempts >= MAX_RETRIES) {
                    throw new IllegalStateException(
                            "No se pudo iniciar Business Unit tras varios intentos", e);
                }
                Thread.sleep(RETRY_DELAY_MILLIS);
            }
        }
    }
}
