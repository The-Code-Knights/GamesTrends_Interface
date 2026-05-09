package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.controller.*;

import org.ulpgc.dacd.thecodeknights.controller.provider.*;
import org.ulpgc.dacd.thecodeknights.controller.SteamController;
import org.ulpgc.dacd.thecodeknights.controller.store.SteamEventPublisher;


import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

public class Main {

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
            System.err.println("No se pudo iniciar SteamEventPublisher. Comprueba que ActiveMQ esté activo");
            System.err.println("Detalle: " + e.getMessage());
            return;
        }


        SteamController controller = new SteamController(consumer, publisher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            controller.stop();
            publisher.stop();
        }));

        controller.start(0, 8, TimeUnit.HOURS);
    }
}