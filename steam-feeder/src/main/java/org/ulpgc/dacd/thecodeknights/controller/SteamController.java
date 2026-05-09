package org.ulpgc.dacd.thecodeknights.controller;

import org.ulpgc.dacd.thecodeknights.controller.provider.SteamConsumer;
import org.ulpgc.dacd.thecodeknights.controller.store.SteamEventPublisher;
import org.ulpgc.dacd.thecodeknights.model.SteamEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SteamController {

    private final SteamConsumer feeder;
    private final SteamEventPublisher publisher;
    private final ScheduledExecutorService scheduler;

    public SteamController(SteamConsumer feeder, SteamEventPublisher publisher) {
        this.feeder = feeder;
        this.publisher = publisher;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void execute() {
        List<SteamEvent> events = feeder.getEvents();
        publisher.publish(events);
    }

    public void start(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
                System.out.println("Eventos de Steam enviados.");
            } catch (Exception e) {
                System.err.println("Error ejecutando SteamController: " + e.getMessage());
            }
        }, initialDelay, period, unit);
    }

    public void stop() {
        scheduler.shutdown();
    }
}
