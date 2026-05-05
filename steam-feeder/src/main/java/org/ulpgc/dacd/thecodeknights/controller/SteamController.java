package org.ulpgc.dacd.thecodeknights.controller;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;
import org.ulpgc.dacd.thecodeknights.event.*;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SteamController {
    private final SteamConsumer feeder;

    private final ScheduledExecutorService scheduler;
    private final SteamEventPipeline pipeline;


    public SteamController(SteamConsumer feeder, SteamEventPipeline pipeline) {
        this.feeder = feeder;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.pipeline = pipeline;
    }

    public void execute() {
        List<SteamGame> games = feeder.getGames();
        pipeline.process(games);
    }

    public void start(long initialDelay, long period, TimeUnit unit) {

        scheduler.scheduleAtFixedRate(() -> {
            try {
                execute();
                System.out.println("Eventos de Steam enviados.");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }, initialDelay, period, unit);
    }

    public void stop() {
        scheduler.shutdown();
        //pipeline.stop();
    }
}
