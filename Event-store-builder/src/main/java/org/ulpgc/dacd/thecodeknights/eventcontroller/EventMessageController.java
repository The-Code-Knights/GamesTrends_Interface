package org.ulpgc.dacd.thecodeknights.eventcontroller;

import org.ulpgc.dacd.thecodeknights.eventstore.EventStoreException;

import org.ulpgc.dacd.thecodeknights.eventstore.EventStore;


public class EventMessageController {

    private final EventStore eventStore;

    public EventMessageController(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void handle(String topicName, String jsonEvent) {
        try {
            eventStore.save(topicName, jsonEvent);

            System.out.println("Evento almacenado correctamente.");
            System.out.println("Topic: " + topicName);
            System.out.println(jsonEvent);

        } catch (EventStoreException e) {
            System.err.println("Evento inválido o no almacenado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado procesando evento: " + e.getMessage());
        }
    }
}
