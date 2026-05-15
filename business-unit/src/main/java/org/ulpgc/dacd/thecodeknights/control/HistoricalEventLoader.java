package org.ulpgc.dacd.thecodeknights.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class HistoricalEventLoader {

    private static final Logger logger = LoggerFactory.getLogger(HistoricalEventLoader.class);

    private final Path eventStorePath;
    private final EventRouter router;

    public HistoricalEventLoader(Path eventStorePath, EventRouter router) {
        this.eventStorePath = eventStorePath;
        this.router = router;
    }

    public void load() {
        if (!Files.exists(eventStorePath)) {
            logger.warn("Event store no encontrado en: {}", eventStorePath);
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
            logger.error("Error recorriendo event store: {}", e.getMessage());
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
            logger.info("Histórico cargado: {}", file.getFileName());
        } catch (IOException e) {
            logger.error("Error leyendo {}: {}", file, e.getMessage());
        }
    }
}
