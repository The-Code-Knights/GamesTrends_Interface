package org.ulpgc.dacd.thecodeknights.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class HistoricalEventLoader {

    private final Path eventStorePath;
    private final EventRouter router;

    public HistoricalEventLoader(Path eventStorePath, EventRouter router) {
        this.eventStorePath = eventStorePath;
        this.router = router;
    }

    public void load() {
        if (!Files.exists(eventStorePath)) {
            System.out.println("Event store no encontrado en: " + eventStorePath);
            return;
        }
        try (Stream<Path> files = Files.walk(eventStorePath)) {
            files.filter(p -> p.toString().endsWith(".events"))
                 .collect(java.util.stream.Collectors.groupingBy(Path::getParent))
                 .values().stream()
                 .map(list -> list.stream().max(java.util.Comparator.naturalOrder()).orElseThrow())
                 .sorted()
                 .forEach(this::loadFile);
        } catch (IOException e) {
            System.err.println("Error recorriendo event store: " + e.getMessage());
        }
    }

    private void loadFile(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    router.route(line);
                }
            }
            System.out.println("Histórico cargado: " + file.getFileName());
        } catch (IOException e) {
            System.err.println("Error leyendo " + file + ": " + e.getMessage());
        }
    }
}
