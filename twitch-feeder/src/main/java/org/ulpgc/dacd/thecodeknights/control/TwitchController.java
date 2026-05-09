package org.ulpgc.dacd.thecodeknights.control;

import org.ulpgc.dacd.thecodeknights.control.provider.TwitchConsumer;
import org.ulpgc.dacd.thecodeknights.control.store.TwitchEventPublisher;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchController {

    private final TwitchConsumer consumer;
    private final TwitchEventPublisher publisher;
    private final ScheduledExecutorService scheduler;

    public TwitchController(TwitchConsumer consumer, TwitchEventPublisher publisher) {
        this.consumer = consumer;
        this.publisher = publisher;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
            } catch (Exception ignored) {}
        }, initialDelay, period, unit);
    }

    public void execute() {
        try {
            List<TwitchEvent> events = consumer.fetchEvents(10000);
            publisher.publish(events);
        } catch (IOException ignored) {}
        catch (Exception ignored) {}
    }

    public void stop() {
        scheduler.shutdown();
    }
}
