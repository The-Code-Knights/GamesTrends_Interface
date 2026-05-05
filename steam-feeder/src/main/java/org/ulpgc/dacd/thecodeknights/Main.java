package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.controller.*;
import org.ulpgc.dacd.thecodeknights.event.SteamEventPipeline;
import org.ulpgc.dacd.thecodeknights.event.SteamEventPublisher;
import org.ulpgc.dacd.thecodeknights.event.SteamEventSerializer;
import org.ulpgc.dacd.thecodeknights.model.SteamParser;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws JMSException {
        //String inyect_URL = args[0];
        SteamHttpGestor httpClient = new SteamHttpGestor();
        SteamParser parser = new SteamParser();

        SteamRankConsumer rankConsumer = new SteamRankConsumer(httpClient);
        SteamGameNamesConsumer nameConsumer = new SteamGameNamesConsumer(httpClient, parser);
        SteamCurrentPlayerConsumer playerConsumer = new SteamCurrentPlayerConsumer(httpClient, parser);

        SteamConsumer consumer = new SteamApiConsumer(rankConsumer, parser, nameConsumer, playerConsumer);

        SteamEventPublisher publisher = new SteamEventPublisher();
        try {
            publisher.start();
        } catch (JMSException e) {
            System.err.println("No se pudo iniciar SteamEventPublisher. Comprueba que ActiveMQ esté activo en tcp://localhost:61616");
            System.err.println("Detalle: " + e.getMessage());
            return;
        }

        SteamEventSerializer serializer = new SteamEventSerializer();
        SteamEventPipeline pipeline = new SteamEventPipeline(publisher, serializer);

        SteamController controller = new SteamController(consumer, pipeline);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            controller.stop();
            publisher.stop();
        }));

        controller.start(0, 8, TimeUnit.HOURS);
    }
}