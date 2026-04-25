package org.ulpgc.dacd.thecodeknights.control;

import org.ulpgc.dacd.thecodeknights.model.Stream;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchController {

    private final TwitchConsumer consumer;
    private final TwitchStore store;
    private final ScheduledExecutorService scheduler;

    public TwitchController(TwitchConsumer consumer, TwitchStore store) {
        this.consumer = consumer;
        this.store = store;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
                System.out.println("Captura de Twitch finalizada.");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }, initialDelay, period, unit);
    }

    public void execute() {
        try {
            List<Stream> streams = consumer.fetchStreams(10000);
            store.save(streams);
        } catch (IOException e) {
            System.err.println("Error fetching streams: " + e.getMessage());
        }
        System.out.println("API calls realizadas: " + consumer.getApiCallCount());
    }

    public void stop() {
        scheduler.shutdown();
    }
}