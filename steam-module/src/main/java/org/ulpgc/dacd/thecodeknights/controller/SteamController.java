package org.ulpgc.dacd.thecodeknights.controller;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SteamController {
    private final SteamConsumer feeder;
    private final SteamStore serializer;
    private final ScheduledExecutorService scheduler;
    public SteamController(SteamConsumer feeder, SteamStore serializer) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void execute() {
        List<SteamGame> games = feeder.getGames();
        serializer.save(games);
    }
    public void start(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
                System.out.println("Captura finalizada.");
            } catch (Exception e) {
                System.err.println("Error en el scheduler: " + e.getMessage());
            }
        }, initialDelay, period, unit);
    }
    //La idea es usarlo cuando comencemos la recoleccion de datos
    public void stop() {
        scheduler.shutdown();
    }

}
