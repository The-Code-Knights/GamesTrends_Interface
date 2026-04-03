package org.ulpgc.dacd.thecodeknights;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SteamScheduler {

    private final SteamController controller;
    private final ScheduledExecutorService scheduler;

    public SteamScheduler(SteamController controller) {
        this.controller = controller;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                controller.execute();
                System.out.println("Captura finalizada.");
            } catch (Exception e) {
                System.err.println("Error en el scheduler: " + e.getMessage());
            }
        }, initialDelay, period, unit);
    }
    //Por si en el proyecto es necesario parar
    public void stop() {
        scheduler.shutdown();
    }
}
