package org.ulpgc.dacd.thecodeknights.eventstore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.ulpgc.dacd.thecodeknights.eventmodel.EventData;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileEventStore implements EventStore {

    private final Gson gson;
    private final EventPathBuilder pathBuilder;

    public FileEventStore(EventPathBuilder pathBuilder) {
        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();

        this.pathBuilder = pathBuilder;
    }

    @Override
    public void save(String topicName, String jsonEvent) {
        EventData data = deserializeMetadata(jsonEvent);

        Path eventFilePath = pathBuilder.build(
                topicName,
                data.getSs(),
                data.getTs()
        );

        writeEvent(eventFilePath, jsonEvent);
    }

    private EventData deserializeMetadata(String jsonEvent) {
        try {
            EventData metadata = gson.fromJson(jsonEvent, EventData.class);

            if (metadata == null) {
                throw new EventStoreException("El evento JSON está vacío");
            }

            if (metadata.getTs() == null || metadata.getTs().isBlank()) {
                throw new EventStoreException("El evento no contiene el campo obligatorio ts");
            }

            if (metadata.getSs() == null || metadata.getSs().isBlank()) {
                throw new EventStoreException("El evento no contiene el campo obligatorio ss");
            }

            return metadata;

        } catch (JsonSyntaxException e) {
            throw new EventStoreException("El evento no tiene formato JSON válido", e);
        }
    }

    private void writeEvent(Path eventFilePath, String jsonEvent) {
        try {
            Path parentDirectory = eventFilePath.getParent();

            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            Files.writeString(
                    eventFilePath,
                    jsonEvent + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    CREATE,
                    APPEND
            );

        } catch (IOException e) {
            throw new EventStoreException("No se pudo escribir el evento en: " + eventFilePath, e);
        }
    }
}
