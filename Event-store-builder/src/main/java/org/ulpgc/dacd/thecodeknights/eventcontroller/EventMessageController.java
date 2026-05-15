package org.ulpgc.dacd.thecodeknights.eventcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.eventstore.EventStoreException;

import org.ulpgc.dacd.thecodeknights.eventstore.EventStore;


public class EventMessageController {

    private static final Logger logger = LoggerFactory.getLogger(EventMessageController.class);

    private final EventStore eventStore;

    public EventMessageController(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void handle(String topicName, String jsonEvent) {
        try {
            eventStore.save(topicName, jsonEvent);

            logger.info("Evento almacenado correctamente.");
            logger.info("Topic: {}", topicName);
            logger.info("{}", jsonEvent);

        } catch (EventStoreException e) {
            logger.error("Evento inválido o no almacenado: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado procesando evento: {}", e.getMessage());
        }
    }
}
