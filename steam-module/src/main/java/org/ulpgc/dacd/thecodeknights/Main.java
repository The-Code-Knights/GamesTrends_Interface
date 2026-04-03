package org.ulpgc.dacd.thecodeknights;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        SteamConsumer consumer = new SteamApiConsumer();
        SteamSerializerInterface serializer = new SQLiteSteamSerializer();

        SteamController controller = new SteamController(consumer, serializer);

        controller.execute();
        serializer.printAllGames(); //Para testear luego borrar
        //SteamScheduler scheduler = new SteamScheduler(controller);
        //scheduler.start(0, 1, TimeUnit.HOURS);
        // Testear que funciona.
        //scheduler.start(0, 55, TimeUnit.SECONDS);
    }
}

