package org.ulpgc.dacd.thecodeknights.eventstore;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class EventPathBuilder {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final Path baseDirectory;

    public EventPathBuilder(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public Path build(String topicName, String source, String timestamp) {
        validate(topicName, source, timestamp);

        String day = extractDay(timestamp);

        return baseDirectory
                .resolve(regexPattern(topicName))
                .resolve(regexPattern(source))
                .resolve(day + ".events");
    }

    private String extractDay(String timestamp) {
        try {
            Instant instant = Instant.parse(timestamp);
            return DATE_FORMATTER.format(instant);
        } catch (Exception e) {
            throw new EventStoreException("Timestamp inválido: " + timestamp, e);
        }
    }

    private void validate(String topicName, String source, String timestamp) {
        if (topicName == null || topicName.isBlank()) {
            throw new EventStoreException("El topic no puede estar vacío");
        }

        if (source == null || source.isBlank()) {
            throw new EventStoreException("El campo ss no puede estar vacío");
        }

        if (timestamp == null || timestamp.isBlank()) {
            throw new EventStoreException("El campo ts no puede estar vacío");
        }
    }

    private String regexPattern(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
