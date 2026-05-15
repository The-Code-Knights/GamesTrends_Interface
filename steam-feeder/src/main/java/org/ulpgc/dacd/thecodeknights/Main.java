package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.controller.*;

import org.ulpgc.dacd.thecodeknights.controller.provider.*;
import org.ulpgc.dacd.thecodeknights.controller.SteamController;
import org.ulpgc.dacd.thecodeknights.controller.store.SteamEventPublisher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JMSException {
        String brokerUrl = args[0];
        String topicName = args[1];
        SteamHttpGestor httpClient = new SteamHttpGestor();
        SteamParser parser = new SteamParser();

        SteamRankConsumer rankConsumer = new SteamRankConsumer(httpClient);
        SteamGameNamesConsumer nameConsumer = new SteamGameNamesConsumer(httpClient, parser);
        SteamCurrentPlayerConsumer playerConsumer = new SteamCurrentPlayerConsumer(httpClient, parser);

        SteamConsumer consumer = new SteamApiConsumer(rankConsumer, parser, nameConsumer, playerConsumer);

        SteamEventPublisher publisher = new SteamEventPublisher(brokerUrl, topicName);
        try {
            publisher.start();
        } catch (JMSException e) {
            logger.error("No se pudo iniciar SteamEventPublisher. Comprueba que ActiveMQ esté activo");
            logger.error("Detalle: {}", e.getMessage());
            return;
        }


        SteamController controller = new SteamController(consumer, publisher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            controller.stop();
            publisher.stop();
        }));

        controller.start(0, 1, TimeUnit.HOURS);
    }
}